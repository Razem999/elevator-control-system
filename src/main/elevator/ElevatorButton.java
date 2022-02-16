package main.elevator;

import java.util.ArrayList;

/**
 * Represents the panel of buttons that are available in an elevator
 */
public class ElevatorButton {

	/**
	 * Represents the available floors to travel
	 */
	private int floors;

	/**
	 * Represents the requested destination floor
	 */
	private int buttonPressed; // -1 if no button pressed
	 
	/**
	 * Instantiates the panel of buttons in the elevator
	 * @param floors The floors that are available to travel to
	 */
	public ElevatorButton(int floors) {
		this.floors = floors;
		buttonPressed = -1;
	}
	
	/**
	 * Gets which button was pressed
	 * @return The button that was pressed
	 */
	public int getButtonPressed() {
		return buttonPressed;
	}
	
	/**
	 * Presses button to indicate which floors to go to
	 * @param floorNum Desired floor to travel to
	 * @return Boolean indicating successful completion
	 * 
	 * TODO: Check that the buttonPressed is not the elevator's current floor
	 */
	public boolean pressFloor(int floorNum) {
		if(floorNum < 1 || floorNum > floors) {
			return false;
		}
		
		buttonPressed = floorNum;
		return true;
	}
}
