/**
 * 
 */
package main.scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.common.Instructions;
import main.common.Logger;
import main.common.PacketHandler;
import main.elevator.Elevator.ElevatorState;
import main.common.Direction;
import main.common.Constants;

/**
 * A class to handle communication between the scheduler and elevator cars.
 */
public class ElevatorAgent implements Runnable {
	/**
	 * Keeps track of the associated Elevator's current state.
	 */
	private ElevatorState currentState;
	/**
	 * Keeps track of the associated Elevator's current direction.
	 */
	private Direction currentDirection;
	/**
	 * Keeps track of the associated Elevator's current floor.
	 */
	private int currentFloor;
	/**
	 * Associated elevator ID.
	 */
	private final int elevatorId;
	/**
	 * Requests specific to this elevator.
	 */
	private final List<Instructions> requests;
	/**
	 * PacketHandler instance for communication.
	 */
	private final PacketHandler packetHandler;
	/**
	 * Logger instance to log messages.
	 */
	private final Logger logger;
	
	/**
	 * Default constructor to initialize an elevator agent with a specific elevator id.
	 * 
	 * @param elevatorId
	 * @param requests
	 * @param currentFloor
	 */
	public ElevatorAgent(int elevatorId, List<Instructions> requests, int currentFloor) {
		this.currentState = ElevatorState.Idle;
		this.currentDirection = Direction.STOP;
		this.currentFloor = currentFloor;
		this.elevatorId = elevatorId;
		this.requests = requests;
		this.packetHandler = new PacketHandler(Constants.ELEVATOR_STARTING_PORT_NUMBER + elevatorId, Constants.ELEVATOR_AGENT_STARTING_PORT_NUMBER + elevatorId);
		this.logger = new Logger("ELEV-AGENT-" + elevatorId);
	}
	
	/**
	 * Getter for current elevator state.
	 * 
	 * @return ElevatorState - current state.
	 */
	public ElevatorState getCurrentState() { return currentState; }
	/**
	 * Getter for current elevator direction.
	 * 
	 * @return Direction - current direction.
	 */
	public Direction getCurrentDirection() { return currentDirection; }
	/**
	 * Getter for current elevator floor.
	 * 
	 * @return Floor - current floor.
	 */
	public int getCurrentFloor() { return currentFloor; }
	/**
	 * Getter for elevator ID.
	 * 
	 * @return int - associated elevator ID.
	 */
	public int getId() { return elevatorId; }
	
	/**
	 * Helper method to find and return the next best request.
	 * 
	 * @return Instructions
	 */
	private Instructions getNextRequest() {
		Instructions nextInstruction = requests.get(0);
		int nextInstructionIndex = 0;
		int minDifference = 100;
		// search through all current ongoing requests for this elevator
		for (int i = 0; i < requests.size(); i++) {
			// calculate the closest request to service
			Instructions currentInstruction = requests.get(i);
			// get the number of floors between the instruction and the floor the elevator is currently on
			int difference = currentInstruction.getCurrentFloor() - currentFloor;
			// if the current instruction is closer, we set it to be the next instruction
			if (Math.abs(difference) < minDifference) {
				minDifference = difference;
				nextInstruction = currentInstruction;
				nextInstructionIndex = i;
			}
		}
		// found best, remove it from list
		requests.remove(nextInstructionIndex);
		// calculate direction to move
		int destinationFloor = nextInstruction.getDestinationFloor();
		if (destinationFloor > currentFloor) {
			currentDirection = Direction.UP;
		} else if (destinationFloor < currentFloor) {
			currentDirection = Direction.DOWN;
		} else {
			currentDirection = Direction.STOP;
		}
		// update elevator state
		currentState = ElevatorState.Moving;
		return nextInstruction;
	}
	
	/**
	 * Helper method to find intermediate requests for the elevator to service as its moving.
	 * 
	 * @param instructions ArrayList<Instructions> - set of current instructions.
	 * @returns boolean - True if intermediate was added
	 */
	private boolean getIntermediateRequest(ArrayList<Instructions> instructions) {
		Instructions intermediate = null;
		int intermediateIndex = -1;
		for (int i = 0; i < requests.size(); i++) {
			Instructions ins = requests.get(i);
			// When we arrive at a floor, we look for instructions that start at the next floor in the direction we are headed
			int targetFloor = ins.getDirection() == Direction.UP ? ins.getCurrentFloor() - 1 : ins.getCurrentFloor() + 1;
			// If that is the case, it is added as a request for this elevator
			if (ins.getDirection() == currentDirection && targetFloor == currentFloor) {
				// add the request
				intermediate = ins;
				intermediateIndex = i;
				break;
			}
		}
		// if found, remove from original list and add to instructions
		if (intermediate != null) {
			requests.remove(intermediateIndex);
			instructions.add(intermediate);
			return true;
		}
		return false;
	}
	
