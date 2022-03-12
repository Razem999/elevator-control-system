package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.common.Constants;
import main.common.Direction;
import main.common.Instructions;
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
	ArrayList<ElevatorAgent> agents;
	ElevatorAgent agent1;
	ElevatorAgent agent2;
	ArrayList<Instructions> instructions = new ArrayList<>();
	ArrayList<String[]> commandsList = new ArrayList<>();
	/**
	 * Set up a scheduler
	 */
	@BeforeEach
	void setup() {
		scheduler = new Scheduler(Constants.SCHEDULER_TEST_PORT);
		
		scheduler = new Scheduler(Constants.SCHEDULER_TEST_PORT);
		floor = new FloorManager(6);
		
		// get instructions array
		floor.getGetInput("src/test/mockInstructions.txt");
		File input = new File("src/test/mockInstructions.txt"); 
		try {		
			Scanner inputReader = new Scanner(input);
			while (inputReader.hasNextLine()) {
			  String line = inputReader.nextLine();
			  String[] commands = line.split(" ");
			  commandsList.add(commands);
			  instructions.add(new Instructions(commands));
			}
			inputReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		elevator1 = new Elevator(1);
		elevator2 = new Elevator(2);
		
		elevator1.setState(eStates.Idle);
		elevator2.setState(eStates.Moving);
		
		agent1 = new ElevatorAgent(elevator1.getElevatorNumber(), null, 1);
		agent2 = new ElevatorAgent(elevator2.getElevatorNumber(), null, 5);
		
		agents.add(agent1);
		agents.add(agent2);
	}
	
	/**
	 * Verify that the initial state is listening
	 */
	@Test
	void testInitialState() {
		assertTrue(scheduler.getState() == SchedulerStates.LISTENING);
	}
	
	@Test
	void testFloorDifference() {
		scheduler.getFloorDifference(1, 4, agent1.getCurrentDirection(), Direction.UP, agent1.getCurrentState());
		assertTrue(1 - 4 == agent1.getCurrentFloor() - 4);
		scheduler.getFloorDifference(5, 0, agent2.getCurrentDirection(), Direction.DOWN, agent2.getCurrentState());
		assertTrue(5 - 0 == agent2.getCurrentFloor() - 0);
	}
	
	@Test
	void testGetBestElevator() {
		
	}
	
	/**
	 * Verify that addInstructions populates the instructions queue and that the state is listening
	 */
//	@Test
//	void testAddInstructions() {
//		Instructions instruction1 = new Instructions("14:05:15.0", "3", "Up", "4");
//		assertFalse(scheduler.hasInstructions());
//		scheduler.addInstructions(instruction1);
//		assertTrue(scheduler.hasInstructions());
//		assertTrue(scheduler.getState() == SchedulerStates.LISTENING);
//	}
//	
//	/**
//	 * Verify that popInstructions removes instruction from instructions queue, and verifies instruction details and that the state is delegating
//	 */
//	@Test
//	void testPopInstructions() {
//		Instructions instruction1 = new Instructions("14:05:15.0", "3", "Up", "4");
//		scheduler.addInstructions(instruction1);
//		scheduler.popInstructions();		
//		assertFalse(scheduler.hasInstructions());
//		assertTrue(scheduler.getState() == SchedulerStates.DELEGATING);
//	}
//	
//	/**
//	 * Verify that completeInstructions populates the completed list and test that the state is changefloor
//	 */
//	@Test
//	void testCompleteInstructions() {
//		Instructions instruction1 = new Instructions("14:05:15.0", "3", "Up", "4");
//		assertFalse(scheduler.hasCompleted());
//		scheduler.completeInstructions(instruction1);
//		assertTrue(scheduler.hasCompleted());
//		assertTrue(scheduler.getState() == SchedulerStates.CHANGEFLOOR);
//	}
//	
//	/**
//	 * Verify that notifyFloor removes from completed list and properly counts numCompleted and that the state is processarrival
//	 */
//	@Test
//	void testNotifyFloor() {
//		Instructions instruction1 = new Instructions("14:05:15.0", "3", "Up", "4");
//		scheduler.completeInstructions(instruction1);
//		scheduler.notifyFloor(4);
//		assertFalse(scheduler.hasCompleted());
//		assertTrue(scheduler.getState() == SchedulerStates.PROCESSARRIVAL);
//	}
	
	
	
}
