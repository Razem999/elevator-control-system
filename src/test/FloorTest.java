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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.common.Instructions;
import main.floor.*;
import main.scheduler.*;

/**
 * Tests for Floor Class
 *
 */
public class FloorTest {

	Scheduler scheduler;
	Floor floor;
	ArrayList<Instructions> instructions = new ArrayList<>();
	ArrayList<String[]> commandsList = new ArrayList<>();
	
	/**
	 * Set up Floor object and sample commands and instructions to test
	 */
	@BeforeEach
	void setup() {
		scheduler = new Scheduler();
		floor = new Floor(6);
		
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
	}

	/**
	 * Testing the list of commands to see if they are valid
	 */
	@Test
	void testVerify() {
		for (int i = 0; i < instructions.size(); i++) {
			System.out.println(instructions.get(i).toString());
			System.out.flush();
		}
		
		assertTrue(floor.getVerifyInput(commandsList.get(0)));
		assertFalse(floor.getVerifyInput(commandsList.get(1)));
		assertFalse(floor.getVerifyInput(commandsList.get(2)));
		assertFalse(floor.getVerifyInput(commandsList.get(3)));
		assertFalse(floor.getVerifyInput(commandsList.get(4)));
		
	}
	
	
	/**
	 * Testing to see if it is able to read input from a mock instructions file
	 */
	@Test
	void getInputFromFile() {
		assertTrue(floor.getInstructions().get(0).getDestinationFloor() == instructions.get(0).getDestinationFloor());
	}

}
