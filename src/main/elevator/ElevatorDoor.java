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
	 * Represents the state of the door
	 */
	private boolean isDoorOpen;
	private Logger logger;
	

	/**
	 * Instantiates a closed Door for the elevator
	 * @param elevator The door of the elevator
	 */
	public ElevatorDoor(int elevator) {
		this.isDoorOpen = false;
		this.logger = new Logger("ELEV " + elevator, "Door");
		logger.log("Starting...");
	}
	
	/**
	 * Gives information on the door opening or not
	 * @return The door state
	 */
	public boolean getIsDoorOpen() {
		return isDoorOpen;
	}
	
	/**
	 * Toggles between door states
	 */
	public void toggleDoorState() {
		isDoorOpen = !isDoorOpen;
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
