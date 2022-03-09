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
	 * Constructor that defaults to the 'OFF' state
	 * @param direction
	 */
	public FloorLamp() {
		this.direction = Direction.OFF;
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
