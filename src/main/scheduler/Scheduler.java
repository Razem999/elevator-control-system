package main.scheduler;

import main.common.DirectionalLamp;
import main.elevator.ElevatorButton;
import main.floor.FloorButton;

public class Scheduler {
	/**
	 * 
	 */
	void notifyFloor() {
		
	}
	
	void notifyElevator() {
		
	}
	
	private int getElevatorButtonPress(ElevatorButton elevatorButton) {
		return elevatorButton.getButtonPressed();
	}
	
	private DirectionalLamp getFloorButtonPress(FloorButton floorButton) {
		return floorButton.getDirectionalLamp();
	}
	
}
