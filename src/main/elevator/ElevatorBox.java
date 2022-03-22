package main.elevator;

import java.util.ArrayList;
import java.util.List;

import main.scheduler.ElevatorAgent;

public class ElevatorBox {

	/**
	 * ArrayList to keep track of elevator agents
	 */
	private ArrayList<ElevatorAgent> agents;
	/**
	 * Reference of the Elevator Door
	 */
	private ElevatorDoor eDoor;
	/**
	 * Reference of the Elevator Motor
	 */
	private ElevatorMotor eMotor;
	private boolean error;
	    
	
	public synchronized void passengerEnter() {
		eDoor.openDoor();
	    while (error) {
	        try {
	            Thread.sleep(4000);
	        } catch (InterruptedException e) {
	            return;
	        }
	    }
	    eDoor.closeDoor();
	    // Elevator starts moving (motor methods)
	    
	}
	    
	public synchronized void passengerExit() {
		eDoor.openDoor();
	    while (error) {
	        try {
	            Thread.sleep(4000);
	        } catch (InterruptedException e) {}
	    }
	    eDoor.closeDoor();
	    // Elevator is idle/moving (check for instructions)
	}
	
	

}
	
