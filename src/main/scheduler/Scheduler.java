package main.scheduler;

import java.util.ArrayList;
import java.util.Iterator;

import main.common.Direction;
import main.common.Instructions;
import main.elevator.ElevatorButton;
import main.floor.FloorButton;

/**
 * Class to represent the backend server to handle communication between
 * elevator components
 */
public class Scheduler {

	/**
	 * Queue for the instructions
	 */
	private ArrayList<Instructions> queue;
	/**
	 * Arraylist of completed instructions
	 */
	private ArrayList<Instructions> completed;
	private int numCompleted; // temporary variable for stopping program
	
	/**
	 * Default constructor
	 */
	public Scheduler() {
		queue = new ArrayList<>();
		completed = new ArrayList<>();
		numCompleted = 0;
	}
	
	/**
	 * Getter for numCompleted
	 */

	public int getNumCompleted() {
		return numCompleted;
	}
	
	/**
	 * Function to send messages to floor
	 * 
	 */
	public synchronized boolean notifyFloor(int floorNumber) {
		while (completed.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
//		boolean elevatorHasReachedFloor = false;
		// for now, just return true and pop one element off of completed
		// once other logic and more floors are added, we can handle those cases
		
		completed.remove(0);
		numCompleted += 1;
		return true;
	}

	/**
	 * Function to send messages to elevator
	 * 
	 */
	public void notifyElevator() {

	}

	/**
	 * Adds instructions to the queue
	 * @param instructions
	 */
	public synchronized void addInstructions(Instructions instructions) {
		queue.add(instructions);
		notifyAll();
	}

	/**
	 * Checks if the queue is not empty with instructions
	 * @return boolean
	 */
	public boolean hasInstructions() {
		return !queue.isEmpty();
	}

	/**
	 * Removes the instructions from the queue and returns it
	 * @return the instruction that was removed
	 */
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

	/** 
	 * Adds the given instructions to the listed of completed instructions
	 * @param instructions
	 */
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

	/**
	 * Pretty string to show the scheduler's queue and completed arrays
	 * @return string
	 */
	public String toString() {
		return "SCHED:\nQ:" + queue + "\nC:" + completed + "\n";
	}
}
