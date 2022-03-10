package main.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


/**
 * Class that contains generic methods for dealing with UDP communication
 */
public class PacketHandler {
	private static final int MAX_BUFFER_SIZE = 100;
	
	private static final int TIMEOUT = 10000;
	
	private DatagramPacket sendPacket, receivePacket;
	
	private DatagramSocket sendReceiveSocket;
	
	private int receiverPort; // the port of the entity that we will communicate with
	
	public PacketHandler() {
		receiverPort = -1;
	}
	
	public PacketHandler(int receiverPort) {
		this.receiverPort = receiverPort;
		
		try {
		    // Construct a datagram socket and bind it to any available 
			// port on the local host machine. This socket will be used to
			// send and receive UDP Datagram packets.
			sendReceiveSocket = new DatagramSocket();
			sendReceiveSocket.setSoTimeout(TIMEOUT); // socket closes after 10 seconds with nothing received
		} catch (SocketException se) {   // Can't create the socket.
		    se.printStackTrace();
		    System.exit(1);
		}
	}
	
	public void setReceiverPort(int receiverPort) {
		this.receiverPort = receiverPort;
	}
	
	/**
	 * Create new array that is only the size we need, so we don't send unnecessary data via UDP
	 * @param message original byte array
	 * @param true length of message
	 * @return message with ending zeroes removed
	 */
	private byte[] trimBuffer(byte[] message, int length) {
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
	public void send(byte[] message) {
		createPacket(message);
		
		// Send the datagram packet to the server via the send/receive socket. 
		try {
		   sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
		   e.printStackTrace();
		   System.exit(1);
		}
	}
	
	/**
	 * Helper function to create a packet with the given byte array message
	 * @param message the message to create a packet with
	 * @return void
	 */
	private void createPacket(byte[] message) {
		// Attempt to send packet to the passed in port
		try {
		   sendPacket = new DatagramPacket(message, message.length,
		                                  InetAddress.getLocalHost(), receiverPort);
		} catch (UnknownHostException e) {
		   e.printStackTrace();
		   System.exit(1);
		}
	}
	
	/**
	 * Helper function for receiving DatagramPacket over a DatagramSocket
	 * @param receiveSocket the socket to be used
	 * @param receivePacket the packet to be sent
	 * @param from the name of the sender
	 * @param to the name of the desired recipient
	 * @return returns the received byte array
	 */
	public byte[] receive() {
		// Construct a DatagramPacket for receiving packets up 
		byte receivedData[] = new byte[MAX_BUFFER_SIZE];
		receivePacket = new DatagramPacket(receivedData, receivePacket.getLength());

		try {
		   // Block until a datagram is received via sendReceiveSocket.  
		   sendReceiveSocket.receive(receivePacket);   
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
		
		receivedData = trimBuffer(receivedData, receivePacket.getLength());
		
		return receivedData;
	}
}
