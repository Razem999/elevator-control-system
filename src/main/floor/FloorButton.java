/**
 * 
 */
package main.floor;

import main.common.Direction;

/**
 * Class to represent a up/down button on a floor
 */
public class FloorButton {
	/** Indicates which direction the associated lamp is pointing */
	private Direction arrows;
	
	/**
	 * Constructor that defaults direction to UP
	 */
	public FloorButton() {
		this.arrows = Direction.UP;
	}
	
	/**
	 * Constructor that accepts a Direction value of UP/DOWN
	 * @param arrows 
	 */
	public FloorButton(Direction arrows) {
		this.arrows = arrows;
	}
	
	public Direction getDirectionalLamp() {
		return arrows;
	}
	
	/**
	 * Simulates pressing the up directional button
	 */
	public void pressUpButton() {
		arrows = Direction.UP;
	}
	
	/**
	 * Simulates pressing the down directional button
	 */
	public void pressDownButton() {
		arrows = Direction.DOWN;
	}
}
