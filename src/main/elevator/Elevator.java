/**
 * 
 */
package main.elevator;

import main.common.Constants;
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
				return Idle;
			}
			public String toString() {
				return "MOVING";
			}
		},

//		Currently commented out Error state, looks like erroring may be an action, not a state
//		Error {
//			public ElevatorState nextState() {
//				return Idle;
//			}
//			public String toString() {
//				return "ERROR";
//			}
//		}
		;
		
		public abstract ElevatorState nextState();
		public abstract String toString();
	}
	
	/**
	 * Initialize an elevator that takes in an elevator number
	 * @param elevatorNumber
	 */
	public Elevator(int elevatorNumber) {
		this.logger = new Logger("ELEV " + elevatorNumber);
		int port = Constants.ELEVATOR_STARTING_PORT_NUMBER + elevatorNumber;

		this.elevatorNumber = elevatorNumber;
		this.buttons = new ElevatorButton(Constants.NUM_FLOORS);
		this.door = new ElevatorDoor();
		this.lamp = new ElevatorLamp();
		this.motor = new ElevatorMotor(Constants.ELEVATOR_TIME_BETWEEN_FLOORS);
		elevatorState = ElevatorState.Idle;

		this.currentFloor = 1;
		this.destinationFloor = 1;
		
		packetHandler = new PacketHandler(Constants.ELEVATOR_AGENT_STARTING_PORT_NUMBER + elevatorNumber, port, Constants.ELEVATOR_TIME_BETWEEN_FLOORS);

		logger.log("Starting...");
	}
	
	/**
	 * Returns an array of size amount containing Elevator Threads
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
	
	/**
	 * Runs the Elevator thread that receives instructions from the scheduler and completes the instructions
	 */
	public void run() {
		while(true) {
				logger.log("Current state: " + elevatorState);
				byte direction = (byte) ((currentFloor == destinationFloor) ? 0 : (currentFloor > destinationFloor ? -1 : 1));
				byte[] status = { 1, (byte) currentFloor, direction }, response = null;
				
				switch (elevatorState) {
					case Idle:
						
						// if there are errors to check for, they should be done at the start of every state's case
						logger.log("Requesting instructions from scheduler...");
						packetHandler.send(status);
						
						logger.log("Awaiting instructions from scheduler...");
						response = packetHandler.receiveTimeout();
						if (response == null) {
							logger.log("No message received, still idle");
							break;
						}
						else {
							destinationFloor = (int) response[0];							
						}
						
						logger.log("Received instructions from scheduler, new destination floor " + destinationFloor);
						elevatorState = ElevatorState.Moving;
						break;
						
					case Moving:
						logger.log("Moving from floor " + currentFloor + " to destination floor " + destinationFloor);
						packetHandler.send(status);
						if (destinationFloor == currentFloor)
						{
							logger.log("Arriving at destination floor " + destinationFloor);
							elevatorState = ElevatorState.Idle;							
							break;
						}
						
						response = packetHandler.receiveTimeout();
						if (response == null) {
							logger.log("No message received, still moving");
							break;
						}
						else {
							try {
								Thread.sleep(Constants.ELEVATOR_TIME_BETWEEN_FLOORS);
							} catch (InterruptedException e) {
								e.printStackTrace();
								System.exit(-1);
							}
							destinationFloor = (int) response[0];							
						}
						
						
						currentFloor += direction;
						break;
			}
		}
	}
	
	/**
	 * Getting the associated elevator number
	 * @return The assigned elevator number
	 */
	public int getElevatorNumber() {
		return this.elevatorNumber;
	}
	
	/**
	 * Instantiates and runs NUM_CARS Elevator threads
	 * @param args
	 */
	public static void main(String args[]) {
		Thread[] elevators = Elevator.generateElevators(Constants.NUM_CARS);
		for (int i = 0; i < elevators.length; i++) {
			elevators[i].start();
		}
	}

}
