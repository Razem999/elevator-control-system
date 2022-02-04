package main.scheduler;

import java.util.ArrayList;

import main.common.Direction;
import main.common.Instructions;
import main.elevator.ElevatorButton;
import main.floor.FloorButton;

/**
 * Class to represent the backend server to handle communication between
 * elevator components
 */
public class Scheduler {

	/** haha */
	private ArrayList<Instructions> queue;
	private ArrayList<Instructions> completed;

	/**
	 * Default constructor
	 */
	public Scheduler() {
		queue = new ArrayList<>();
		completed = new ArrayList<>();
	}

	/**
	 * Function to send messages to floor
	 * 
	 */
	public boolean notifyFloor(int floorNumber) {
		while (completed.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		boolean elevatorHasReachedFloor = false;
		
		for (Instructions i : completed) {
			if (i.getDestinationFloor() == floorNumber) {
				completed.remove(i);
				elevatorHasReachedFloor = true;
			}
		}
		return elevatorHasReachedFloor;
	}

	/**
	 * Function to send messages to elevator
	 * 
	 */
	public void notifyElevator() {

	}

	public synchronized void addInstructions(Instructions instructions) {
		queue.add(instructions);
		notifyAll();
	}

	public boolean hasInstructions() {
		return !queue.isEmpty();
	}

	public synchronized Instructions popInstructions() {
		while (!hasInstructions()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		notifyAll();
		return queue.remove(0);
	}

	public synchronized void completeInstructions(Instructions instructions) {
		completed.add(instructions);
		notifyAll();
	}

	/**
	 * Function that returns the value of an elevator button press (which floor has
	 * been requested)
	 * 
	 * @return an integer representing the requested floor
	 */
	private int getElevatorButtonPress(ElevatorButton elevatorButton) {
		return elevatorButton.getButtonPressed();
	}

	/**
	 * Function that returns the value of a floor button press (elevator going up or
	 * down)
	 * 
	 * @return an enum value (UP/DOWN)
	 */
	private Direction getFloorButtonPress(FloorButton floorButton) {
		return floorButton.getDirectionalLamp();
	}

	public String toString() {
		return "SCHED:\nQ:" + queue + "\nC:" + completed + "\n";
	}
}
