package main.common;

/**
 * Class with constant variables
 */
public class Constants {
	/** The amount of active Elevators */
	public static final int NUM_CARS = 2;
	/** The amount of active Floors */
	public static final int NUM_FLOORS = 6;
	
	//	Elevator Ports
	
	/** This value is used in conjunction with the Elevator number to assign each Elevator a unique port number */
	public static final int ELEVATOR_STARTING_PORT_NUMBER = 1000;
	/** This value is used in conjunction with the ELEVATOR_STARTING_PORT_NUMBER to assign each Elevator a unique port number, incremented by 10*/
	public static final int NEW_ELEVATOR_INCREMENT = 10; 
	/** The amount of time it takes for an Elevator to move floors */
	public static final int ELEVATOR_TIME_BETWEEN_FLOORS = 2000;
	/** This value is used in conjunction with the Elevator number to assign each Elevator Agent a unique port number */
	public static final int ELEVATOR_AGENT_STARTING_PORT_NUMBER = 2000;
	/** The max byte array size that we will support sending over UDP */
	public static final int MAX_BUFFER_SIZE = 100;
	/** The time a UDP Listener will wait before timing out */
	public static final int TIMEOUT = 20000;
	/** the port number for the floor facing scheduler */
	public static final int SCHEDULER_PORT = 24;
	/** the port number for the floor facing scheduler in unit tests */
	public static final int SCHEDULER_TEST_PORT = 23;
	/** Number of cycles in an idle state that an elevator will stay active for before exiting */
	public static final int IDLE_EXIT_COUNT = 10;
}
