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
	
	private Scheduler scheduler;
	private int elevatorNumber;
	private ElevatorButton buttons;
	private ElevatorDoor door;
	private ElevatorLamp lamp;
	private ElevatorMotor motor;
	private Instructions instructions;
	
	
	public Elevator(Scheduler scheduler, int elevatorNumber) {
		this.scheduler = scheduler;
		this.elevatorNumber = elevatorNumber;
		this.buttons = new ElevatorButton(Main.NUM_FLOORS);
		this.door = new ElevatorDoor();
		this.lamp = new ElevatorLamp();
		this.motor = new ElevatorMotor(TIME_BETWEEN_FLOORS);
	}
	
	public void run() {
		while(true) {
			synchronized(scheduler) {
				if (scheduler.hasInstructions()) {
					instructions = scheduler.popInstructions();
					try {
						Thread.sleep(1000); // might be TIME_BETWEEN_FLOORS x difference in Floors
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					scheduler.completeInstructions(instructions);
					System.out.println(scheduler);
				};
			}
		}
	}

}
