package main.scheduler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import main.common.ByteConverter;
import main.common.Constants;
import main.common.Direction;
import main.common.Logger;
import main.common.PacketHandler;
import main.common.Input.Instructions;
import main.elevator.Elevator.ElevatorState;

/**
 * Class to represent the backend server to handle communication between
 * elevator components
 */
public class Scheduler {
	private static final int FLOOR_MANAGER_PORT = 50;
	/** logger instance to handle console logging */
	private Logger logger;
	/**
	 * ArrayList of Lists corresponding to each elevator's requests list
	 */
	private ArrayList<List<Instructions>> elevatorRequests;
	/**
	 * ArrayList to keep track of elevator agents
	 */
	private ArrayList<ElevatorAgent> agents;
	/**
	 * Variable to track the current state of scheduler
	 */
	private SchedulerStates currState = SchedulerStates.LISTENING;

	/**
	 * ENUM to represent the scheduler states
	 */
	public enum SchedulerStates {
		LISTENING { // Reading in instructions from floor
			public SchedulerStates nextState() {
				return DELEGATING;
			}
			
			public String toString() {
				return "LISTENING";
			}
		},
		DELEGATING { // Sending instructions to an elevator
			public SchedulerStates nextState() {
				return LISTENING;
			}
			public String toString() {
				return "DELEGATING";
			}
		};
		public abstract SchedulerStates nextState();
		
		public abstract String toString();
	};

	/** PacketHandler for dealing with UDP communication */
	private PacketHandler packetHandler;

	/**
	 * Default constructor
	 */
	public Scheduler(int portNum) {
		elevatorRequests = new ArrayList<>();
		agents = new ArrayList<>();
		for (int i = 0; i < Constants.NUM_CARS; i++) {
			List<Instructions> requests = Collections.synchronizedList(new ArrayList<Instructions>());
			elevatorRequests.add(requests);
			this.agents.add(new ElevatorAgent(i, requests, 1));
		}
		packetHandler = new PacketHandler(FLOOR_MANAGER_PORT, portNum, Constants.TIMEOUT);
		this.logger = new Logger("SCHED");
		logger.log("Starting...");
	}

	// getter for unit tests
	public SchedulerStates getState() {
		return currState;
	}

	/**
	 * Pretty string to show the scheduler's lists
	 * 
	 * @return string
	 */
	public String toString() {
		String output = "";
		for (int i = 0; i < elevatorRequests.size(); i++) {
			output += "Agent-" + i + ": " + elevatorRequests.get(i) + "\n";
		}
		return output;
	}

	/**
	 * Helper method to find the best elevator agent for a request.
	 * 
	 * @param startingFloor int - starting floor.
	 * @param direction Direction - direction the elevator will be moving.
	 * @return
	 */
	private ElevatorAgent getBestElevator(int startingFloor, Direction direction) {
		ArrayList<ElevatorAgent> bestAgents = new ArrayList<>();
		int bestScore = 0;
		for (ElevatorAgent agent : agents) {
			int score = getFloorDifference(startingFloor, agent.getCurrentFloor(), direction, agent.getCurrentDirection(), agent.getPreviousDirection(), agent.getCurrentState(), agent.getId());
			if (score > bestScore) {
				bestScore = score;
				bestAgents = new ArrayList<>();
				bestAgents.add(agent);
			} else if (score == bestScore) {
				bestAgents.add(agent);
			}
		}
		// may the odds be ever in your favour
		// let the hunger games begin
		logger.log("SCORE: " + bestScore + ", AGENTS: " + bestAgents);
		return bestAgents.get(ThreadLocalRandom.current().nextInt(0, bestAgents.size()));
	}

