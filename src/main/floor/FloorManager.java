/**
 * 
 */
package main.floor;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import static java.time.temporal.ChronoUnit.MILLIS;

import main.common.Logger;
import main.common.PacketHandler;
import main.common.Input.Fault;
import main.common.Input.FaultType;
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
	
	public boolean getVerifyInstructions(String[] commands) {
		return verifyInstructions(commands);
	}
	
	public boolean getVerifyFault(String[] commands) {
		return verifyFault(commands);
	}
	
	/**
     * Verifies that the input line is valid and returns true if it is, false if it is not 
     * (i.e. current floor = destination, current floor != this floor, direction is wrong) 
     * 
     * @returns boolean
     */
    private boolean verifyInstructions(String[] commands) {
    	// verify the length of the commands array
    	if (commands.length != 4) {
    		return false;
    	}
    	
		// if the current and destination floors are the same
		if (commands[1].equals(commands[3])) {
			return false;
		}
		
		// ensure the time is properly formatted
		try {
    		LocalTime.parse(commands[0]);
    	} catch (DateTimeParseException e) {
    		return false;
    	}
		
		int startFloor, endFloor;
		
		// check if commands[1] is not an integer
		try {
			startFloor = Integer.parseInt(commands[1]);
			endFloor = Integer.valueOf(commands[3]);
			
			// make sure floors in the instruction are within legal bounds
			if (startFloor < 1 || startFloor > Constants.NUM_FLOORS) {
				return false;
			}

			if (endFloor < 1 || endFloor > Constants.NUM_FLOORS) {
				return false;
			}

			// if the previous checks passed, we just need to verify that the direction is correct
			if (startFloor < endFloor) {
				return commands[2].equals("Up");
			}
			
			return commands[2].equals("Down");
			
		} catch (NumberFormatException e) {
			return false;
		}
	}
    
    /**
     * Verifies that the input line is a valid fault.
     * 
     * @param commands String[] - line split by spaces.
     * @return boolean - True if valid fault, False otherwise.
     */
    private boolean verifyFault(String[] commands) {  	
    	// verify the length of the commands array
    	if (commands.length != 3) {
    		return false;
    	}
    	
    	// ensure the time is properly formatted
    	try {
    		LocalTime.parse(commands[0]);
    	} catch (DateTimeParseException e) {
    		return false;
    	}
    	
    	int elevatorNum;
    	
    	try {
    		FaultType.valueOf(commands[1]);
    		elevatorNum = Integer.parseInt(commands[2]);
    		
    		// ensure that the elevator the fault affects exists 
    		// (i.e. is within the number of elevators instantiated)
    		if (elevatorNum >= Constants.NUM_CARS || elevatorNum < 0) {
    			return false;
    		}
    	} 
    	catch (NumberFormatException e) {
    		return false;
    	}
    	catch (IllegalArgumentException e) {
    		return false;
    	}
    	
    	return true;
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
				  Instructions ins = new Instructions(commands);
				  logger.log("Adding instruction: " + ins);
				  inputList.add(ins);
			  } else if (verifyFault(commands)) {
				  Fault fault = new Fault(commands);
				  logger.log("Adding fault: " + fault);
				  inputList.add(fault);
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
	public ArrayList<Input> getInputList() {
		return inputList;
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
     * Sends a fault to the specified elevator in the fault.
     * 
     * @param fault Fault - fault to be sent.
     */
    private void sendFault(Fault fault) {
    	// make a new packet handler to send to a specific elevator
    	int targetPort = Constants.ELEVATOR_STARTING_PORT_NUMBER + fault.getElevatorId();
    	PacketHandler elevatorHandler = new PacketHandler(targetPort);
    	// Set first byte flag based on fault type
    	byte firstByte = 0;
    	switch (fault.getType()) {
	    	case OpenDoorFault:
	    		firstByte = -2;
	    		break;
	    	case CloseDoorFault:
	    		firstByte = -3;
	    		break;
	    	case MotorFault:
	    	default:
	    		firstByte = -1;
	    		break;
    	}
    	// send fault to elevator
    	elevatorHandler.send(new byte[] { firstByte });
    }

	/**
	 * Sends an input to its corresponding destination based on its class.
	 * 
	 * @param input Input - input to send. May be Instructions or Fault.
	 */
	private void sendInput(Input input) {
		// determine type of input and go from there
		if (input instanceof Instructions) {
			Instructions ins = (Instructions) input;
			logger.log("Sending " + ins + " to scheduler.");
			sendInstruction(ins);
			logger.log("Sent instructions to scheduler.");
		} else {
			Fault fault = (Fault) input;
			logger.log("Sending " + fault + " to elevator " + fault.getElevatorId());
			sendFault(fault);
			logger.log("Sent fault to elevator.");
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
