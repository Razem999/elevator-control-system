/**
 * 
 */
package main.floor;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import static java.time.temporal.ChronoUnit.MILLIS;

import main.common.Logger;
import main.common.PacketHandler;
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

	/**
	 * Constructor that accepts a scheduler and floor number
	 * @param scheduler
	 * @param floorNumber
	 */
	public FloorManager(int numFloors) {
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
     * Receives input from the input manager and forwards it to the Scheduler.
     */
    private void simulate() {
    	// TODO: need a better way of doing this
    	while (instructions.size() < 8) {
    		receiveInstruction();
    	}
    	while (!instructions.isEmpty()) {
    		sendInstruction(instructions.remove(0));
    	}
    }

	public static void main(String[] args) {
		FloorManager floor = new FloorManager(Constants.NUM_FLOORS);
		floor.simulate();
	}
}
