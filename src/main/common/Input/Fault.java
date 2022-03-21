/**
 * 
 */
package main.common.Input;

import java.time.LocalTime;

/**
 * Class used to simulate an error in the system.
 */
public class Fault extends Input {
	private String type;
	private int elevatorId; // do we only send faults to elevators?
	
	/**
	 * Initializing Fault structure for time, type, and elevator id.
	 * 
	 * @param time String
	 * @param type String
	 * @param elevatorId int
	 */
	public Fault(String time, String type , int elevatorId) {	
		setTime(LocalTime.parse(time));
		this.type = type;
		this.elevatorId = elevatorId;
	}
	
	/**
	 * 
	 * @param args
	 */
	public Fault(String[] args) {
		this(args[0], args[1], Integer.parseInt(args[2]));
	}
	
	/**
	 * Getter for the elevator id.
	 * 
	 * @return int - elevator id.
	 */
	public int getElevatorId() { return elevatorId; }
}
