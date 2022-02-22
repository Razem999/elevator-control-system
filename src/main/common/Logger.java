/**
 * 
 */
package main.common;

/**
 * Class to reduce redundant logging code
 */
public class Logger {
	/** Associated name to print with each statement */
	private String name;

	/**
	 * Default constructor that accepts a String name parameter.
	 * @param name
	 */
	public Logger(String name) {
		this.name = name;
	}

	/**
	 * Print out a formatted string with the given message to the console.
	 * @param message
	 */
	public void log(String message) {
		System.out.println(String.format("[%s]: %s", name, message));
	}
}
