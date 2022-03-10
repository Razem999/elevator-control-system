/**
 * 
 */
package main.elevator;

import main.common.Constants;
import main.common.Instructions;
import main.common.Logger;
import main.scheduler.Scheduler;

/**
 * TODO
 */
public class Elevator implements Runnable {
	
	private static final int TIME_BETWEEN_FLOORS = 500;
	
	/** logger instance to handle console logging */
	private Logger logger;
	
	/**
	 * Scheduler reference for contact
	 */
	private Scheduler scheduler;
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
	 * Instructions an elevator would take
	 */
	private Instructions instructions;
	/**
	 * Keeps track of the current state of the elevator
	 */
	private ElevatorState elevatorState;
	/**
	 * The last floor the elevator has been on
	 */
	private int currentFloor;
	
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
	 * Initialize an elevator that takes in a scheduler and elevator
	 * @param scheduler
	 * @param elevatorNumber
	 */
	public Elevator(Scheduler scheduler, int elevatorNumber) {
		this.scheduler = scheduler;
		this.elevatorNumber = elevatorNumber;
		this.buttons = new ElevatorButton(Constants.NUM_FLOORS);
		this.door = new ElevatorDoor();
		this.lamp = new ElevatorLamp();
		this.motor = new ElevatorMotor(TIME_BETWEEN_FLOORS);
		elevatorState = ElevatorState.Idle;
		this.logger = new Logger("ELEV " + elevatorNumber);
		currentFloor = 0;
		logger.log("Starting...");
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
			synchronized(scheduler) {
				logger.log("Current state: " + elevatorState);
				switch (elevatorState) {
					case Idle:
						
						// if there are errors to check for, they should be done at the start of every state's case
						
						logger.log("Looking for instructions from scheduler...");
						instructions = scheduler.popInstructions();
						logger.log("Received instructions from scheduler");
						
						elevatorState = ElevatorState.Moving;
						break;

					case Moving:
						int destinationFloor = instructions.getDestinationFloor();
						
						logger.log("Moving from floor " + currentFloor + " to destination floor " + destinationFloor);
						
						System.out.println("LOOKIE HERE: " + destinationFloor + " " + currentFloor);
						
						if (destinationFloor == currentFloor)
						{
							logger.log("Arriving at destination floor " + destinationFloor);
							elevatorState = ElevatorState.Arriving;							
							break;
						}
						
						try {
							Thread.sleep(TIME_BETWEEN_FLOORS);
							currentFloor =  destinationFloor > currentFloor ? currentFloor + 1 : currentFloor - 1;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						break;

						
					case Arriving:

						logger.log("Completed instruction " + instructions);
						logger.log("Notifying scheduler...");
						scheduler.completeInstructions(instructions);
						
						elevatorState = ElevatorState.Idle;
						break;
				}
			}
		}
	}
	
	/**
	 * Getting the associated elevator number
	 * @return int - The assigned elevator number
	 */
	public int getElevatorNumber() {
		return this.elevatorNumber;
	}

}
