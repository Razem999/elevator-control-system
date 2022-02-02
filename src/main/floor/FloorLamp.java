/**
 * 
 */
package main.floor;

import main.common.Direction;

/**
 * Class to represent the up/down lamps on a floor
 */
public class FloorLamp {
	/** Indicates which direction the lamp is pointing */
	private Direction direction;
	
	/**
	 * Constructor that accepts a Direction value of UP/DOWN
	 * @param direction
	 */
	public FloorLamp(Direction direction) {
		this.direction = direction;
	}
	
	/**
	 * Returns the current direction value
	 * @return Direction - the current direction
	 */
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * Sets the lamp direction to UP
	 */
	public void setLightUp() {
		direction = Direction.UP;
	}
	
	/**
	 * Sets the lamp direction to DOWN
	 */
	public void setLightDown() {
		direction = Direction.DOWN;
	}
}
