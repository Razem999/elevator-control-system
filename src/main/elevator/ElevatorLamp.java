/**
 * 
 */
package main.elevator;

import main.common.DirectionalLamp;

/**
 * The ElevatorLamp class displays the direction that the associated Elevator is heading. 
 *
 */
public class ElevatorLamp {
	/**
	 * The direction enum representing the direction the Elevator is headed towards. 
	 */
	private DirectionalLamp direction;
	
	/**
	 * Instantiates ElevatorLamp's direction.
	 * @param direction A DirectionLamp used to handle the Elevator's direction state.
	 */
	ElevatorLamp(DirectionalLamp direction) {
		this.direction = direction;
	}
	
	/**
	 * Gets direction
	 * @return DirectionLamp
	 */
	public DirectionalLamp getDirection() {
		return direction;
	}
	
	/**
	 * Sets direction to up.
	 */
	public void setLightUp() {
		direction = DirectionalLamp.UP;
	}
	
	/**
	 * Sets direction to down.
	 */
	public void setLightDown() {
		direction = DirectionalLamp.DOWN;
		
	}
}