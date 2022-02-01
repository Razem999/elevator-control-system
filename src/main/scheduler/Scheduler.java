package main.scheduler;

import main.common.DirectionalLamp;
import main.elevator.ElevatorButton;
import main.floor.FloorButton;

/**
 * Class to represent the backend server to handle communication between elevator components
 */
public class Scheduler {
	
	/**
	 * Default constructor
	 */
	public Scheduler() {
		
	}
	
	
	/**
	 * Function to send messages to floor
	 * 
	 */
	void notifyFloor() {
		
	}
	
	
	/**
	 * Function to send messages to elevator
	 * 
	 */
	void notifyElevator() {
		
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
	private DirectionalLamp getFloorButtonPress(FloorButton floorButton) {
		return floorButton.getDirectionalLamp();
	}
	
}
