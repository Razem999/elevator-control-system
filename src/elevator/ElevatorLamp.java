/**
 * 
 */
package elevator;

/**
 * The ElevatorLamp class displays the direction that the associated Elevator is heading. 
 *
 */
public class ElevatorLamp {
	/**
	 * The direction enum representing the direction the Elevator is headed towards. 
	 */
	private DirectionLamp direction;
	
	/**
	 * Instantiates ElevatorLamp's direction.
	 * @param direction A DirectionLamp used to handle the Elevator's direction state.
	 */
	ElevatorLamp(DirectionLamp direction) {
		this.direction = direction;
	}
	
	/**
	 * Gets direction
	 * @return DirectionLamp
	 */
	public DirectionLamp getDirection() {
		return direction;
	}
	
	/**
	 * Sets direction to up.
	 */
	public void setLightUp() {
		direction = DirectionLamp.UP;
	}
	
	/**
	 * Sets direction to down.
	 */
	public void setLightDown() {
		direction = DirectionLamp.DOWN;
		
	}
}
