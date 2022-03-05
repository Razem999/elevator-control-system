/**
 * 
 */
package main.common;

import java.net.InetAddress;
import java.util.Arrays;

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
