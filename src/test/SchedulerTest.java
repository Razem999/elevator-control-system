package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.common.Instructions;
import main.floor.Floor;
import main.scheduler.Scheduler;

/**
 * Tests for Scheduler Class
 *
 */
public class SchedulerTest {
	Scheduler scheduler;
	Floor floor;
	/**
	 * Set up a scheduler
	 */
	@BeforeEach
	void setup() {
		scheduler = new Scheduler();
	}
	
	/**
	 * Verify that addInstructions populates the instructions queue
	 */
	@Test
	void testAddInstructions() {
		Instructions instruction1 = new Instructions("14:05:15.0", "3", "Up", "4");
		assertFalse(scheduler.hasInstructions());
		scheduler.addInstructions(instruction1);
		assertTrue(scheduler.hasInstructions());
	}
	
	/**
	 * Verify that popInstructions removes instruction from instructions queue, and verifies instruction details
	 */
	@Test
	void testPopInstructions() {
		Instructions instruction1 = new Instructions("14:05:15.0", "3", "Up", "4");
		scheduler.addInstructions(instruction1);
		scheduler.popInstructions();		
		assertFalse(scheduler.hasInstructions());
	}
	
	/**
	 * Verify that completeInstructions populates the completed list
	 */
	@Test
	void testCompleteInstructions() {
		Instructions instruction1 = new Instructions("14:05:15.0", "3", "Up", "4");
		assertFalse(scheduler.hasCompleted());
		scheduler.completeInstructions(instruction1);
		assertTrue(scheduler.hasCompleted());
	}
	
	/**
	 * Verify that notifyFloor removes from completed list and properly counts numCompleted
	 */
	@Test
	void testNotifyFloor() {
		Instructions instruction1 = new Instructions("14:05:15.0", "3", "Up", "4");
		scheduler.completeInstructions(instruction1);
		scheduler.notifyFloor(4);
		assertFalse(scheduler.hasCompleted());
		assertTrue(scheduler.getNumCompleted() == 1);
	}
	
}
