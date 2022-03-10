package main.scheduler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

import main.common.ByteConverter;
import main.common.Direction;
import main.common.Instructions;
import main.common.Logger;
import main.common.PacketHandler;
import main.elevator.ElevatorButton;
import main.floor.FloorButton;

/**
 * Class to represent the backend server to handle communication between
 * elevator components
 */
public class Scheduler implements Runnable{
	private static final int FLOOR_MANAGER_PORT = 50;
	/** logger instance to handle console logging */
	private Logger logger;
	/**
	 * Queue for the instructions
	 */
	private ArrayList<Instructions> queue;
	/**
	 * Arraylist of completed instructions
	 */
	private ArrayList<Instructions> completed;
	private int numCompleted; // temporary variable for stopping program
	
	/**
	 * Variable to track the current state of scheduler
	 */
	private SchedulerStates currState = SchedulerStates.LISTENING;
	
	/**
	 * ENUM to represent the scheduler states
	 */
	public enum SchedulerStates {
		LISTENING { // Reading in instructions from floor
			public String toString() {
				return "LISTENING";
			}
		},
		DELEGATING { // Sending instructions to an elevator
			public String toString() {
				return "DELEGATING";
			}
		},
		CHANGEFLOOR { // State when elevator reaches its destination floor and scheduler logs this
			public String toString() {
				return "CHANGEFLOOR";
			}
		},
		PROCESSARRIVAL { // State when floor confirms elevator reaches it and scheduler logs this
			public String toString() {
				return "FLOORARRIVES";
			}
		}
	};
	

	/** PacketHandler for dealing with UDP communication */
	private PacketHandler packetHandler;
	
	/**
	 * Default constructor
	 */
	public Scheduler(boolean floorFacing, int portNum) {
		queue = new ArrayList<>();
		completed = new ArrayList<>();
		numCompleted = 0;
		this.logger = new Logger("SCHED");
		logger.log("Starting...");
		if (floorFacing) {
			packetHandler = new PacketHandler(FLOOR_MANAGER_PORT, portNum);
		} else {
			packetHandler = new PacketHandler(0, portNum);
		}
	}
	
	/**
	 * Getter for numCompleted
	 */
	public int getNumCompleted() {
		return numCompleted;
	}
	
	
	// getter for unit tests
	public SchedulerStates getState() {
		return currState;
	}
	
	/**
	 * Function to send messages to floor
	 * 
	 */
	public synchronized boolean notifyFloor(int floorNumber) {
		while (!hasCompleted()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		currState = SchedulerStates.PROCESSARRIVAL;
//		boolean elevatorHasReachedFloor = false;
		// for now, just return true and pop one element off of completed
		// once other logic and more floors are added, we can handle those cases
		logger.log("Removing instructions from completed...");
		logger.log("Instructions removed: " + completed.remove(0));
		logger.log("Current state: " + currState + "\n" + this);
		logger.log("Notifying floor " + floorNumber);
		numCompleted += 1;
		return true;
	}

	/**
	 * Function to send messages to elevator
	 * 
	 */
	public void notifyElevator() {

	}

	/**
	 * Adds instructions to the queue
	 * @param instructions
	 */
	public synchronized void addInstructions(Instructions instructions) {;
		currState = SchedulerStates.LISTENING;
		
		logger.log("Adding instructions to queue...");
		queue.add(instructions);
		logger.log("Instructions added: " + instructions);
		logger.log("Current state: " + currState + "\n" + this);
		logger.log("Notifying all...\n");
		notifyAll();
	}

	/**
	 * Checks if the queue is not empty with instructions
	 * @return boolean
	 */
	public boolean hasInstructions() {
		return !queue.isEmpty();
	}
	
	/**
	 * Checks if the completed queue is not empty with instructions
	 * @return boolean
	 */
	public boolean hasCompleted() {
		return !completed.isEmpty();
	}

	/**
	 * Removes the instructions from the queue and returns it
	 * @return the instruction that was removed
	 */
	public synchronized Instructions popInstructions() {
		while (!hasInstructions()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		currState = SchedulerStates.DELEGATING;
		
		logger.log("Removing instructions from queue...");
		Instructions removed = queue.remove(0);
		logger.log("Removed instructions: " + removed);
		logger.log("Current state: " + currState + "\n" + this);
		logger.log("Notifying all...");
		notifyAll();
		logger.log("Sending instructions to elevator\n");
		return removed;
	}

	/** 
	 * Adds the given instructions to the listed of completed instructions
	 * @param instructions
	 */
	public synchronized void completeInstructions(Instructions instructions) {
		currState = SchedulerStates.CHANGEFLOOR;
		logger.log("Adding instructions to completed...");
		completed.add(instructions);
		logger.log("Instructions added: " + instructions);
		logger.log("Current state: " + currState + "\n" + this);
		logger.log("Notifying all...\n");
		notifyAll();
	}

	/**
	 * Function that returns the value of an elevator button press (which floor has
	 * been requested)
	 * 
	 * @return an integer representing the requested floor
	 */
	private int getElevatorButtonPress(ElevatorButton elevatorButton) {
		return elevatorButton.getButtonPressed();
	}

	/**
	 * Function that returns the value of a floor button press (elevator going up or
	 * down)
	 * 
	 * @return an enum value (UP/DOWN)
	 */
	private Direction getFloorButtonPress(FloorButton floorButton) {
		return floorButton.getDirectionalLamp();
	}

	/**
	 * Pretty string to show the scheduler's queue and completed arrays
	 * @return string
	 */
	public String toString() {
		return "Q:" + queue + "\nC:" + completed;
	}
	
	public void run() {
		byte[] response;
		Instructions instruction;
		String stringResponse;
		while (true) {
			logger.log("Waiting..");
			response = packetHandler.receive();
			stringResponse = new String(response, StandardCharsets.UTF_8).substring(0,2);
			if (stringResponse.equals("UP") || stringResponse.equals("DO")) {

				logger.log("Received Instruction");
				instruction = ByteConverter.byteArrayToInstructions(response);
				packetHandler.send(new byte[]{0});
				//Optimize here
				logger.log("Sent Instruction");
				
			}
		}
	}
	
	public static void main(String[] args) {
		Thread sched1 = new Thread(new Scheduler(true, 24), "Floor Facing");
//		Thread sched2 = new Thread(new Scheduler(false, 23), "Elevator Facing");
		sched1.start();
//		sched2.start();
	}
}
