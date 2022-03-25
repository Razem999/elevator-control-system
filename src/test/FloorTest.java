package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.common.Constants;
import main.common.Input.Fault;
import main.common.Input.Instructions;
import main.floor.*;
import main.scheduler.*;

/**
 * Tests for Floor Class
 *
 */
public class FloorTest {

	Scheduler scheduler;
	FloorManager floor;
	ArrayList<Instructions> instructions = new ArrayList<>();
	ArrayList<String[]> commandsList = new ArrayList<>();
	ArrayList<Fault> faults = new ArrayList<>();
	
	/**
	 * Set up Floor object and sample commands and instructions to test
	 */
	@BeforeEach
	void setup() {
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
	
	@AfterEach
	void reset() {
		scheduler = null;
		floor = null;
		System.gc();
	}

	/**
	 * Testing the method that verifies if an instruction is valid
	 */
	@Test
	void testVerifyInput() {
		assertTrue(floor.getVerifyInstructions(commandsList.get(0)));
		assertFalse(floor.getVerifyInstructions(commandsList.get(1)));
		assertFalse(floor.getVerifyInstructions(commandsList.get(2)));
		assertFalse(floor.getVerifyInstructions(commandsList.get(3)));
		assertFalse(floor.getVerifyInstructions(commandsList.get(4)));
	}
	
	/**
	 * Testing the method that verifies if a fault is valid
	 */
	@Test
	void testVerifyFault() {
		System.out.println(commandsList);
		assertTrue(floor.getVerifyFault(commandsList.get(5)));
		assertTrue(floor.getVerifyFault(commandsList.get(6)));
		assertTrue(floor.getVerifyFault(commandsList.get(7)));
		assertFalse(floor.getVerifyFault(commandsList.get(8)));
		assertFalse(floor.getVerifyFault(commandsList.get(9)));
	}
	
	/**
	 * Testing to see if it is able to read input from a mock instructions file
	 */
	@Test
	void getInputFromFile() {
		assertTrue(((Instructions) (floor.getInputList().get(0))).getDestinationFloor() == instructions.get(0).getDestinationFloor());
	}

}
