package main.elevator;

import java.util.ArrayList;
import java.util.List;

import main.scheduler.ElevatorAgent;

public class ElevatorBox {

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
		Thread.sleep(Constants.DOOR_OPEN);
	    while (error) {
	        try {
	            Thread.sleep(Constants.DOOR_OPEN);
	        } catch (InterruptedException e) {
	            return;
	        }
	    }
	    eDoor.closeDoor();
	    // Elevator starts moving (motor methods)
	    
	}
	    
	public synchronized void passengerExit() {
		eDoor.openDoor();
		Thread.sleep(Constants.DOOR_OPEN);
	    while (error) {
	        try {
	            Thread.sleep(Constants.DOOR_OPEN);
	        } catch (InterruptedException e) {}
	    }
	    eDoor.closeDoor();
	    // Elevator is idle/moving (check for instructions)
	}
	
	

}
	
