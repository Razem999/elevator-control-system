/**
 * 
 */
package main.elevator;

import main.common.Logger;

/**
 * ElevatorDoor represents the door
 *
 */
public class ElevatorDoor {
	
	/**
	 * Used to communicate with the console
	 */
	private Logger logger;
	

	/**
	 * Instantiates the Door's logger
	 * @param elevator The door of the elevator
	 */
	public ElevatorDoor(int elevator) {
		this.logger = new Logger("ELEV " + elevator, "Door");
		logger.log("Starting...");
	}
	
	/**
	 * Print out a formatted error message to the console
	 * @param message Whether the door is closed or open
	 */
	public void error(String message) {
		logger.log("ERROR: Doors are " + message + " for too long.");
	}
	
	/**
	 * Print out a formatted message to the console saying doors are opening
	 */
	public void open() {
		logger.log("Doors are opening.");
	}
	
	/**
	 * Print out a formatted message to the console saying doors are opening
	 */
	public void close() {
		logger.log("Doors are closing.");
	}

}
