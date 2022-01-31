/**
 * 
 */
package main.floor;

import main.common.DirectionalLamp;

/**
 * Class to represent a up/down button on a floor
 */
public class FloorButton {
	/** Indicates which direction the associated lamp is pointing */
	private DirectionalLamp arrows;
	
	public DirectionalLamp getDirectionalLamp() {
		return arrows;
	}
	
	
	/**
	 * Constructor that accepts a DirectionalLamp value of UP/DOWN
	 * @param arrows 
	 */
	public FloorButton(DirectionalLamp arrows) {
		this.arrows = arrows;
	}
	
	/**
	 * Simulates pressing the up directional button
	 */
	public void pressUpButton() {
		arrows = DirectionalLamp.UP;
	}
	
	/**
	 * Simulates pressing the down directional button
	 */
	public void pressDownButton() {
		arrows = DirectionalLamp.DOWN;
	}
}
