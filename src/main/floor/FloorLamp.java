/**
 * 
 */
package main.floor;

import main.common.DirectionalLamp;

/**
 * Class to represent the up/down lamps on a floor
 */
public class FloorLamp {
	/** Indicates which direction the lamp is pointing */
	private DirectionalLamp direction;
	
	/**
	 * Constructor that accepts a DirectionalLamp value of UP/DOWN
	 * @param direction
	 */
	public FloorLamp(DirectionalLamp direction) {
		this.direction = direction;
	}
	
	/**
	 * Returns the current direction value
	 * @return DirectionalLamp - the current direction
	 */
	public DirectionalLamp getDirection() {
		return direction;
	}
	
	/**
	 * Sets the lamp direction to UP
	 */
	public void setLightUp() {
		direction = DirectionalLamp.UP;
	}
	
	/**
	 * Sets the lamp direction to DOWN
	 */
	public void setLightDown() {
		direction = DirectionalLamp.DOWN;
	}
}
