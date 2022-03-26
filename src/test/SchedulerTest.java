package test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.common.Constants;
import main.common.Direction;
import main.common.Input.Fault;
import main.common.Input.Instructions;
import main.elevator.Elevator;
import main.elevator.Elevator.ElevatorState;
import main.floor.FloorManager;
import main.scheduler.ElevatorAgent;
import main.scheduler.Scheduler;
import main.scheduler.Scheduler.SchedulerStates;

/**
 * Tests for Scheduler Class
 *
 */
public class SchedulerTest {
	Scheduler scheduler;
	Elevator elevator1, elevator2;
	FloorManager floor;
	SchedulerStates sStates;
	ElevatorState eStates;
	ElevatorAgent agent1;
	ElevatorAgent agent2;
	ArrayList<Instructions> instructions = new ArrayList<>();
	ArrayList<String[]> commandsList = new ArrayList<>();
	ArrayList<Fault> faults = new ArrayList<>();
	/**
	 * Set up a scheduler
	 */
	@BeforeEach
	void setup() {
		elevator1 = new Elevator(123);
		elevator2 = new Elevator(321);
		
		elevator1.setState(eStates.Idle);
		elevator2.setState(eStates.Idle);
		
		agent1 = new ElevatorAgent(elevator1.getElevatorNumber(), null, 2);
		agent2 = new ElevatorAgent(elevator2.getElevatorNumber(), null, 0);
		
		scheduler = new Scheduler(Constants.SCHEDULER_TEST_PORT);
		floor = new FloorManager(6, "src/test/mockInstructions.txt");
		
		// get instructions array
		File input = new File("src/test/mockInstructions.txt"); 
		try {		
			Scanner inputReader = new Scanner(input);
			while (inputReader.hasNextLine()) {
			  String line = inputReader.nextLine();
			  String[] commands = line.split(" ");
			  
			  commandsList.add(commands);
			  
			  try {
				  instructions.add(new Instructions(commands));
			  }
			  catch (ArrayIndexOutOfBoundsException e) {
				  try {
					  faults.add(new Fault(commands));
				  }
				  catch (IllegalArgumentException e1) {
					  continue;
				  }
			  }
			}
			inputReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Reset all terminals before starting next test
	 */
	@AfterEach
	void reset() {
		agent1 = null;
		agent2 = null;
		elevator1 = null;
		elevator2 = null;
		scheduler = null;
		floor = null;
		System.gc(); 
	}
	
	/**
	 * Verify that the initial state is listening
	 */
	@Test
	void testInitialState() {
		assertTrue(scheduler.getState() == SchedulerStates.LISTENING);
	}
	
	/**
	 * Verify that the scheduler's next state is Delegating and the one after is Listening
	 */
	@Test
	void testDelegatingState() {
		assertTrue(scheduler.getState().nextState() == SchedulerStates.DELEGATING);
		assertTrue(scheduler.getState().nextState().nextState() == SchedulerStates.LISTENING);
	}
	
	@Test
	void testFloorDifference() {
		scheduler.getFloorDifference(2, 4, agent1.getCurrentDirection(), ((Instructions) (floor.getInputList().get(0))).getDirection(), agent1.getCurrentState());
		assertTrue(2 - 4 == agent1.getCurrentFloor() - ((Instructions)(floor.getInputList().get(0))).getDestinationFloor());
	}
}
