package main.elevator;

import java.util.ArrayList;

public class ElevatorButton {
	private ArrayList<Integer> floors = new ArrayList<>();
	private int buttonPressed; // -1 if no button pressed
	 
	public ElevatorButton(ArrayList<Integer> floors) {
		this.floors = floors;
		buttonPressed = -1;
	}
	
	public int getButtonPressed() {
		return buttonPressed;
	}
	
	/**
	 * 
	 */
	public boolean pressFloor(int floorNum) {
		if(floorNum > floors.size()) {
			return false;
		}
		
		buttonPressed = floors.get(floorNum-1);
		return true;
	}
}
