package main.gui;

import static org.junit.Assert.assertNotNull;

import main.common.Constants;
import main.common.Logger;
import main.common.PacketHandler;
import main.common.Input.FaultType;
import main.elevator.Elevator.ElevatorState;

/* 
 * Listener for values from a specific elevator for the model
 */
public class ElevatorListener implements Runnable {

	/* handles packets */
	private PacketHandler handler;
	/* the elevator number that we are listening to */
	private int elevatorNumber;
	/* The current floor of the elevator */
	private int currFloor;
	/* The next floor of the elevator */
	private int nextFloor;
	/* Current state of the elevator */
	private ElevatorState state;
	/* Current fault of the elevator, if none then this is null */
	private FaultType fault;
	/* Logger */
	private Logger logger;
	
	// ElevatorListener has access to these to update them
	
	/* Tracks the current floors of each elevator, index maps to elevator number */
	private int[] currentFloors;
	/* Tracks the next floors of each elevator, index maps to elevator number */
	private int[] nextFloors;
	/* Tracks the states of each elevator, index maps to elevator number */
	private ElevatorState[] states;
	/* Tracks the faults of each elevator, index maps to elevator number, if no fault, then value will be null */
	private FaultType[] elevatorFaults;
	
	
	public ElevatorListener(int elevatorNumber, int[] currentFloors, int[] nextFloors, ElevatorState[] states, FaultType[] elevatorFaults) {
		this.elevatorNumber = elevatorNumber;
		this.handler = new PacketHandler(69, Constants.MODEL_PORT + elevatorNumber);
		this.currFloor = 1;
		this.nextFloor = 1;
		this.state = ElevatorState.Idle;
		this.fault = null;
		
		// set the arrays to the arrays from the Model class
		this.currentFloors = currentFloors;
		this.nextFloors = nextFloors;
		this.states = states;
		this.elevatorFaults = elevatorFaults;
	}
	
	/**
	 * Takes the integer from a received packet and maps it to an ElevatorState.
	 * 0 = IDLE, 1 = MOVING, 2 = ARRIVING
	 * @param stateInt the integer
	 * @return ElevatorState 
	 */
	private ElevatorState parseStateFromPacket(int stateInt) {
		switch (stateInt) {
			case 0:
				return ElevatorState.Idle;
			case 1:
				return ElevatorState.Moving;
			case 2:
				return ElevatorState.Arriving;
			default:
				return ElevatorState.Idle;
		}
	}
	
	/**
	 * Takes the integer from a received packet and maps it to a FaultType.
	 * 0 = No Fault, 1 = MotorFault, 2 = OpenDoorFault, 3 = ClosedDoorFault
	 * @param faultInt the integer
	 * @return FaultType
	 */
	private FaultType parseFaultFromPacket(int faultInt) {
		switch (faultInt) {
			case 0:
				return null;
			case 1:
				return FaultType.MotorFault;
			case 2:
				return FaultType.OpenDoorFault;
			case 3:
				return FaultType.CloseDoorFault;
			default:
				return null;
		}
	}
	
	/** 
	 * Listens for updates from elevator and updates own values
	*/
	private void getUpdate() {
		byte[] received = handler.receiveTimeout(Constants.ELEVATOR_LISTENER_TIMEOUT);
		
		if (received == null) {
			
		}
		currFloor = (int) received[0];
		nextFloor = (int) received[1];
		state = parseStateFromPacket((int) received[2]);
		fault = parseFaultFromPacket((int) received[3]);
	}
	
	private void updateModel() {
		synchronized(currentFloors) {
			currentFloors[elevatorNumber] = currFloor;
		}
		
		synchronized(nextFloors) {
			nextFloors[elevatorNumber] = nextFloor;
		}
		
		synchronized(states) {
			states[elevatorNumber] = state;
		}
		
		synchronized(elevatorFaults) {
			elevatorFaults[elevatorNumber] = fault;
		}
	}
	
	@Override
	public void run() {
		while(true) {
			getUpdate();
			
			updateModel();
		}
	}

}
