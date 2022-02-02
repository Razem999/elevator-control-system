/**
 * 
 */
package main.floor;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import main.common.Instructions;
import main.elevator.Elevator;
import main.scheduler.Scheduler;

/**
 * Class to represent a floor subsystem
 */
public class Floor {
	/** List of elevators */
	private ArrayList<Elevator> elevators; // maybe should be deleted
	/** Button for users to interact with */
	private FloorButton direction;
	/** Associated floor number */
	private int floorNumber;
	/** The Scheduler which handles this Floor's inputs */
	private Scheduler scheduler;
	/** Instructions sent from this Floor to the Scheduler */
	private Instructions instructions;
	
	/**
	 * Constructor that accepts all instance variables
	 * @param elevators
	 * @param direction
	 * @param floorNumber
	 */
	public Floor(ArrayList<Elevator> elevators, FloorButton direction, int floorNumber, Scheduler scheduler) {
		this.elevators = elevators;
		this.direction = direction;
		this.floorNumber = floorNumber;
		this.scheduler = scheduler;
	}
	
	/**
	 * 
	 */
	public void getInput() {
		File input = new File("mockInput.txt"); // should be passed in?
		if (input.exists()) {
			Scanner inputReader;
			try {
				inputReader = new Scanner(input);
				
				while (inputReader.hasNextLine()) {
				  String line = inputReader.nextLine();
				  String[] commands = line.split(" ");
				  instructions = new Instructions(commands[0], commands[1], commands[2], commands[3]);
				  
				  System.out.println(instructions.toString());
				  scheduler.setInstructions(instructions);
				  
				  System.out.println(Arrays.toString(commands));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
//				System.exit(0);
				return;
			}
			
			inputReader.close();
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
