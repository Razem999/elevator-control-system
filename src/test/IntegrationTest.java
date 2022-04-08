package test;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import main.common.Constants;
import main.elevator.Elevator;
import main.floor.FloorManager;
import main.scheduler.Scheduler;

public class IntegrationTest {
	/**
	 * Test the entire system running as a whole
	 * Note: This test takes a long time due to waiting for timeouts
	 */
	@Test
	void integrationTest() {
		int exitValue = -1;
		Thread t = new Thread(() -> {
			FloorManager floor = new FloorManager(Constants.NUM_FLOORS, "src/test/mockInstructions.txt");
			floor.simulate();
		});
		t.start();

		Scheduler scheduler = new Scheduler(Constants.SCHEDULER_PORT);
		Thread[] elevators = Elevator.generateElevators(Constants.NUM_CARS);
		for (int i = 0; i < elevators.length; i++) {
			elevators[i].start();
		}
		try {
		// Scheduler.run() only returns 0 if the program runs until successfully completed
		exitValue = scheduler.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		assertTrue(exitValue == 0);
	}
}
