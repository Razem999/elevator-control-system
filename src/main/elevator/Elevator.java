/**
 * 
 */
package main.elevator;

import main.common.Constants;
import main.common.Direction;
import main.common.Logger;
import main.common.PacketHandler;

/**
 * The Elevator communicates with the Scheduler to travel to various Floors.
 */
public class Elevator implements Runnable {

	/** logger instance to handle console logging */
	private Logger logger;
	/**
	 * Assigned elevator number
	 */
	private int elevatorNumber;
	/**
	 * Reference of the Elevator Buttons
	 */
	private ElevatorButton buttons;
	/**
	 * Reference of the Elevator door
	 */
	private ElevatorDoor door;
	/**
	 * Reference of the Elevator lamps
	 */
	private ElevatorLamp lamp;
	/**
	 * Reference of the Elevator motor
	 */
	private ElevatorMotor motor;
	/**
	 * The destination the Elevator wants to go to
	 */
	private int destinationFloor;
	/**
	 * Represents if the next destination will be the final one
	 */
	private boolean isFinalDestination;
	/**
	 * Keeps track of the elevator's direction
	 */
	private byte direction;
	/**
	 * Keeps track of the current state of the elevator
	 */
	private ElevatorState elevatorState;
	/**
	 * The last floor the elevator has been on
	 */
	private int currentFloor;
	/**
	 * The PacketHandler used by the Elevator to communicate with the Scheduler
	 */
	private PacketHandler packetHandler;
	/**
	 * The elevator will exit after this number of consecutive cycles in the IDLE state
	 */
	private int consecutiveIdles;
	/**
	 * Represents whether there will be any motor errors.
	 */
	private boolean willMotorFail;
	/**
	 * Represents whether the door will be stuck open for too long.
	 */
	private boolean willDoorsBeStuckOpen;
	/**
	 * Represents whether the door will be stuck closed for too long.
	 */
	private boolean willDoorsBeStuckClosed;
	/**
	 * Boolean used to shut down a thread
	 */
	private boolean isRunning;
	/**
	 * Elevator state machine definition
	 */
	public enum ElevatorState {
		Idle {
			public ElevatorState nextState() {
				return Moving;
			}

			public String toString() {
				return "IDLE";
			}
		},

		Moving {
			public ElevatorState nextState() {
				return Arriving;
			}

			public String toString() {
				return "MOVING";
			}
		},
		
		Arriving {
			public ElevatorState nextState() {
				return Idle;
			}
			
			public String toString() {
				return "ARRIVING";
			}
		};

		public abstract ElevatorState nextState();

		public abstract String toString();
	}

	/**
	 * Initialize an elevator that takes in an elevator number
	 * 
	 * @param elevatorNumber
	 */
	public Elevator(int elevatorNumber) {
		this.logger = new Logger("ELEV " + elevatorNumber);
		int port = Constants.ELEVATOR_STARTING_PORT_NUMBER + elevatorNumber;

		this.elevatorNumber = elevatorNumber;
		this.buttons = new ElevatorButton(Constants.NUM_FLOORS);
		this.door = new ElevatorDoor(elevatorNumber);
		this.lamp = new ElevatorLamp();
		this.motor = new ElevatorMotor(elevatorNumber);
		this.isRunning = true;
		elevatorState = ElevatorState.Idle;

		this.consecutiveIdles = 0;
		
		this.currentFloor = 1;
		this.destinationFloor = 1;
		this.isFinalDestination = this.willMotorFail = this.willDoorsBeStuckOpen = this.willDoorsBeStuckClosed = false;

		this.packetHandler = new PacketHandler(Constants.ELEVATOR_AGENT_STARTING_PORT_NUMBER + elevatorNumber, port,
				Constants.ELEVATOR_TIME_BETWEEN_FLOORS);

		this.logger.log("Starting...");
	}

	/**
	 * Returns an array of size amount containing Elevator Threads
	 * 
	 * @param amount How many Elevator Threads to create
	 * @return An array of Elevator Threads
	 */
	public static Thread[] generateElevators(int amount) {
		Thread[] elevators = new Thread[amount];
		for (int i = 0; i < amount; i++) {
			elevators[i] = new Thread(new Elevator(i));
		}

		return elevators;
	}

	// getters/setters for unit tests
	public ElevatorState getState() {
		return elevatorState;
	}
	
