/**
 * 
 */
package main.floor;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import static java.time.temporal.ChronoUnit.MILLIS;

import main.common.Instructions;
import main.common.Logger;
import main.common.PacketHandler;
import main.common.ByteConverter;
import main.common.Constants;


/**
 * Class to represent a floor subsystem
 */
public class FloorManager {
	private static final int SCHEDULER_PORT = 24;
	private static final int FLOOR_MANAGER_PORT = 50;
	
	/** logger instance to handle console logging */
	private Logger logger;
	/** Button for users to interact with */
	private FloorButton button;
	/** Array to track all floors that exist */
	private int[] floors;
	/** Array of all floorButtons */
	private FloorButton[] floorButtons;
	/** Array of all floorLamps */
	private FloorLamp[] floorLamps;
	/** Instructions sent from this Floor to the Scheduler */
	private ArrayList<Instructions> instructions;
	/** Tracks time spent without processing instructions */
	private int timeWithoutRequests = 0;
	/** PacketHandler for dealing with UDP communication */
	private PacketHandler packetHandler;
	
	/**
	 * Constructor that accepts a scheduler and floor number
	 * @param scheduler
	 * @param floorNumber
	 */
	public FloorManager(int numFloors) {
		this.button = new FloorButton();
		this.instructions = new ArrayList<>();
		this.packetHandler = new PacketHandler(SCHEDULER_PORT, FLOOR_MANAGER_PORT); // TODO: decide on port for schedulers then change this
		 
		// Fill in the array with floors from 1 to numFloors
		this.floors = new int[numFloors];
		for (int i = 1; i <= numFloors; i++) {
			floors[i-1] = i;
		}
		
		this.floorButtons = new FloorButton[numFloors];
		this.floorLamps = new FloorLamp[numFloors];
		
		this.logger = new Logger("FLOOR");
		logger.log("Starting...");
	}
	
	/**
	 * Get getInput() for unit testing
	 * @param filename
	 */
	public void getGetInput(String file) {
		getInput(file);
	}
	
	/**
	 * Get verifyInput() for unit testing
	 * @param commands
	 */
	public boolean getVerifyInput(String[] commands) {
		return verifyInput(commands);
	}
	
	/**
	 * Returns the instructions associated with this floor
	 * @return arrayList - array of instructions 
	 */
	public ArrayList<Instructions> getInstructions() {
		return instructions;
	}
	
	/**
	 * Reads in a text input file to populate the instructions
	 * @param file - string file name of input
	 */
	private void getInput(String file) {
		logger.log("Reading input file...");
		File input = new File(file); // should be passed in?
		try {
			// TODO: Possibly change this so floor N will only add instructions with a destination value of N			
			Scanner inputReader = new Scanner(input);
			while (inputReader.hasNextLine()) {
			  String line = inputReader.nextLine();
			  String[] commands = line.split(" ");
			  if (verifyInput(commands)) {
				  instructions.add(new Instructions(commands));
			  }
			}
			logger.log("Added all instructions to floor");
			inputReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Verifies that the input line is valid and returns true if it is, false if it is not 
     * (i.e. current floor = destination, current floor != this floor, direction is wrong) 
     * 
     * @returns boolean
     */
    private boolean verifyInput(String[] commands) {
		// if the current and destination floors are the same
		if (commands[1].equals(commands[3])) {
			return false;
		}
		
		// make sure floors in the instruction are within legal bounds
		if (Integer.valueOf(commands[1]) < 1 || Integer.valueOf(commands[1]) > floors.length) {
			return false;
		}
		
		
		if (Integer.valueOf(commands[3]) < 1 || Integer.valueOf(commands[3]) > floors.length) {
			return false;
		}
		               
		// if the previous checks passed, we just need to verify that the direction is correct
		if (Integer.valueOf(commands[1]) < Integer.valueOf(commands[3])) {
			return commands[2].equals("Up");
		}

		return commands[2].equals("Down");
	}
	
    /**
     * Sends an instruction to the scheduler to be handled by an elevator
     * @returns boolean representing whether the instruction was received by the scheduler or not
     */
    private boolean sendInstruction(Instructions instruction) {
    	byte[] insArr = ByteConverter.instructionToByteArray(instruction);
    	
    	packetHandler.send(insArr);
    	byte[] returned = packetHandler.receive();
    	logger.log("Response: " + new String(returned, StandardCharsets.UTF_8));
    	return returned[0] == (byte) 0; 
    }
    
	/**
	 * Method for the floor simulator to read in and send instructions
	 */
	public void simulate() {
		// read instructions from file into instructions array
		getInput("mockInput.txt");
		
		if(sendInstruction(instructions.get(0))) {
			logger.log("Successfully sent instruction: " + instructions.get(0).toString());
		}
		
		for (int i = 1; i < instructions.size(); i++) {
			try {
				// Sleep for the difference in time between each request
			    Thread.sleep(Math.abs(MILLIS.between(instructions.get(i).getTime(), instructions.get(i - 1).getTime())));
			} 
			catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
			if(sendInstruction(instructions.get(i))) {
				logger.log("Successfully sent instruction: " + instructions.get(i).toString());
			}
		}
	}

	public static void main(String[] args) {
		FloorManager floor = new FloorManager(Constants.NUM_FLOORS);
		floor.simulate();
	}
}
