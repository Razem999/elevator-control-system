/**
 * 
 */
package main.common;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class to reduce redundant logging code. Now with timing!
 */
public class Logger {
	/** Subsystem name to print with each statement */
	private String subsystemName;
	/** Subcomponent name to print with each statement, if applicable */
	private String subcomponentName;

	/**
	 * Default constructor that accepts a subsystem String parameter.
	 * Use if no subcomponent is applicable.
	 * 
	 * @param subsystem String - name of subsystem.
	 */
	public Logger(String subsystemName) {
		this(subsystemName, null);
	}

	/**
	 * Constructor that specifies both subsystem and subcomponent names.
	 * Use if a subcomponent name is applicable.
	 * 
	 * @param subsystemName String - name of subsystem.
	 * @param subcomponentName - name of subcomponent.
	 */
	public Logger(String subsystemName, String subcomponentName) {
		this.subsystemName = subsystemName;
		this.subcomponentName = subcomponentName;
	}

	/**
	 * Print out a formatted string with the given message to the console.
	 * Format: [timestamp, subsystem, subcomponent_if_applicable, message]
	 * 
	 * @param message String - message to print.
	 */
	public void log(String message) {
		String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
		if (subcomponentName == null) {
			System.out.println(String.format("%s, %s, %s", timestamp, subsystemName, message));
		} else {
			System.out.println(String.format("%s, %s, %s, %s", timestamp, subsystemName, subcomponentName, message));
		}
	}
}
