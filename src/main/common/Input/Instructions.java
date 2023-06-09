/**
 * 
 */
package main.common.Input;

import java.time.LocalTime;

import main.common.Direction;

/**
 * Instruction Data structure for sending commands from floor to elevator
 *
 */
public class Instructions extends Input {
	/**
	 * Direction of where the passenger would like to go
	 */
	private Direction direction;
	/**
	 * Current floor of passenger
	 */
	private int currentFloor;
	/**
	 * Destination floor of where the passenger would like to go
	 */
	private int destinationFloor;
	
	/**
	 * Initializing Instruction structure for time, floors, and direction
	 * @param time
	 * @param currentFloor
	 * @param direction
	 * @param destinationFloor
	 */
	public Instructions(String time, String currentFloor, String direction , String destinationFloor) {	
		setTime(LocalTime.parse(time));
		this.currentFloor = Integer.parseInt(currentFloor);
		this.direction = Direction.valueOf(direction.toUpperCase());
		this.destinationFloor = Integer.parseInt(destinationFloor);
	}
	
	public Instructions(String[] instructions) {
		this(instructions[0], instructions[1], instructions[2], instructions[3]);
	}
	
	/**
	 * Getting destination of instruction creation
	 * @return destination floor
	 */
	public int getDestinationFloor() {
		return destinationFloor;
	}
	
	/**
	 * Getting current floor of instruction creation
	 * @return current floor
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	/**
	 * Getting direction of instruction creation
	 * @return direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Returns a string of the instructions prettified
	 * @return prettified string
	 */
	public String toString() {
		return String.format("Instructions(%s,%s,%d,%d)", getTime().toString(), direction,  currentFloor, destinationFloor);
	}
}
