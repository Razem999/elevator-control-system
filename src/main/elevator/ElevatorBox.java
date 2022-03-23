package main.elevator;

import java.util.ArrayList;
import java.util.List;

import main.common.Constants;

public class ElevatorBox {

	/**
	 * Reference of the Elevator Door
	 */
	private ElevatorDoor eDoor;
	/**
	 * Reference of the Elevator Motor
	 */
	private ElevatorMotor eMotor;
	/**
	 * Faults that arise when the elevator performs in an unexpected way
	 */
	private boolean error;
	    
	
	public synchronized void passengerEnter() {
		eDoor.toggleDoorState();
		try {
			Thread.sleep(Constants.DOOR_OPEN_TIME);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    while (error) {
	        try {
	            Thread.sleep(Constants.DOOR_OPEN_TIME);
	        } catch (InterruptedException e) {
	            return;
	        }
	    }
	    eDoor.toggleDoorState();
	    // Elevator starts moving (motor methods)
	    
	}
	    
	public synchronized void passengerExit() {
		eDoor.toggleDoorState();
		try {
			Thread.sleep(Constants.DOOR_OPEN_TIME);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    while (error) {
	        try {
	            Thread.sleep(Constants.DOOR_OPEN_TIME);
	        } catch (InterruptedException e) {}
	    }
	    eDoor.toggleDoorState();
	    // Elevator is idle/moving (check for instructions)
	}
	
	

}
	
