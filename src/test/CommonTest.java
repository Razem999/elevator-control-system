package test;

import main.common.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommonTest {

	Instructions instruction;
	@BeforeEach
	void setup() {
		instruction = new Instructions("14:05:15.0", "3", "Up", "4");
	}
	@Test
	void CheckDestinationFloor() {
		assertTrue(instruction.getDestinationFloor() == 4);
	}
	
	@Test
	void CheckDestinationFloorForDeparture() {
		assertFalse(instruction.getDestinationFloor() == 3);
	}
	
	@Test
	void CheckString() {
		assertTrue(instruction.toString().equals("INS:[Thu Jan 01 14:00:15 EST 1970,UP,3,4]"));
	}
}
