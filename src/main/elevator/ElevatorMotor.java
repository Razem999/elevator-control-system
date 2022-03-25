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
	private Logger logger;
	
	/**
	 * Instantiates a Motor with the time it takes to travel between floors
	 * @param timeBetweenFloors The time it takes to travel between two floors
	 */
	public ElevatorMotor(int elevator, float timeBetweenFloors) {
		this.timeBetweenFloors = timeBetweenFloors;
		this.logger = new Logger("ELEV " + elevator, "Motor");
		logger.log("Starting...");
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
		logger.log("Motor is not functioning.");
	}
}
