/**
 * 
 */
package test;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.common.Instructions;
import main.elevator.Elevator;
import main.elevator.Elevator.ElevatorState;
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
	void elevatorNumberTest() {
		assertTrue(elevator.getElevatorNumber() == 4);
	}
	
	/**
	 * Test that the initial state of the elevator is arriving
	 */
	@Test
	void testIdleState() {
		assertTrue(elevator.getState() == ElevatorState.Idle);
	}
	
	/**
	 * Test that the ability to set the state to moving
	 */
	@Test
	void testMovingState() {
		elevator.setState(elevator.getState().nextState());
		assertTrue(elevator.getState() == ElevatorState.Moving);
	}
	
	/**
	 * Test that the ability to set the state to arriving
	 */
	@Test
	void testArrivingState() {
		elevator.setState(elevator.getState().nextState());
		elevator.setState(elevator.getState().nextState());
		assertTrue(elevator.getState() == ElevatorState.Arriving);
	}
}
