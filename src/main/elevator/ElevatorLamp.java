/**
 * 
 */
package main.elevator;

import main.common.Direction;

/**
 * The ElevatorLamp class displays the direction that the associated Elevator is heading. 
 *
 */
public class ElevatorLamp {
	/**
	 * The direction enum representing the direction the Elevator is headed towards. 
	 */
	private Direction direction;
	
	/**
	 * 
	 */
	ElevatorLamp() {
		this.direction = Direction.UP;
	}
	
	/**
	 * Instantiates ElevatorLamp's direction.
	 * @param direction A DirectionLamp used to handle the Elevator's direction state.
	 */
	ElevatorLamp(Direction direction) {
		this.direction = direction;
	}
	
	/**
	 * Gets direction
	 * @return DirectionLamp
	 */
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * Sets direction to up.
	 */
	public void setLightUp() {
		direction = Direction.UP;
	}
	
	/**
	 * Sets direction to down.
	 */
	public void setLightDown() {
		direction = Direction.DOWN;
		
	}
}