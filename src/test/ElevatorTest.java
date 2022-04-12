/**
 * 
 */
package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.elevator.Elevator;
import main.elevator.Elevator.ElevatorState;

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
		elevator = new Elevator(123);
	}
	
	@AfterEach
	void reset() {
		elevator = null;
		System.gc();
	}

	/**
	 * Test for seeing if getting elevator number is correct
	 */
	@Test
	void elevatorNumberTest() {
		assertTrue(elevator.getElevatorNumber() == 123);
	}

	/**
	 * Test that the initial state of the elevator is arriving
	 */
	@Test
	void testIdleState() {
		assertTrue(elevator.getState() == ElevatorState.Idle);
	}

	/**
	 * Test that the state can be set to moving
	 */
	@Test
	void testMovingState() {
		elevator.setState(elevator.getState().nextState());
		assertTrue(elevator.getState() == ElevatorState.Moving);
	}
	
	/**
	 * Test that the state can be set to arriving
	 */
	@Test
	void testArrivingState() {
		elevator.setState(ElevatorState.Arriving);
		assertTrue(elevator.getState() == ElevatorState.Arriving);
	}

	/**
	 * Test that the state can be set to idle
	 */
	@Test
	void testRecurringIdleState() {
		elevator.setState(elevator.getState().nextState());
		elevator.setState(ElevatorState.Idle);
		assertTrue(elevator.getState() == ElevatorState.Idle);
	}
	
	
	/**
	 * Test the process message method for motor fault
	 */
	@Test
	void testProcessMessageDestination() {
		byte[] message = { 1, 0 };
		
		elevator.getProcessMessage(message);
		
		assertFalse(elevator.getwillMotorFail());
		assertFalse(elevator.getwillDoorsBeStuckOpen());
		assertFalse(elevator.getwillDoorsBeStuckClosed());
	}
	
	/**
	 * Test the final destination
	 */
	@Test
	void testFinalDestination() {
		byte[] message = { 1, 1 };
		elevator.getProcessMessage(message);
		
		assertTrue(elevator.isFinalDestination());
	}
	
	/**
	 * Test the process message method for motor fault
	 */
	@Test
	void testProcessMessageMotorFault() {
		byte[] message = { -1, 0 };
		
		elevator.getProcessMessage(message);
		
		assertTrue(elevator.getwillMotorFail());
	}
	
	/**
	 * Test the process message method for door stuck open fault
	 */
	@Test
	void testProcessMessageDoorOpen() {
		byte[] message = { -2, 0 };
		
		elevator.getProcessMessage(message);
		
		assertTrue(elevator.getwillDoorsBeStuckOpen());
	}
	
	/**
	 * Test the process message method for door stuck closed fault
	 */
	@Test
	void testProcessMessageDoorClosed() {
		byte[] message = { -3, 0 };
		
		elevator.getProcessMessage(message);
		
		assertTrue(elevator.getwillDoorsBeStuckClosed());
	}
}