	public void setState(ElevatorState state) {
		elevatorState = state;
	}
	
	public boolean getProcessMessage(byte[] message) {
		return processMessage(message);
	}
	
	public boolean getwillDoorsBeStuckClosed() {
		return willDoorsBeStuckClosed;
	}
	
	public boolean getwillDoorsBeStuckOpen() {
		return willDoorsBeStuckOpen;
	}
	
	public boolean getwillMotorFail() {
		return willMotorFail;
	}
	
	public boolean isFinalDestination() {
		return isFinalDestination;
	}
	
	/**
	 * Interprets the message received by the Elevator and updates the destination floor and errors accordingly.
	 * @param message The message received by the Elevator
	 * @param logDestination Whether the new destination should be logged or not
	 * @return Whether a message was received or not
	 */
	private boolean processMessage(byte[] message) {
		if (message == null) return false;
		if (message[0] >= 0) { // we're provided a destination
			destinationFloor = (int) message[0];
			isFinalDestination = message[1] == Constants.ELEVATOR_FINAL_DESTINATION_VALUE;
			logger.log("Received instructions from scheduler, new destination floor " + destinationFloor);
		}
		else {
			String error = "";
			if (message[0] == Constants.ELEVATOR_MOTOR_FAIL_VALUE) {
				willMotorFail = true;
				error = "MOTOR WILL FAIL";
			}
			else if (message[0] == Constants.ELEVATOR_DOOR_STUCK_OPEN_VALUE) {
				willDoorsBeStuckOpen = true;
				error = "DOORS GET STUCK OPEN";
			}
			else if (message[0] == Constants.ELEVATOR_DOOR_STUCK_CLOSED_VALUE) {
				willDoorsBeStuckClosed = true;
				error = "DOORS GET STUCK CLOSED";
			}
			logger.log("Received " + error + " error from scheduler ");
			// we don't bother checking for other error values because they shouldn't exist.
		}
		return true;
	}
	
	/**
	 * Simulates the time taken by doors opening and closing, taking door faults in mind. Listens for messages while the doors are opening/closing.
	 */
	private void openCloseDoors() {
		byte[] response;
		int count = willDoorsBeStuckClosed ? 2 : 1;
		door.open();
		while (count > 0) {
			if (willDoorsBeStuckClosed) {
				door.error("closed");
				count++;
				logger.log("Door, Trying to open the door");
			}
			willDoorsBeStuckClosed = false;

			response = packetHandler.receiveTimeout(Constants.ELEVATOR_TIME_FOR_DOORS);
			processMessage(response);
			count--;
		}
		logger.log("Door, Opened");
		
		count = willDoorsBeStuckOpen ? 2 : 1;
		door.close();
		while (count > 0) {
			if (willDoorsBeStuckOpen) {
				door.error("open");
				count++;
				logger.log("Door, Trying to close the door");
			}
			willDoorsBeStuckOpen = false;

			response = packetHandler.receiveTimeout(Constants.ELEVATOR_TIME_FOR_DOORS);
			processMessage(response);
			count--;
		}
		logger.log("Door, Closed");
	}
	
	/* 
	 * Converts this elevator's current state to a byte for sending to model
	 * @return stateByte
	 */
	private byte convertStateToByte() {
		switch (elevatorState) {
			case Idle:
				return (byte) 0;
			case Moving:
				return (byte) 1;
			case Arriving:
				return (byte) 2;
			default:
				return (byte) 0;
		}
	}
	
	private byte convertFaultToByte() {
		if (willMotorFail) {
			return (byte) 1;
		}
		
		if (elevatorState == ElevatorState.Idle || elevatorState == ElevatorState.Arriving) {
			if (willDoorsBeStuckOpen) {
				return (byte) 2;
			}
			else if (willDoorsBeStuckClosed) {
				return (byte) 3;
			}
		}
	
		return (byte) 0;
	}
	
	/**
	 * This method creates a byte array to be sent to the Model with this elevators current information
	 * The byte array will consist of 5 integers. index 0 is current floor, index 1 is destination floor, index 2 is elevator direction, index 3 is state, index 4 is fault
	 * Ex: [1,10,0,0,0] would be curr floor: 1, destination floor: 10, direction: STOP, state: idle, fault: none
	 */
	private byte[] createStatusUpdate() {
		byte[] message = new byte[5];
		
		message[0] = (byte) currentFloor;
		message[1] = (byte) destinationFloor;
		message[2] = (byte) direction;
		message[3] = convertStateToByte();
		message[4] = convertFaultToByte();
		
		return message;
	}
	/**
	 * Shuts down the thread after closing the packetHandler's socket.
	 */
	private void shutDown() {
		packetHandler.shutDown();
		isRunning = false;
	}

