/**
 * 
 */
package main.common.Input;

import java.time.LocalTime;

/**
 * Class used to simulate an error in the system.
 */
public class Fault extends Input {
	/** Indicates the specific fault type */
	private FaultType type;
	/** Indicates the associated elevator id */
	private int elevatorId; // do we only send faults to elevators?
	
	/**
	 * Initializing Fault structure for all instance variables.
	 * 
	 * @param time String - timestamp of the fault.
	 * @param type FaultType - type.
	 * @param elevatorId int - associated elevator id.
	 * @param duration int - duration of the error (in seconds).
	 */
	public Fault(String time, FaultType type , int elevatorId) {	
		setTime(LocalTime.parse(time));
		this.type = type;
		this.elevatorId = elevatorId;
	}
	
	/**
	 * Shortcut constructor that accepts a list of 4 string arguments.
	 * 
	 * @param args String[] - contains time, fault type, elevatorId, and duration.
	 */
	public Fault(String[] args) {
		this(args[0], FaultType.valueOf(args[1]), Integer.parseInt(args[2]));
	}
	
	/**
	 * Getter for the fault type.
	 * 
	 * @return FaultType - fault type.
	 */
	public FaultType getType() { return type; }
	
	/**
	 * Getter for the elevator id.
	 * 
	 * @return int - elevator id.
	 */
	public int getElevatorId() { return elevatorId; }
	
	/**
	 * Returns a string representation of fault's contents.
	 * 
	 * @return String - string output.
	 */
	public String toString() {
		return String.format("Fault(%s,%s,%d)", getTime().toString(), type.toString(), elevatorId);
	}
}
