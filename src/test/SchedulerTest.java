package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.common.Constants;
import main.common.Instructions;
import main.floor.FloorManager;
import main.scheduler.Scheduler;
import main.scheduler.Scheduler.SchedulerStates;

/**
 * Tests for Scheduler Class
 *
 */
public class SchedulerTest {
	Scheduler scheduler;
	FloorManager floor;
	/**
	 * Set up a scheduler
	 */
	@BeforeEach
	void setup() {
		scheduler = new Scheduler(Constants.SCHEDULER_TEST_PORT);
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
	
	/**
	 * Verify that the initial state is listening
	 */
	@Test
	void testInitialState() {
		assertTrue(scheduler.getState() == SchedulerStates.LISTENING);
	}
}
