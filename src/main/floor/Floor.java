/**
 * 
 */
package main.floor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import main.common.Instructions;
import main.scheduler.Scheduler;

/**
 * Class to represent a floor subsystem
 */
public class Floor implements Runnable {
	/** Button for users to interact with */
	private FloorButton button;
	/** Associated floor number */
	private int floorNumber;
	/** The Scheduler which handles this Floor's inputs */
	private Scheduler scheduler;
	/** Instructions sent from this Floor to the Scheduler */
	private ArrayList<Instructions> instructions;
	
	/**
	 * Constructor that accepts a scheduler and floor number
	 * @param scheduler
	 * @param floorNumber
	 */
	public Floor(Scheduler scheduler, int floorNumber) {
		this.button = new FloorButton();
		this.floorNumber = floorNumber;
		this.scheduler = scheduler;
		this.instructions = new ArrayList<>();
		getInput("mockInput.txt");
	}
	
	/**
	 * Reads in a text input file to populate the instructions
	 * @param file - string file name of input
	 */
	private void getInput(String file) {
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
		              
		// if the current floor of the command doesn't match this floor
		if (Integer.valueOf(commands[1]) != this.floorNumber) {
			return false;
		}
		               
		// if the two previous checks passed, we just need to verify that the direction is correct
		if (Integer.valueOf(commands[1]) < Integer.valueOf(commands[3])) {
			return commands[2].equals("Up");
		}

		return commands[2].equals("Down");
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
	 * Main loop to add all instructions to the scheduler and then wait on requests to complete
	 */
	public void run() {
		while (true) {
			synchronized(scheduler) {
				while (!instructions.isEmpty()) {
					System.out.println("[FLOOR " + floorNumber + "]: Sending instructions to the Scheduler");
					scheduler.addInstructions(instructions.remove(0));
				}
				if (scheduler.notifyFloor(floorNumber)) {
					System.out.println("[FLOOR " + floorNumber + "]: An elevator has reached me");
					System.out.println(scheduler);
				}
			}
		}
	}
	
	
	/**
	 * Returns the floor number associated with this floor
	 * @return int - associated floor number
	 */
	public int getFloorNumber() {
		return floorNumber;
	}
	
	/**
	 * Sets the associated floor number of this floor
	 * @param floor int - the new floor number
	 */
	public void setFloorNumber(int floor) {
		floorNumber = floor;
	}
	
	/**
	 * Returns the instructions associated with this floor
	 * @return arrayList - array of instructions 
	 */
	public ArrayList<Instructions> getInstructions() {
		return instructions;
	}
}
