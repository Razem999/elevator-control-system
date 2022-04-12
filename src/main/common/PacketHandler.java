package main.common;

import java.io.IOException;
import java.net.*;

/**
 * Class that contains generic methods for dealing with UDP communication
 */
public class PacketHandler {
	/** packets to be used for UDP */
	private DatagramPacket sendPacket, receivePacket;
	/** socket to be used for UDP */
	private DatagramSocket sendReceiveSocket;
	/** the port of the entity that we will communicate with */
	private int sendPort;
	
	public PacketHandler(int sendPort) {
		this.sendPort = sendPort;
		try {
			sendReceiveSocket = new DatagramSocket();
			sendReceiveSocket.setSoTimeout(Constants.TIMEOUT);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	public PacketHandler(int sendPort, int receivePort) {
		this.sendPort = sendPort;

		try {
			// Construct a datagram socket and bind it to any available
			// port on the local host machine. This socket will be used to
			// send and receive UDP Datagram packets.
			sendReceiveSocket = new DatagramSocket(receivePort);
			sendReceiveSocket.setSoTimeout(Constants.TIMEOUT); // socket closes after 10 seconds with nothing received
		} catch (SocketException se) { // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}
	}

	public PacketHandler(int sendPort, int receivePort, int timeout) {
		this(sendPort, receivePort);
		try {
			sendReceiveSocket.setSoTimeout(timeout);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void setSendPort(int sendPort) {
		this.sendPort = sendPort;
	}

	/**
	 * Create new array that is only the size we need, so we don't send unnecessary
	 * data via UDP
	 * 
	 * @param message original byte array
	 * @param length true length of message
	 * @return message with ending zeroes removed
	 */
	private byte[] trimBuffer(byte[] message, int length) {
		// create new array that is only the size we need, so we don't send unnecessary
		// data via UDP
		byte[] receivedRightSize = new byte[length];
		System.arraycopy(message, 0, receivedRightSize, 0, length);

		return receivedRightSize;
	}

	/**
	 * Helper function for sending DatagramPacket over a DatagramSocket to this class's main communication partner
	 * 
	 * @param message the message to send
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
	 * Helper function for sending DatagramPacket over a DatagramSocket to a
	 * specified port
	 * 
	 * @param message the message to send
	 * @param sendPort the port to send to
	 */
	public void send(byte[] message, int sendPort) {
		createPacket(message, sendPort);

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
	 * 
	 * @param message the message to create a packet with
	 * @return void
	 */
	private void createPacket(byte[] message) {
		// Attempt to send packet to the passed in port
		try {
			sendPacket = new DatagramPacket(message, message.length, InetAddress.getLocalHost(), sendPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Helper function to create a packet with the given byte array message
	 * This one allows us to specify a port to send to
	 * 
	 * @param message the message to create a packet with
	 * @param port to send to
	 * @return void
	 */
	private void createPacket(byte[] message, int sendPort) {
		// Attempt to send packet to the passed in port
		try {
			sendPacket = new DatagramPacket(message, message.length, InetAddress.getLocalHost(), sendPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Helper function for receiving DatagramPacket over a DatagramSocket
	 * 
	 * @return returns the received byte array
	 */
	public byte[] receive() {
		// Construct a DatagramPacket for receiving packets up
		byte receivedData[] = new byte[Constants.MAX_BUFFER_SIZE];
		receivePacket = new DatagramPacket(receivedData, Constants.MAX_BUFFER_SIZE);

		try {
			// Block until a datagram is received via sendReceiveSocket.
			sendReceiveSocket.receive(receivePacket);
			// sets the port based on who sent us a message
			sendPort = receivePacket.getPort();
		} catch (SocketTimeoutException e) {
			System.out.println("Did not receive response for " + Constants.TIMEOUT/1000 + " seconds...Exiting");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		receivedData = trimBuffer(receivedData, receivePacket.getLength());

		return receivedData;
	}
	
	/**
	 * Helper function for receiving DatagramPacket over a DatagramSocket without
	 * crashing on timeout
	 * 
	 * @param timeout time in milliseconds it takes to timeout
	 * @return returns the received byte array, or null if the receive times out
	 */
	public byte[] receiveTimeout(int timeout) {
		// Construct a DatagramPacket for receiving packets up
		byte receivedData[] = new byte[Constants.MAX_BUFFER_SIZE];
		receivePacket = new DatagramPacket(receivedData, Constants.MAX_BUFFER_SIZE);

		try {
			sendReceiveSocket.setSoTimeout(timeout);
			// Block until a datagram is received via sendReceiveSocket.
			sendReceiveSocket.receive(receivePacket);
		} catch (SocketTimeoutException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		receivedData = trimBuffer(receivedData, receivePacket.getLength());

		return receivedData;
	}

	/**
	 * Closes the sendReceiveSocket, this should be the last PacketHandler function called 
	 */
	public void shutDown() {
		sendReceiveSocket.close();
	}
}
