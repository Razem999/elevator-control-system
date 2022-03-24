/**
 * 
 */
package main.common;

import java.net.InetAddress;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

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
	
	/**
	 * Logs message to console whenever a system receives a message
	 * @param recipient the recipient of the message
	 * @param sender the message sender
	 * @param senderAdd sender address
	 * @param senderPort the sender port
	 * @param length length of the message
	 * @message the message byte array
	 */
	public static void logReceiving(String recipient, String sender, InetAddress senderAdd, int senderPort, int length, byte[] message) {
		System.out.println(recipient + ": Packet received:");
		System.out.println("From " + sender + " " + senderAdd);
		System.out.println(sender + " port: " + senderPort);
		System.out.println("Length: " + length);
		System.out.println("Containing:");
	    System.out.println("As a string " + new String(message, 0, length));
	    System.out.println("As a byte array: " + Arrays.toString(message) + "\n");
	}
	
	/**
	 * Logs message to console whenever a system sends a message
	 * @param sender the message sender
	 * @param recipient the recipient of the message
	 * @param recvAdd recipient address
	 * @param recvPort the recipient's port
	 * @param length length of the message
	 * @message the message byte array
	 */
	public static void logSending(String sender, String recipient, InetAddress recvAdd, int recvPort, int length, byte[] message) {
		System.out.println(sender + ": Sending packet:");
		System.out.println("To " + recipient + ": " + recvAdd);
		System.out.println("Destination " + recipient + " port: " + recvPort);
		System.out.println("Length: " + length);
		System.out.println("Containing:");
		System.out.println("As a string: " + new String(message, 0, length)); 
		System.out.println("As a byte array: " + Arrays.toString(message) + "\n");
	}
}
