package main.elevator;

/**
 * Represents the Motor
 *
 */
public class ElevatorMotor {

	/**
	 * Time it takes to move between a single floor
	 */
	private float timeBetweenFloors;
	
	/**
	 * Instantiates a Motor with the time it takes to travel between floors
	 * @param timeBetweenFloors The time it takes to travel between two floors
	 */
	public ElevatorMotor(float timeBetweenFloors) {
		this.timeBetweenFloors = timeBetweenFloors;
	}
	
	/**
	 * Moves the elevator up
	 */
	public void MoveElevatorUp() {
		
	}
	
	/**
	 * Moves the elevator down
	 */
	public void MoveElevatorDown() {
		
	}
	
	/**
	 * Calculates the amount of time to travel between floors 
	 * @param amountOfFloors The amount of floors to travel
	 * @return The time it takes to travel between the given amount of floors
	 */
	public float timeTravelled(int amountOfFloors) {
		return amountOfFloors * timeBetweenFloors;
	}
}
