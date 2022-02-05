package test;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
		floor.getInput();
		assertTrue(floor.getInstructions().get(0).getDestinationFloor() == 4);
	}
}
