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
		getInput();
	}
	
	/**
	 * 
	 */
	public void getInput() {
		File input = new File("mockInput.txt"); // should be passed in?
		try {
			Scanner inputReader = new Scanner(input);
			while (inputReader.hasNextLine()) {
			  String line = inputReader.nextLine();
			  String[] commands = line.split(" ");
			  instructions.add(new Instructions(commands[0], commands[1], commands[2], commands[3]));
			}
			inputReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void run() {
		while (true) {
			synchronized(scheduler) {
				while (!instructions.isEmpty()) {
					scheduler.addInstructions(instructions.remove(0));
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
}
