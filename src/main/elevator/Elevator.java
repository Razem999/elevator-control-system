/**
 * 
 */
package main.elevator;

import main.scheduler.Scheduler;

/**
 * TODO
 */
public class Elevator {
	
	private final float TIME_BETWEEN_FLOORS = 13;
	
	private Scheduler scheduler;
	
	private ElevatorButton[] buttons;
	private ElevatorDoor door;
	private ElevatorLamp lamp;
	private ElevatorMotor motor;
	
	
	Elevator(Scheduler scheduler) {
		this.scheduler = scheduler;

		this.motor = new ElevatorMotor(TIME_BETWEEN_FLOORS);
	}

}
