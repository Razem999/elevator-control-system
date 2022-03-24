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
	private int elevNumber;
	private Logger logger;
	

	public ElevatorDoor(int elevator) {
		this.elevNumber = elevator;
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
	
	public void log(String message) {
		System.out.println("Elevator " + this.elevNumber + " doors are " + message + " for too long.");
	}

}
