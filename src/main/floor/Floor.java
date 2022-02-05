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
		// TODO: Add function that checks that the starting floor is this floor, the ending floor is different, and the direction is correct
		File input = new File(file); // should be passed in?
		try {
			// TODO: Possibly change this so floor N will only add instructions with a destination value of N			
			Scanner inputReader = new Scanner(input);
			while (inputReader.hasNextLine()) {
			  String line = inputReader.nextLine();
			  String[] commands = line.split(" ");
			  instructions.add(new Instructions(commands));
			}
			inputReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get getInput() for unit testing
	 * @param filename
	 */
	public void getGetInput(String file) {
		getInput(file);
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
