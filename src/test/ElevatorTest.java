/**
 * 
 */
package test;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.elevator.Elevator;
import main.scheduler.Scheduler;

/**
 * Tests for the Elevator Class
 */
public class ElevatorTest {

	Elevator elevator;
	
	/**
	 * Set up an elevator object for tests to use
	 */
	@BeforeEach
	void setup() {
		Scheduler scheduler = new Scheduler();
		elevator = new Elevator(scheduler, 4);
	}
	
	/**
	 * Test for seeing if getting elevator number is correct
	 */
	@Test
	void ElevatorNumberTest() {
		assertTrue(elevator.getElevatorNumber() == 4);
	}
}