	/**
	 * This function assigns an elevator a score for handling a given request, where higher is better
	 * @param startingFloor the starting floor for the instruction
	 * @param currentFloor the current floor of the elevator
	 * @param destinationDirection the direction of the instruction
	 * @param currentDirection the current direction of the elevator
	 * @param previousDirection Direction - previous direction of the elevator if applicable
	 * @param id int - id of the elevator agent
	 * @return integer between 1 and 4 representing that elevator's score to handle a request, higher is better
	 */
	public int getFloorDifference(int startingFloor, int currentFloor, Direction destinationDirection, Direction currentDirection, Direction previousDirection, ElevatorState currentState, int id) {
		int difference = currentFloor - startingFloor;

		switch (currentState) {
			// Priorities from best to worst:
			// 1st - Idle elevators within a a distance of NUM_FLOORS/4
			// 2nd - Elevators that can serve the request as an intermediate request
			// 3rd - Idle elevators that are farther away
			// 4th - Any other elevator

			// If the elevator is idle
			default:
			case Idle:
				// catch elevators that haven't switched over to MOVING yet from their first request
				if (elevatorRequests.get(id).size() > 0) {
					return getFloorDifference(startingFloor, currentFloor, destinationDirection, elevatorRequests.get(id).get(0).getDirection(), previousDirection, ElevatorState.Moving, id);
				// Best case is an idle elevator that is close to the pickup floor
				} else if (Math.abs(difference) <= Math.ceil(Constants.NUM_FLOORS/3)) {
					return 5;
				}
				// Idle elevators that are far away are given a fair score
				return 3;
			// Elevator is currently moving
			case Moving:
				switch (currentDirection) {
					// catch elevators that are "arriving" and don't have a current direction
					case STOP:
						return getFloorDifference(startingFloor, currentFloor, destinationDirection, currentDirection, previousDirection, ElevatorState.Arriving, id);
					case UP:
						// if starting floor cannot be reached without changing direction
						if (difference > 0) {
							return 1;
						// if starting floor can be reached and heading in same direction, this is 
						// our second most favorable case
						} else if (currentDirection.equals(destinationDirection)) {
							if (Math.abs(difference) <= Math.ceil(Constants.NUM_FLOORS/4)) {
								return 4;
							} else {
								return 2;
							}
						}
						// if starting floor can be reached but needs changing direction
						return 1;
					case DOWN:
						// Elevator is moving down
						// if starting floor cannot be reached without changing direction
						if (difference < 0) {
							return 1;
						// if starting floor can be reached and heading in same direction
						} else if (currentDirection.equals(destinationDirection)) {
							if (Math.abs(difference) <= Math.ceil(Constants.NUM_FLOORS/4)) {
								return 4;
							} else {
								return 2;
							}
						}
						// if starting floor can be reached but needs changing direction
						return 1;
				}
			// Elevator is arriving at a floor
			case Arriving:
				switch (previousDirection) {
					// catch elevators that don't have a direction
					default:
					case STOP:
						// if there is a request, use the request's direction
						if (elevatorRequests.get(id).size() > 0) {
							return getFloorDifference(startingFloor, currentFloor, destinationDirection, elevatorRequests.get(id).get(0).getDirection(), null, ElevatorState.Moving, id);
						}
						// somehow the elevator has arrived, stopped, and was not previously moving in a direction (??)
						return 1;
					// pass in previous direction as the new current direction
					case UP:
					case DOWN:
						return getFloorDifference(startingFloor, currentFloor, destinationDirection, previousDirection, null, ElevatorState.Moving, id);
					}
		}
	}
	
	/**
	 * Getting Elevator agents
	 * @return elevator agents arraylist
	 */
	public ArrayList<ElevatorAgent> getAgents() {
		return agents;
	}

	/**
	 * Main run method for the scheduler thread
	 */
	public int run() {
		byte[] response;
		Instructions instruction;
		String stringResponse;
		long startTime = System.nanoTime();
		long endTime = System.nanoTime();
		boolean started = false;
		boolean lastRequestTimed = false;

		// start elevator agents
		Thread[] agentThreads = new Thread[Constants.NUM_CARS];
		for (int i = 0; i < agents.size(); i++) {
			agentThreads[i] = new Thread(agents.get(i));
			agentThreads[i].start();
		}

		// enter main infinite loop
		while (true) {
			// move scheduler to listening state
			currState = SchedulerStates.LISTENING;
			logger.log("Current state: " + currState);
			response = packetHandler.receiveTimeout(Constants.SCHEDULER_TIMEOUT); // TODO not really a response xd
			if (response == null) { // Assume that if response times out, that there will be no more requests
				if (started && !lastRequestTimed) {
					endTime = System.nanoTime();
					logger.log("Time from first request received until last request sent (s): " + ((endTime - startTime - (Constants.SCHEDULER_TIMEOUT * 1000000)) / Math.pow(10, 9)));
					lastRequestTimed = true;
				}
				continue;
			}
			if (!started) { // Start the timer when it receives the first request
				started = true;
				startTime = System.nanoTime();
			}
			stringResponse = new String(response, StandardCharsets.UTF_8).substring(0, 2);

			// check if input received is a valid instruction
			if (stringResponse.equals("UP") || stringResponse.equals("DO")) {
				// convert instruction bytes to instruction object
				instruction = ByteConverter.byteArrayToInstructions(response);
				logger.log("Received: " + instruction);
				// send acknowledgement
				packetHandler.send(new byte[] { 0 });
				logger.log("Sent acknowledgement");
				// enter delegating state and find optimal elevator
				logger.log("Finding optimal elevator...");
				currState = SchedulerStates.DELEGATING;
				// get best elevator to deal with this
				ElevatorAgent bestAgent = getBestElevator(instruction.getCurrentFloor(), instruction.getDirection());
				logger.log("Best agent found is " + bestAgent);
				// and add the instructions to their requests queue
				elevatorRequests.get(bestAgent.getId()).add(instruction);
				logger.log("Requests:\n" + this);
				endTime = System.nanoTime();

			} else if (response[0] == (byte)-1 ) { // Received failure request from agent
				ElevatorAgent removeAgent = null;
				int id = -1;
				// Pick agent to remove
				for (ElevatorAgent agent: agents) {
					if ((byte) agent.getId() == response[1]) {
						removeAgent = agent;
						id = removeAgent.getId();
						break;
					}
					
				}
				if (removeAgent == null) continue;
				
				// Remove agent from possible elevators to delegate
				agents.remove(removeAgent);
				
				if (agents.isEmpty()) {
					logger.log("No more agents available. Shutting down...");
					// Time when no more elevators available
					endTime = System.nanoTime();
					logger.log("Time from first request to elevators finishing requests or all broken down (s): " + ((endTime - startTime) / Math.pow(10, 9)));
					break;
				}
				
				// we remove all instructions that were to be served by this elevator if its motor breaks down
				elevatorRequests.get(id).clear();
				logger.log("Requests:\n" + this);
			}
		}
		return 0;
	}

	/**
	 * Main function
	 * @param args String[] - unused.
	 */
	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler(Constants.SCHEDULER_PORT);
		scheduler.run();
	}
}
