package main.scheduler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.common.ByteConverter;
import main.common.Constants;
import main.common.Direction;
import main.common.Logger;
import main.common.PacketHandler;
import main.common.Input.Instructions;
import main.elevator.Elevator.ElevatorState;
import main.elevator.ElevatorButton;
import main.floor.FloorButton;

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
		ElevatorAgent bestAgent = null;
		int bestScore = 0;
		for (ElevatorAgent agent : agents) {
			int score = getFloorDifference(agent.getCurrentFloor(), startingFloor, agent.getCurrentDirection(), direction, agent.getCurrentState());
			if (score > bestScore) {
				bestScore = score;
				bestAgent = agent;
			}
		}
		return bestAgent;
	}
	
	/**
	 * This function assigns an elevator a score for handling a given request, where higher is better
	 * @param currentFloor the current floor of the elevator
	 * @param startingFloor the starting floor for the instruction
	 * @param currentDirection the current direction of the elevator
	 * @param destinationDirection the direction of the instruction
	 * @return integer between 1 and 4 representing that elevator's score to handle a request, higher is better
	 */
	public int getFloorDifference(int currentFloor, int startingFloor, Direction currentDirection, Direction destinationDirection, ElevatorState currentState) {
		
		int difference = currentFloor - startingFloor;
		
		switch (currentState) {
			// Priorities from best to worst:
			// 1. Idle elevators within a a distance of NUM_FLOORS/4
			// 2. Elevators that can serve the request as an intermediate request
			// 3. Idle elevators that are farther away
			// 4. Any other elevator
			
			// If the elevator is idle
			case Idle:
				// Best case is an idle elevator that is close to the pickup floor
				if (difference <= Math.ceil(Constants.NUM_FLOORS/4)) {
					return 4;
				}
				// Idle elevators that are far away are given a fair score
				else {
					return 2;
				}
			// Elevator is currently moving
			case Moving:
				// Elevator is moving up
				if (currentDirection == Direction.UP) {
					// if starting floor cannot be reached without changing direction
					if (difference < 0) {
						return 1; 
					// if starting floor can be reached and heading in same direction, this is 
					// our second most favorable case
					} else if (currentDirection == destinationDirection) {
						return 3; 
					}
					// if starting floor can be reached but needs changing direction
					return 1; 
				}
				// Elevator is moving down
				// if starting floor cannot be reached without changing direction
				if (difference > 0) {
					return 1;
				// if starting floor can be reached and heading in same direction
				} else if (currentDirection == destinationDirection) {
					return 3;
				}
				// if starting floor can be reached but needs changing direction
				return 1; 
			
			// any elevator outside of the above cases is given a score of 2
			default:
				return 2;
		}
	}

	/**
	 * Main run method for the scheduler thread
	 */
	public void run() {
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
				logger.log("Received Instruction");
				instruction = ByteConverter.byteArrayToInstructions(response);
				// send acknowledgement
				packetHandler.send(new byte[] { 0 });
				logger.log("Sent acknowledgement");
				// enter delegating state and find optimal elevator
				logger.log("Finding optimal elevator...");
				currState = SchedulerStates.DELEGATING;
				// get best elevator to deal with this
				ElevatorAgent bestAgent = getBestElevator(instruction.getCurrentFloor(), instruction.getDirection());
				logger.log("Best agent found is ELEV-AGENT-" + bestAgent.getId());
				// and add the instructions to their requests queue
				elevatorRequests.get(bestAgent.getId()).add(instruction);
				logger.log("Sent Instruction to Elevator " + bestAgent.getId());
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
