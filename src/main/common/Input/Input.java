/**
 * 
 */
package main.common.Input;

import java.time.LocalTime;

/**
 * Abstract class representing a system input.
 */
public abstract class Input {
	/**
	 * Time format for when the input gets issued
	 */
	private LocalTime time;
	
	/**
	 * Getter for the time value.
	 * 
	 * @return LocalTime - time.
	 */
	public LocalTime getTime() { return time; }
	
	/**
	 * Setter for the time value.
	 */
	public void setTime(LocalTime time) { this.time = time; }
}
