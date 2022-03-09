package test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import main.common.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommonTest {

	Instructions instruction;
	/**
	 * Set up an example instructions for tests to work off of
	 */
	@BeforeEach
	void setup() {
		instruction = new Instructions("14:05:15.0", "3", "Up", "4");
	}
	
	/**
	 * Test to see if getting time gets the correct time
	 */
	@Test
	void checkTime() {
		Date testTime;
		
		try {
			testTime = new SimpleDateFormat("hh:mm:ss.mmm").parse("14:05:15.0");
			assertTrue(instruction.getTime().equals(testTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test for getting correct destination floor
	 */
	@Test
	void checkDestinationFloor() {
		assertTrue(instruction.getDestinationFloor() == 4);
	}
	
	/**
	 * Test for getting correct current floor
	 */
	@Test
	void checkCurrentFloor() {
		assertTrue(instruction.getCurrentFloor() == 3);
	}
	
	/** 
	 * Test for seeing if printing instructions leads to correct string
	 */
	@Test
	void checkString() {
		assertTrue(instruction.toString().equals("INS:[Thu Jan 01 14:00:15 EST 1970,UP,3,4]"));
	}
	
	/**
	 * Test conversion from instruction to byte array
	 */
	@Test
	void testInstructionToByteArr() {
		byte[] correct = {85, 80, 0, 51, 0, 52};
		byte[] test = ByteConverter.instructionToByteArray(instruction);
		assertTrue(Arrays.equals(test, correct));
	}
	
	/**
	 * Test conversion from byte array to instruction
	 */
	@Test
	void testByteArrToInstruction() {
		byte[] test = {85, 80, 0, 51, 0, 52};
		
		Instructions testInstruction = ByteConverter.byteArrayToInstructions(test);
		
		assertTrue(instruction.getCurrentFloor() == testInstruction.getCurrentFloor());
		assertTrue(instruction.getDestinationFloor() == testInstruction.getDestinationFloor());
		assertTrue(instruction.getDirection() == testInstruction.getDirection());
	}
}
