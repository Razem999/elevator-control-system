package test;

import org.junit.jupiter.api.BeforeEach;

import main.floor.Floor;
import main.scheduler.Scheduler;

/**
 * Tests for Scheduler Class
 *
 */
public class SchedulerTest {
	Scheduler scheduler;
	Floor floor;
	@BeforeEach
	void setup() {
		scheduler = new Scheduler();
		floor = new Floor(scheduler, 1);
	}
	
	
}
