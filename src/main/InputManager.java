/**
 * 
 */
package main;

import static java.time.temporal.ChronoUnit.MILLIS;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

import main.common.ByteConverter;
import main.common.Constants;
import main.common.Logger;
import main.common.PacketHandler;
import main.common.Input.Fault;
import main.common.Input.Input;
import main.common.Input.Instructions;

/**
 * Overarching class that reads in test input files and sends
 * instructions/faults to their corresponding subsystems
 */
public class InputManager {
	/** list of inputs to process */
	private final ArrayList<Input> inputList;
	/** packet handler to handle udp communication with floor */
	private final PacketHandler floorHandler;
	/** private logger instance */
	private final Logger logger;
	
	/**
	 * Constructor that accepts a filename parameter and loads the instructions from said file.
	 * 
	 * @param filename String
	 */
	public InputManager(String filename) {
		inputList = new ArrayList<>();
		floorHandler = new PacketHandler(Constants.FLOOR_MANAGER_PORT);
		logger = new Logger("INPUT-MGR");
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
	 * Sends an input to its corresponding destination based on its class.
	 * 
	 * @param input Input - input to send. May be Instructions or Fault.
	 */
	private void sendInput(Input input) {
		if (input instanceof Instructions) {
			floorHandler.send(ByteConverter.instructionToByteArray((Instructions) input));
			logger.log("Sent instructions to floor manager");
		} else {
			Fault fault = (Fault) input;
			int targetPort = Constants.ELEVATOR_STARTING_PORT_NUMBER + fault.getElevatorId(); // uncomment other line when we change elevator ports
//			int targetPort = Constants.ELEVATOR_STARTING_PORT_NUMBER + fault.getElevatorId() * 10;
			PacketHandler elevatorHandler = new PacketHandler(targetPort);
			elevatorHandler.send(new byte[] { -1 }); // TODO: actual byte format for faults
			logger.log("Sent instructions to elevator");
		}
	}
	
	/**
	 * Method for the floor simulator to read in and send instructions
	 */
	public void simulate() {
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
		InputManager inmgr = new InputManager("mockInput.txt");
		inmgr.simulate();
	}
}
