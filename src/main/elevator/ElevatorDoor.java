/**
 * 
 */
package main.elevator;

import main.common.Constants;
import main.common.PacketHandler;

/**
 * ElevatorDoor represents the door
 *
 */
public class ElevatorDoor {
	
	/**
	 * Represents the state of the door
	 * 0 indicates the door is closed
	 * 1 indicates the door is open
	 */
	private int doorState;
	
	/**
	 * The PacketHandler used by the ElevatorDoor to communicate with the Elevator
	 */
	private PacketHandler packetHandler;
	
	/**
	 * Initializing elevator door state to closed
	 */
	public ElevatorDoor(int elevatorNumber, int doorState) {
		this.doorState = 0;
		int port = Constants.ELEVATOR_STARTING_PORT_NUMBER + (elevatorNumber * Constants.NEW_ELEVATOR_INCREMENT) + Constants.ELEVATOR_DOOR_PORT_NUMBER;
		
	}
	
	/**
	 * Gives information on the door opening or not
	 * @return The door state
	 */
	public boolean isDoorOpen() {
		if (doorState == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Sets elevator doors to open
	 */
	public void openDoor() {
		this.doorState = 1;
	}
	
	/**
	 * Sets elevator doors to close
	 */
	public void closeDoor() {
		this.doorState = 0;
	}
	
}
