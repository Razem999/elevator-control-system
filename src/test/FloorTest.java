package test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

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
	@BeforeEach
	void setup() {
		scheduler = new Scheduler();
		floor = new Floor(scheduler, 1);
	}
	@Test
	void floorNumberTest() {
		System.out.println("Setting floor number");
		floor.setFloorNumber(2);
		assertTrue(floor.getFloorNumber() == 2);
	}
	
	@Test
	void getInputFromFile() {
		ArrayList<Instructions> instructions = new ArrayList<>();;
		floor.getGetInput("src/test/mockInstructions.txt");
		File input = new File("src/test/mockInstructions.txt"); 
		try {		
			Scanner inputReader = new Scanner(input);
			while (inputReader.hasNextLine()) {
			  String line = inputReader.nextLine();
			  String[] commands = line.split(" ");
			  instructions.add(new Instructions(commands));
			}
			inputReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		assertTrue(floor.getInstructions().get(0).getDestinationFloor() == instructions.get(0).getDestinationFloor());
	}
}
