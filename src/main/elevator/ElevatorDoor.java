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
	 */
	private boolean isDoorOpen;
	
	/**
	 * The PacketHandler used by the ElevatorDoor to communicate with the Elevator
	 */
	private PacketHandler packetHandler;
	
	/**
	 * Initializing elevator door state to closed
	 */
	public ElevatorDoor(int elevatorNumber) {
		this.isDoorOpen = true;
		int port = Constants.ELEVATOR_STARTING_PORT_NUMBER + (elevatorNumber * Constants.ELEVATOR_INCREMENT) + Constants.ELEVATOR_DOOR_PORT_NUMBER;
		
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
	
}
