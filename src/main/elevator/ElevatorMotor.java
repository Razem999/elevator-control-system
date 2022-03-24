package main.elevator;

import main.common.Logger;

/**
 * Represents the Motor
 *
 */
public class ElevatorMotor {

	/**
	 * Time it takes to move between a single floor
	 */
	private float timeBetweenFloors;
	private int elevNumber;
	private Logger logger;
	
	/**
	 * Instantiates a Motor with the time it takes to travel between floors
	 * @param timeBetweenFloors The time it takes to travel between two floors
	 */
	public ElevatorMotor(int elevator, float timeBetweenFloors) {
		this.elevNumber = elevator;
		this.timeBetweenFloors = timeBetweenFloors;
		this.logger = new Logger("ELEV " + elevNumber, "Motor");
		logger.log("Starting...");
	}
	
	/**
	 * Moves the elevator up
	 */
	public void MoveElevatorUp() {
		
	}
	
	/**
	 * Moves the elevator down
	 */
	public void MoveElevatorDown() {
		
	}
	
	/**
	 * Calculates the amount of time to travel between floors 
	 * @param amountOfFloors The amount of floors to travel
	 * @return The time it takes to travel between the given amount of floors
	 */
	public float timeTravelled(int amountOfFloors) {
		return amountOfFloors * timeBetweenFloors;
	}
	
	public void log() {
		System.out.println("Elevator " + this.elevNumber + " motor is not functioning.");
	}
}
