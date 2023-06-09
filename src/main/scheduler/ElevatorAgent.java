/**
 * 
 */
package main.scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import main.common.Logger;
import main.common.PacketHandler;
import main.common.Input.Instructions;
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
	 * Keeps track of the associated Elevator's previous direction.
	 */
	private Direction previousDirection;
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
	 * Indicates whether a pickup for a request was done.
	 */
	private final HashMap<Instructions, Boolean> pickedUpRequests;
	/**
	 * PacketHandler instance for communication.
	 */
	private final PacketHandler packetHandler;
	/**
	 * Logger instance to log messages.
	 */
	private final Logger logger;
	/**
	 * Boolean used to shut down elevator agent thread
	 */
	private boolean isRunning;
	
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
		this.previousDirection = Direction.STOP;
		this.currentFloor = currentFloor;
		this.elevatorId = elevatorId;
		this.requests = requests;
		this.pickedUpRequests = new HashMap<>();
		this.isRunning = true;
		this.packetHandler = new PacketHandler(Constants.ELEVATOR_STARTING_PORT_NUMBER + elevatorId, Constants.ELEVATOR_AGENT_STARTING_PORT_NUMBER + elevatorId);
		this.logger = new Logger("EAGENT " + elevatorId);
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
	 * Getter for previous elevator direction.
	 * 
	 * @return Direction - previous direction.
	 */
	public Direction getPreviousDirection() { return previousDirection; }
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
		previousDirection = currentDirection;
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
			pickedUpRequests.put(intermediate, false);
			return true;
		}
		return false;
	}
	
	/**
	 * Helper method to get the elevator's next destination floor.
	 * Returns a byte array representing { destinationFloor, 1 if last destination and 0 otherwise }
	 * 
	 * @param instructions ArrayList - set of current instructions.
	 * @returns byte[] - formatted byte array.
	 */
	private byte[] getNextDestinationFloor(ArrayList<Instructions> instructions) {
		int minDifference = 100, nextFloor = 1, lastFloor = 0;
		// iterate through every instruction that should be served by this elevator
		for (Instructions ins : instructions) {
			// candidate floor represents the floor that we will potentially go to next, initialize it to 0 if we are going down, the top floor
			// if we are going up, since we are trying to find the minimum distance to travel in the same direction we are already headed
			int candidateFloor;

			// if the pickup hasn't been completed yet, we should do that first
			if (!pickedUpRequests.get(ins)) {
				candidateFloor = ins.getCurrentFloor();
			// otherwise, set the dropoff floor as the candidate
			} else {
				candidateFloor = ins.getDestinationFloor();
			}

			// we compare the candidateFloor to the current minimum distance destination, and replace it if
			// candidate is more favorable
			int difference = Math.abs(candidateFloor - currentFloor);
			if (difference < minDifference && difference != 0) {
				minDifference = difference;
				nextFloor = candidateFloor;
			}
		}
		// if there is only one instruction left and this is the destination of the request
		// indicate to the elevator that this is the last floor
		lastFloor = 0;
		for (Instructions ins : instructions) {
			if (pickedUpRequests.get(ins) && nextFloor == ins.getDestinationFloor()) {
				lastFloor += 1;
			} else {
				break;
			}
		}
		return new byte[] { (byte) nextFloor, (byte) (lastFloor == instructions.size() ? 1 : 0) };
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
		pickedUpRequests.put(ins, false);
		int currentDestination = ins.getCurrentFloor();

		// send first instructions to elevator
		byte[] sent = new byte[] { (byte) currentDestination, 0 };
		byte[] elevFailure = new byte[] { (byte) -1, (byte) elevatorId };
		logger.log("Sending first instruction: " + Arrays.toString(sent));
		packetHandler.send(sent);

		// while elevator is moving or arriving
		while (currentState == ElevatorState.Moving || currentState == ElevatorState.Arriving) {
			// receive status updates from elevator
			byte[] received = packetHandler.receiveTimeout(Constants.ELEVATOR_AGENT_TIMEOUT);
			
			// Elevator is deemed dead if no response within given time
			if (received == null) {
				logger.log(elevatorId + " is now considered dead");
				packetHandler.setSendPort(Constants.SCHEDULER_PORT);
				packetHandler.send(elevFailure);
				isRunning = false;
				break;
			}
			
//			logger.log("Received: " + Arrays.toString(received));
			currentFloor = received[1];
			previousDirection = currentDirection;
			currentDirection = received[2] == 0 ? Direction.STOP : received[2] == 1 ? Direction.UP : Direction.DOWN;
			if (currentDirection == Direction.STOP) {
				logger.log("Elevator is arriving at floor " + currentFloor);
				currentState = ElevatorState.Arriving;
			} else {
				logger.log("Elevator is passing floor " + currentFloor + ", heading " + currentDirection);
				currentState = ElevatorState.Moving;
			}
			// check if intermediate requests can be dealt with here
			if (!requests.isEmpty()) {
				logger.log("Checking for intermediate requests...");
				if (getIntermediateRequest(instructions)) {
					logger.log("intermediate request added: " + instructions.toString());
					logger.log("Elevator is picking up passengers at floor " + currentFloor);
					sent = new byte[] { (byte) currentFloor, 0 };
					logger.log("Sending: " + Arrays.toString(sent));
					packetHandler.send(sent);
					continue;
				}
			}
			// check if elevator has stopped and reached a destination floor
			if (currentState == ElevatorState.Arriving) {
				logger.log("Elevator arrived at a pickup/dropoff floor");
				ArrayList<Integer> toBePickedUp = new ArrayList<>();
				ArrayList<Integer> toBeRemoved = new ArrayList<>();
//				logger.log("Currently servicing requests: " + instructions.toString());
				for (int i = 0; i < instructions.size(); i++) {
					// elevator reached pickup floor
					Instructions temp = instructions.get(i);
					if (!pickedUpRequests.get(temp) && currentFloor == temp.getCurrentFloor()) {
						logger.log("Elevator is picking up passengers at floor " + currentFloor + " to drop off at " + temp.getDestinationFloor());
						toBePickedUp.add(i);
					// elevator reached dropoff floor
					} else if (pickedUpRequests.get(temp) && currentFloor == instructions.get(i).getDestinationFloor()) {
						logger.log("Elevator is dropping off passengers from floor " + temp.getCurrentFloor() + " to floor " + currentFloor);
						toBeRemoved.add(i);
					}
				}
				// update pick ups
				for (Integer i : toBePickedUp) {
					pickedUpRequests.put(instructions.get(i), true);
				}
				// remove completed requests
				for (int i = toBeRemoved.size() - 1; i >= 0; i--) {
					int idx = toBeRemoved.get(i);
					pickedUpRequests.remove(instructions.get(idx));
					instructions.remove(idx);
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
			sent = getNextDestinationFloor(instructions);
//			logger.log("Sending: " + Arrays.toString(sent));
			logger.log("Sending destination floor " + sent[0] + (sent[1] == 1 ? ". This is its last destination." : ""));
			packetHandler.send(sent);
		}
	}
	
	@Override
	public String toString() {
		return String.format("ElevatorAgent(%d, %s, %d, %s)", elevatorId, currentState.toString(), currentFloor, currentDirection.toString());
	}

	/**
	 * Main run method for each Elevator Agent thread.
	 */
	@Override
	public void run() {
		logger.log("Starting...");
		while (isRunning) {
			// check for next request when idle and requests is non-empty
			if (currentState == ElevatorState.Idle && !requests.isEmpty()) {
				handleRequest(getNextRequest());
			} else {
				// check if elevator has closed or not
				// receive status updates from elevator
				byte[] received = packetHandler.receiveTimeout(Constants.ELEVATOR_AGENT_TIMEOUT);
				byte[] elevFailure = new byte[] { (byte) -1, (byte) elevatorId };
				
				// Elevator is deemed dead if no response within given time
				if (received == null) {
					logger.log(elevatorId + " is now considered dead");
					packetHandler.setSendPort(Constants.SCHEDULER_PORT);
					packetHandler.send(elevFailure);
					break;
				}
			}
		}
	}
}
