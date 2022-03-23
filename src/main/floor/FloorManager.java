/**
 * 
 */
package main.floor;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;
import static java.time.temporal.ChronoUnit.MILLIS;

import main.common.Logger;
import main.common.PacketHandler;
import main.common.Input.Fault;
import main.common.Input.Input;
import main.common.Input.Instructions;
import main.common.ByteConverter;
import main.common.Constants;

/**
 * Class to represent a floor subsystem
 */
public class FloorManager {
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
	/** list of inputs to process */
	private final ArrayList<Input> inputList;

	/**
	 * Constructor that accepts a scheduler and floor number
	 * @param scheduler
	 * @param floorNumber
	 */
	public FloorManager(int numFloors, String filename) {
		inputList = new ArrayList<>();
		this.button = new FloorButton();
		this.instructions = new ArrayList<>();
		this.packetHandler = new PacketHandler(Constants.SCHEDULER_PORT, Constants.FLOOR_MANAGER_PORT);

		// Fill in the array with floors from 1 to numFloors
		this.floors = new int[numFloors];
		for (int i = 1; i <= numFloors; i++) {
			floors[i-1] = i;
		}

		this.floorButtons = new FloorButton[numFloors];
		this.floorLamps = new FloorLamp[numFloors];
		
		this.logger = new Logger("FLOOR");
		logger.log("Starting...");
		
		getInput(filename);
	}
	
	/**
     * Verifies that the input line is valid and returns true if it is, false if it is not 
     * (i.e. current floor = destination, current floor != this floor, direction is wrong) 
     * 
     * @returns boolean
     */
    private boolean verifyInstructions(String[] commands) {
    	if (commands.length < 4) {
    		return false;
    	}
		// if the current and destination floors are the same
		if (commands[1].equals(commands[3])) {
			return false;
		}

		// make sure floors in the instruction are within legal bounds
		if (Integer.valueOf(commands[1]) < 1 || Integer.valueOf(commands[1]) > Constants.NUM_FLOORS) {
			return false;
		}

		if (Integer.valueOf(commands[3]) < 1 || Integer.valueOf(commands[3]) > Constants.NUM_FLOORS) {
			return false;
		}

		// if the previous checks passed, we just need to verify that the direction is correct
		if (Integer.valueOf(commands[1]) < Integer.valueOf(commands[3])) {
			return commands[2].equals("Up");
		}

		return commands[2].equals("Down");
	}
    
    /**
     * 
     * @param commands
     * @return
     */
    private boolean verifyFault(String[] commands) {
    	// TODO TODO TODO
    	return commands[1].equals("Type");
    }
    
    /**
	 * Reads in a text input file to populate the instructions
	 * @param file - string file name of input
	 */
	private void getInput(String file) {
		logger.log("Reading input file...");
		File input = new File(file);
		try {
			Scanner inputReader = new Scanner(input);
			while (inputReader.hasNextLine()) {
			  String line = inputReader.nextLine();
			  String[] commands = line.split(" ");

			  // check if they are valid instructions or faults
			  if (verifyInstructions(commands)) {
				  inputList.add(new Instructions(commands));
			  } else if (verifyFault(commands)) {
				  inputList.add(new Fault(commands));
			  }
			}
			logger.log("Added all inputs to input list");
			inputReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the instructions associated with this floor
	 * @return arrayList - array of instructions 
	 */
	public ArrayList<Instructions> getInstructions() {
		return instructions;
	}

    /**
     * Receives instructions from the input manager.
     */
    private void receiveInstruction() {
    	byte[] returned = packetHandler.receive();
    	// TODO: check here to see if it's valid instruction
    	Instructions ins = ByteConverter.byteArrayToInstructions(returned);
    	instructions.add(ins);
    	logger.log("Received instruction: " + ins);
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
	 * Sends an input to its corresponding destination based on its class.
	 * 
	 * @param input Input - input to send. May be Instructions or Fault.
	 */
	private void sendInput(Input input) {
		if (input instanceof Instructions) {
			sendInstruction((Instructions) input);
			logger.log("Sent instructions to floor manager");
		} else {
			Fault fault = (Fault) input;
			int targetPort = Constants.ELEVATOR_STARTING_PORT_NUMBER + fault.getElevatorId(); // uncomment other line when we change elevator ports
//			int targetPort = Constants.ELEVATOR_STARTING_PORT_NUMBER + fault.getElevatorId() * 10;
			PacketHandler elevatorHandler = new PacketHandler(targetPort);
			elevatorHandler.send(new byte[] { -1 }); // TODO: actual byte format for faults
			logger.log("Sent fault to elevator");
		}
	}
	
    /**
     * Receives input from the input manager and forwards it to the Scheduler.
     */
    private void simulate() {
    	LocalTime prevTime = null;
		for (Input input : inputList) {
			sendInput(input);
			if (prevTime != null) {
				try {
					// Sleep for the difference in time between each request
				    Thread.sleep(Math.abs(MILLIS.between(input.getTime(), prevTime)));
				} 
				catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
			prevTime = input.getTime();
		}
    }

	public static void main(String[] args) {
		FloorManager floor = new FloorManager(Constants.NUM_FLOORS, "mockInput.txt");
		floor.simulate();
	}
}
