package main.common;

/**
 * Class with constant variables
 */
public class Constants {
	// Overall options
	
	/** The amount of active Elevators */
	public static final int NUM_CARS = 4;

	/** The amount of active Floors */
	public static final int NUM_FLOORS = 22;
	/** The max byte array size that we will support sending over UDP */
	public static final int MAX_BUFFER_SIZE = 100;
	/** The time a UDP Listener will wait before timing out */
	public static final int TIMEOUT = 20000;
	
	// Elevator
	
	/** This value is used in conjunction with the Elevator number to assign each Elevator a unique port number */
	public static final int ELEVATOR_STARTING_PORT_NUMBER = 1000;
	/** The amount of time the elevator door stays open for before closing*/
	public static final int DOOR_OPEN_TIME = 4000;
	/** The amount of time it takes for an Elevator to move floors */
	public static final int ELEVATOR_TIME_BETWEEN_FLOORS = 2000;
	/** The amount of time for an Elevator to give up */
	public static final int ELEVATOR_TIMEOUT = 2000;
	/** Number of cycles in an idle state that an elevator will stay active for before exiting */
	public static final int IDLE_EXIT_COUNT = 15;
	
	// Scheduler
	
	/** The amount of time it takes for an Elevator to move floors */
	public static final int ELEVATOR_TIME_FOR_DOORS = 500;
	/** The value found in an Elevator's message that will represent a final destination */
	public static final int ELEVATOR_FINAL_DESTINATION_VALUE = 1;
	/** The value found in an Elevator's message that will represent a motor failure */
	public static final int ELEVATOR_MOTOR_FAIL_VALUE = -1;
	/** The value found in an Elevator's message that will represent a door stuck open failure*/
	public static final int ELEVATOR_DOOR_STUCK_OPEN_VALUE = -2;
	/** The value found in an Elevator's message that will represent a door stuck closed failure*/
	public static final int ELEVATOR_DOOR_STUCK_CLOSED_VALUE = -3;
	/** This value is used in conjunction with the Elevator number to assign each Elevator Agent a unique port number */
	public static final int ELEVATOR_AGENT_STARTING_PORT_NUMBER = 2000;
	/** the amount of time for an agent to give up and go home */
	public static final int ELEVATOR_AGENT_TIMEOUT = 10000;
	/** the port number for the floor facing scheduler */
	public static final int SCHEDULER_PORT = 24;
	/** the amount of time for the scheduler to check for a response */
	public static final int SCHEDULER_TIMEOUT = 10000;
	/** the port number for the floor facing scheduler in unit tests */
	public static final int SCHEDULER_TEST_PORT = 23;
	
	// Floor
	
	/** port number of the floor manager */
	public static final int FLOOR_MANAGER_PORT = 50;
	
	// Model
	
	/** Starting port for elevator listeners */
	public static final int MODEL_PORT = 9000;
	/** the amount of time for a listener to give up and go home */
	public static final int ELEVATOR_LISTENER_TIMEOUT = 10000;
}
