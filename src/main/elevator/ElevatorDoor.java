/**
 * 
 */
package main.elevator;

/**
 * ElevatorDoor represents the door
 *
 */
public class ElevatorDoor {

	/**
	 * Represents if door is open or not
	 */
	private boolean doorState;
	
	/**
	 * Instantiates the elevator door with a specific position: open or closed
	 * @param doorState Starting door position
	 */
	public ElevatorDoor(boolean doorState) {
		this.doorState = doorState;
	}
	
	/**
	 * Gives information on the door opening or not
	 * @return The door state
	 */
	public boolean isDoorOpen() {
		return doorState;
	}
	
	/**
	 * Closes the door if it is open or vice versa
	 */
	public void toggleDoorState() {
		doorState = !doorState;
	}
	
	
}