	/**
	 * Runs the Elevator thread that receives instructions from the scheduler and
	 * completes the instructions
	 */
	public void run() {
		while (isRunning) {
			logger.log("Current state: " + elevatorState);
			direction = (byte) ((currentFloor == destinationFloor) ? 0
					: (currentFloor > destinationFloor ? -1 : 1));
			byte[] status = { 1, (byte) currentFloor, direction }, response = null;

			switch (elevatorState) {
				case Idle:
					
					
					
					logger.log("Updating my agent");
					packetHandler.send(status);
					logger.log("Awaiting instructions from scheduler...");	
					response = packetHandler.receiveTimeout(Constants.ELEVATOR_TIMEOUT);
					
					// If we don't receive a message, just stay in idle state
					if (!processMessage(response)) {
						consecutiveIdles++;
						if (consecutiveIdles == Constants.IDLE_EXIT_COUNT) {
							logger.log("Elevator idle for too long...Exiting");
							shutDown();
						}
						elevatorState = ElevatorState.Idle;
						break;
					}
					else {
						consecutiveIdles = 0;
						if (currentFloor != destinationFloor) {
							elevatorState = ElevatorState.Moving;
							break; // no need to open/close doors if we're not at the right floor
						}
						// MODEL MODEL MODEL
						packetHandler.send(createStatusUpdate(), Constants.MODEL_PORT + elevatorNumber);
						// MODEL MODEL MODEL
						openCloseDoors();
						// MODEL MODEL MODEL
						packetHandler.send(createStatusUpdate(), Constants.MODEL_PORT + elevatorNumber);
						// MODEL MODEL MODEL
					}
					break;

				case Moving:
					String finalDest = isFinalDestination ? "final " : "";
					logger.log("Moving from floor " + currentFloor + " to " + finalDest + "destination floor " + destinationFloor);
					motor.run();
					if (willMotorFail) {
						motor.error();
						shutDown();
						break;
					}
					
					packetHandler.send(status);
					
					response = packetHandler.receiveTimeout(Constants.ELEVATOR_TIME_BETWEEN_FLOORS);
					if (processMessage(response)) {
						response = packetHandler.receiveTimeout(Constants.ELEVATOR_TIME_BETWEEN_FLOORS);
						processMessage(response);
						
						// line below might not even be needed
						direction = (byte) ((currentFloor == destinationFloor) ? 0
								: (currentFloor > destinationFloor ? -1 : 1));
					}
					
					// MODEL MODEL MODEL
					packetHandler.send(createStatusUpdate(), Constants.MODEL_PORT + elevatorNumber);
					// MODEL MODEL MODEL
					
					if (destinationFloor == currentFloor) {
						logger.log("Arriving at destination floor " + destinationFloor);
						elevatorState = ElevatorState.Arriving;
						break;
					}
					
					currentFloor += direction;
					break;
				case Arriving:
					logger.log("I'm arriving to my destination floor " + destinationFloor);
					
					// MODEL MODEL MODEL
					packetHandler.send(createStatusUpdate(), Constants.MODEL_PORT + elevatorNumber);
					// MODEL MODEL MODEL
					openCloseDoors();
					// MODEL MODEL MODEL
					packetHandler.send(createStatusUpdate(), Constants.MODEL_PORT + elevatorNumber);
					// MODEL MODEL MODEL
					
					if (isFinalDestination) {
						packetHandler.send(status);
						elevatorState = ElevatorState.Idle;
					} else {
						elevatorState = ElevatorState.Moving;
					}

					break;
			}
		}
	}

	/**
	 * Getting the associated elevator number
	 * 
	 * @return The assigned elevator number
	 */
	public int getElevatorNumber() {
		return this.elevatorNumber;
	}

	/**
	 * Instantiates and runs NUM_CARS Elevator threads
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		Thread[] elevators = Elevator.generateElevators(Constants.NUM_CARS);
		for (int i = 0; i < elevators.length; i++) {
			elevators[i].start();
		}
	}

}
