package main.gui;

import java.util.ArrayList;
import java.util.Collections;

import main.common.Constants;
import main.common.Input.FaultType;
import main.elevator.Elevator.ElevatorState;
import main.common.PacketHandler;


/* 
 * The source of truth for the GUI that will hold values 
 */
public class Model implements Runnable {
	
	/* Tracks the current floors of each elevator, index maps to elevator number */
	private int[] currentFloors;
	/* Tracks the next floors of each elevator, index maps to elevator number */
	private int[] nextFloors;
	/* Tracks the states of each elevator, index maps to elevator number */
	private ElevatorState[] states;
	/* Tracks the faults of each elevator, index maps to elevator number, if no fault, then value will be null */
	private FaultType[] elevatorFaults;
	/* Listeners that get info from each individual elevator and updates the values in the arrays */
	private ArrayList<ElevatorListener> elevatorListeners;
	
	public Model() {
		currentFloors = new int[Constants.NUM_CARS];
		nextFloors = new int[Constants.NUM_CARS];
		states = new ElevatorState[Constants.NUM_CARS];
		elevatorFaults = new FaultType[Constants.NUM_CARS];
		
		elevatorListeners = new ArrayList<>();
		
		for (int i = 0; i < Constants.NUM_CARS; i++) {
			elevatorListeners.add(new ElevatorListener(i, currentFloors, nextFloors, states, elevatorFaults));
		}
	}
	
	/* 
	 * Getters for view class 
	 */
	public int[] getCurrentFloors() {
		return currentFloors;
	}
	
	public int[] getNextFloors() {
		return nextFloors;
	}
	
	public ElevatorState[] getStates() {
		return states;
	}
	
	public FaultType[] getFaults() {
		return elevatorFaults;
	}
	
	private void startListeners() {
		// start elevator agents
		Thread[] listenerThreads = new Thread[Constants.NUM_CARS];
		for (int i = 0; i < elevatorListeners.size(); i++) {
			listenerThreads[i] = new Thread(elevatorListeners.get(i));
			listenerThreads[i].start();
		}
	}
	
	
	public void run() {
		startListeners();
		while (true) {
			for (int i = 0; i < Constants.NUM_CARS; i++) {
				System.out.println("ELEV " + i + " " + currentFloors[i] + " " + nextFloors[i] + " " + states[i] + " " + elevatorFaults[i]);
			}
			System.out.println();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Model model = new Model();
		
		model.run();
	}
	
}
