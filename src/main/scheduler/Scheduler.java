package main.scheduler;

import main.common.Direction;
import main.common.Instructions;
import main.elevator.ElevatorButton;
import main.floor.FloorButton;

/**
 * Class to represent the backend server to handle communication between elevator components
 */
public class Scheduler {
	
	/** haha */
	private Instructions instructions;
	
	/**
	 * Default constructor
	 */
	public Scheduler() {
		
	}
	
	/**
	 * Function to send messages to floor
	 * 
	 */
	
	public void notifyFloor() {
		
	}
	
	/**
	 * Function to send messages to elevator
	 * 
	 */
	public void notifyElevator() {
		
	}
	
	public void setInstructions(Instructions i) {
		instructions = i;
	}

	/**
	 * Function that returns the value of an elevator button press (which floor has been requested)
	 * 
	 * @return an integer representing the requested floor
	 */
	private int getElevatorButtonPress(ElevatorButton elevatorButton) {
		return elevatorButton.getButtonPressed();
	}
	
	/**
	 * Function that returns the value of a floor button press (elevator going up or down)
	 * 
	 * @return an enum value (UP/DOWN)
	 */
	private Direction getFloorButtonPress(FloorButton floorButton) {
		return floorButton.getDirectionalLamp();
	}
	
	public String toString() {
		return "Scheduler " + instructions.toString();
		
	}
}
