package main.gui;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

import main.common.Constants;
import main.common.Direction;
import main.common.Logger;
import main.common.Input.FaultType;
import main.elevator.Elevator.ElevatorState;

/* 
 * The source of truth for the GUI that will hold values 
 */

public class Model {

	
	/* Tracks the current floors of each elevator, index maps to elevator number */
	private int[] currentFloors;
	/* Tracks the next floors of each elevator, index maps to elevator number */
	private int[] nextFloors;
	/* Tracks the direction in which the elevator is traveling to */
	private Direction[] directions;
	/* Tracks the states of each elevator, index maps to elevator number */
	private ElevatorState[] states;
	/* Tracks the faults of each elevator, index maps to elevator number, if no fault, then value will be null */
	private FaultType[] elevatorFaults;
	/* Listeners that get info from each individual elevator and updates the values in the arrays */
	private ArrayList<ElevatorListener> elevatorListeners;
	/* logger */
	private Logger logger;
	
	public Model() {
		currentFloors = new int[Constants.NUM_CARS];
		nextFloors = new int[Constants.NUM_CARS];
		directions = new Direction[Constants.NUM_CARS];
		states = new ElevatorState[Constants.NUM_CARS];
		elevatorFaults = new FaultType[Constants.NUM_CARS];
		
		
		elevatorListeners = new ArrayList<>();
		
		for (int i = 0; i < Constants.NUM_CARS; i++) {
			elevatorListeners.add(new ElevatorListener(i, currentFloors, nextFloors, directions, states, elevatorFaults, this));
		}
		
		logger = new Logger("Model");
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
	
	public Direction[] getDirection() {
		return directions;
	}
	
	public ElevatorState[] getStates() {
		return states;
	}
	
	public FaultType[] getFaults() {
		return elevatorFaults;
	}
	
	/* Start the listener threads */
	public void startListeners() {
		// start elevator agents
		Thread[] listenerThreads = new Thread[Constants.NUM_CARS];
		for (int i = 0; i < elevatorListeners.size(); i++) {
			listenerThreads[i] = new Thread(elevatorListeners.get(i));
			listenerThreads[i].start();
		}
	}
	
	/* start the model */
	public void start() {
		startListeners();
		// Since this is instantiated within the view class, we do not need to exit explicitly since the program terminates once the user exits the view window
		while (true) {
			for (int i = 0; i < Constants.NUM_CARS; i++) {
				logger.log("ELEV " + i + " " + currentFloors[i] + " " + nextFloors[i] + " " + directions[i] + " " + states[i] + " " + elevatorFaults[i]);
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