	/**
	 * Helper method to get the elevator's next destination floor.
	 * 
	 * @param instructions ArrayList<Instructions> - set of current instructions.
	 */
	private int getNextDestinationFloor(ArrayList<Instructions> instructions) {
		int minDifference = 100, nextFloor = 1;
		// iterate through every instruction that should be served by this elevator
		for (Instructions ins : instructions) {
			int difference = 100;
			// candidate floor represents the floor that we will potentially go to next, initialize it to 0 if we are going down, the top floor
			// if we are going up, since we are trying to find the minimum distance to travel in the same direction we are already headed
			int candidateFloor = currentDirection == Direction.UP ? Constants.NUM_FLOORS - 1 : 0;
			if (currentDirection == Direction.UP) {
				// if the instructions pickup floor is greater than the elevator's current floor, we need to pick people up
				if (ins.getCurrentFloor() > currentFloor) {
					candidateFloor = ins.getCurrentFloor();
				// if the instructions destination floor is greater than the elevator's current floor, we need to drop people off
				} else {
					candidateFloor = ins.getDestinationFloor();
				}
				// vice versa for if we are going down
			} else {
				if (ins.getCurrentFloor() < currentFloor) {
					candidateFloor = ins.getCurrentFloor();
				} else {
					candidateFloor = ins.getDestinationFloor();
				}
			}
			
			// we compare the candidateFloor to the current minimum distance destination, and replace it if
			// candidate is more favorable
			difference = Math.abs(candidateFloor - currentFloor);
			if (difference < minDifference && difference != 0) {
				minDifference = difference;
				nextFloor = candidateFloor;
			}
		}
		return nextFloor;
	}

	/**
	 * Helper method to handle elevators processing a request.
	 * 
	 * @param ins Instructions - instructions to process.
	 */
	private void handleRequest(Instructions ins) {
		logger.log("Handling request: " + ins);
		// store instructions for this request
		ArrayList<Instructions> instructions = new ArrayList<>();
		instructions.add(ins);
		int currentDestination = ins.getCurrentFloor();

		// send first instructions to elevator
		byte[] sent = new byte[] { (byte) currentDestination };
		logger.log("Sending first instruction: " + Arrays.toString(sent));
		packetHandler.send(sent);

		// while elevator is moving
		while (currentState == ElevatorState.Moving) {
			// receive status updates from elevator
			byte[] received = packetHandler.receive();
			logger.log("Received: " + Arrays.toString(received));
			currentFloor = received[1];
			Direction direction = received[2] == 0 ? Direction.STOP : received[2] == 1 ? Direction.UP : Direction.DOWN;
			logger.log("Elevator is at floor " + currentFloor + ", heading " + direction);
			// check if intermediate requests can be dealt with here
			if (!requests.isEmpty()) {
				logger.log("Checking for intermediate requests...");
				if (getIntermediateRequest(instructions)) {
					logger.log("intermediate request added: " + instructions.toString());
					logger.log("Elevator is picking up passengers at floor " + currentFloor);
					sent = new byte[] { (byte) currentFloor };
					logger.log("Sending: " + Arrays.toString(sent));
					packetHandler.send(sent);
					System.out.println();
					continue;
				}
			}
			// check if elevator has stopped and reached a destination floor
			if (direction == Direction.STOP) {
				logger.log("Elevator stopped moving!");
				ArrayList<Integer> removedIndices = new ArrayList<>();
				logger.log("Currently servicing requests: " + instructions.toString());
				for (int i = 0; i < instructions.size(); i++) {
					// elevator reached pickup floor
					Instructions temp = instructions.get(i);
					if (currentFloor == temp.getCurrentFloor()) {
						logger.log("Elevator is picking up passengers at floor " + currentFloor + " to drop off at " + temp.getDestinationFloor());
					// elevator reached dropoff floor
					} else if (currentFloor == instructions.get(i).getDestinationFloor()) {
						logger.log("Elevator is dropping off passengers from floor " + temp.getCurrentFloor() + " to floor " + currentFloor);
						removedIndices.add(i);
					}
				}
//				logger.log("To be removed: " + removedIndices.toString());
				for (int i = removedIndices.size() - 1; i >= 0; i--) {
					instructions.remove(i);
				}
			}
			// check if no more instructions
			if (instructions.isEmpty()) {
				logger.log("Elevator has finished its requests.");
				currentState = ElevatorState.Idle;
				currentDirection = Direction.STOP;
				break;
			}
			// send new destination floor
			sent = new byte[] { (byte) getNextDestinationFloor(instructions) };
			logger.log("Sending: " + Arrays.toString(sent));
			packetHandler.send(sent);
			System.out.println();
		}
	}

	/**
	 * Main run method for each Elevator Agent thread.
	 */
	@Override
	public void run() {
		logger.log("Starting...");
		while (true) {
			// check for next request when idle and requests is non-empty
			if (currentState == ElevatorState.Idle && !requests.isEmpty()) {
				handleRequest(getNextRequest());
			}
		}
	}
}
