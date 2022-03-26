package main.elevator;

import main.common.Logger;

/**
 * Represents the Motor
 *
 */
public class ElevatorMotor {

	private Logger logger;
	
	/**
	 * Instantiates a Motor with the time it takes to travel between floors
	 * @param timeBetweenFloors The time it takes to travel between two floors
	 */
	public ElevatorMotor(int elevator) {
		this.logger = new Logger("ELEV " + elevator, "Motor");
		logger.log("Starting...");
	}
	
	/**
	 * Print out a formatted error message to the console
	 */
	public void error() {
		logger.log("ERROR: Not functioning.");
	}
	
	/**
	 * Print out a formatted message to the console saying the motor is running
	 */
	public void run() {
		logger.log("BRRRRRR");
	}
}
