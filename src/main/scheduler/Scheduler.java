package main.scheduler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.common.ByteConverter;
import main.common.Constants;
import main.common.Direction;
import main.common.Instructions;
import main.common.Logger;
import main.common.PacketHandler;
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
			public String toString() {
				return "LISTENING";
			}
		},
		DELEGATING { // Sending instructions to an elevator
			public String toString() {
				return "DELEGATING";
			}
		},
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
		return elevatorRequests.toString();
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
			if (score >= bestScore) {
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
	private int getFloorDifference(int currentFloor, int startingFloor, Direction currentDirection, Direction destinationDirection, ElevatorState currentState) {
		
		int difference = currentFloor - startingFloor;
		
		switch (currentState) {
			// Priority from best to worst:
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
					return 1; // TODO bad score
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

		// start elevator agents
		Thread[] agentThreads = new Thread[Constants.NUM_CARS];
		for (int i = 0; i < agents.size(); i++) {
			agentThreads[i] = new Thread(agents.get(i));
			agentThreads[i].start();
		}

		while (true) {
			currState = SchedulerStates.LISTENING;
			logger.log("Current state: " + currState + "\n" + this);
			response = packetHandler.receive(); // TODO not really a response
			stringResponse = new String(response, StandardCharsets.UTF_8).substring(0, 2);
			if (stringResponse.equals("UP") || stringResponse.equals("DO")) {
				logger.log("Received Instruction");
				instruction = ByteConverter.byteArrayToInstructions(response);
				packetHandler.send(new byte[] { 0 });
				logger.log("Sent acknowledgement");

				logger.log("Finding optimal elevator...");
				currState = SchedulerStates.DELEGATING;
				logger.log("Current state: " + currState + "\n" + this);
				ElevatorAgent bestAgent = getBestElevator(instruction.getCurrentFloor(), instruction.getDirection());
				elevatorRequests.get(bestAgent.getId()).add(instruction);
				logger.log("Sent Instruction");
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
