/**
 * 
 */
package main.floor;

import java.util.ArrayList;
import main.elevator.Elevator;

/**
 * Class to represent a floor subsystem
 */
public class Floor {
	/** List of elevators */
	private ArrayList<Elevator> elevators;
	/** Button for users to interact with */
	private FloorButton direction;
	/** Associated floor number */
	private int floorNumber;
	
	/**
	 * Constructor that accepts all instance variables
	 * @param elevators
	 * @param direction
	 * @param floorNumber
	 */
	public Floor(ArrayList<Elevator> elevators, FloorButton direction, int floorNumber) {
		this.elevators = elevators;
		this.direction = direction;
		this.floorNumber = floorNumber;
	}
	
	/**
	 * Returns the floor number associated with this floor
	 * @return int - associated floor number
	 */
	public int getFloorNumber() {
		return floorNumber;
	}
	
	/**
	 * Sets the associated floor number of this floor
	 * @param floor int - the new floor number
	 */
	public void setFloorNumber(int floor) {
		floorNumber = floor;
	}
}
