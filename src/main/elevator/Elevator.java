/**
 * 
 */
package main.elevator;

import main.Main;
import main.common.Instructions;
import main.scheduler.Scheduler;

/**
 * TODO
 */
public class Elevator implements Runnable {
	
	private static final float TIME_BETWEEN_FLOORS = 13;
	
	/**
	 * Scheduler reference for contact
	 */
	private Scheduler scheduler;
	/**
	 * Assigned elevator number
	 */
	private int elevatorNumber;
	/**
	 * Time passed for the thread
	 */
	private int timeWithoutInput;
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
	 * Initialize an elevator that takes in a scheduler and elevator
	 * @param scheduler
	 * @param elevatorNumber
	 */
	public Elevator(Scheduler scheduler, int elevatorNumber) {
		this.scheduler = scheduler;
		this.elevatorNumber = elevatorNumber;
		this.timeWithoutInput = 0;
		this.buttons = new ElevatorButton(Main.NUM_FLOORS);
		this.door = new ElevatorDoor();
		this.lamp = new ElevatorLamp();
		this.motor = new ElevatorMotor(TIME_BETWEEN_FLOORS);
	}
	
	/**
	 * Runs the Elevator thread that receives instructions from the scheduler and completes the instructions
	 */
	public void run() {
		while(timeWithoutInput < 20000) {
			synchronized(scheduler) {
				if (scheduler.hasInstructions()) {
					instructions = scheduler.popInstructions();
					try {
						Thread.sleep(1000); // might be TIME_BETWEEN_FLOORS x difference in Floors
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					scheduler.completeInstructions(instructions);
					timeWithoutInput = 0;
					System.out.println(scheduler);
				};
				timeWithoutInput += 1000;
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
