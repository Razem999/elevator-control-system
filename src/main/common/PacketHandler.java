package main.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


/**
 * Class that contains generic methods for dealing with UDP communication
 */
public class PacketHandler {
	
	private int receiverPort; // the port of the entity that we will communicate with
	
	public PacketHandler() {
		receiverPort = -1;
	}
	
	public PacketHandler(int receiverPort) {
		this.receiverPort = receiverPort;
	}
	
	// setter for use with client so we can dynamically set it
	public void setReceiverPort(int receiverPort) {
		this.receiverPort = receiverPort;
	}
	
	/**
	 * Create new array that is only the size we need, so we don't send unnecessary data via UDP
	 * @param message original byte array
	 * @param true length of message
	 * @return message with ending zeroes removed
	 */
	public static byte[] removeUnecessary(byte[] message, int length) {
		// create new array that is only the size we need, so we don't send unnecessary data via UDP
	    byte[] receivedRightSize = new byte[length];
	    System.arraycopy(message, 0, receivedRightSize, 0, length);
	    
	    return receivedRightSize;
	}
	
	/**
	 * Helper function for sending DatagramPacket over a DatagramSocket to a specified port
	 * @param sendSocket the socket to be used
	 * @param sendPacket the packet to be sent
	 * @param from the name of the sender
	 * @param to the name of the desired recipient
	 */
	protected void send(DatagramSocket sendSocket, DatagramPacket sendPacket, String from, String to) {
		// Send the datagram packet to the server via the send/receive socket. 
		try {
		   sendSocket.send(sendPacket);
		} catch (IOException e) {
		   e.printStackTrace();
		   System.exit(1);
		}
	}
	
	/**
	 * Helper function to create a packet with the given byte array message
	 * @param message the message to create a packet with
	 * @return sendPacket the created packet
	 */
	protected DatagramPacket createPacket(byte[] message) {
		DatagramPacket sendPacket = null;
		// Attempt to send packet to the passed in port
		try {
		   sendPacket = new DatagramPacket(message, message.length,
		                                  InetAddress.getLocalHost(), receiverPort);
		} catch (UnknownHostException e) {
		   e.printStackTrace();
		   System.exit(1);
		}
		
		return sendPacket;
	}
	/**
	 * Helper function for receiving DatagramPacket over a DatagramSocket
	 * @param receiveSocket the socket to be used
	 * @param receivePacket the packet to be sent
	 * @param from the name of the sender
	 * @param to the name of the desired recipient
	 * @return returns the received byte array
	 */
	protected byte[] receive(DatagramSocket receiveSocket, DatagramPacket receivePacket, String from, String to) {
		// Construct a DatagramPacket for receiving packets up 
		byte receivedData[] = new byte[100]; // while we know the exact size for this assignment, this won't always be the case
		receivePacket = new DatagramPacket(receivedData, receivedData.length);

		try {
		   // Block until a datagram is received via sendReceiveSocket.  
		   receiveSocket.receive(receivePacket);   
		   // sets the port based on who sent us a message
		   receiverPort = receivePacket.getPort();
		} 
		catch(SocketTimeoutException e) {
			System.out.println("Did not receive response for 10 seconds...Exiting");
			System.exit(-1);
		} 
		catch(IOException e) {
		   e.printStackTrace();
		   System.exit(1);
		}
		
		
		int len = receivePacket.getLength();
		
		receivedData = removeUnecessary(receivedData, len);
		
		return receivedData;
	}

}
