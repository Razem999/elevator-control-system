/**
 * 
 */
package main;

import main.elevator.Elevator;
import main.floor.Floor;
import main.scheduler.Scheduler;

/**
 * The main entryway into the program
 */
public class Main {
	
	public static final int NUM_FLOORS = 1;
	public static final int NUM_CARS = 1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO make and start 3 threads
		Scheduler scheduler = new Scheduler();
		Thread floor = new Thread(new Floor(scheduler, 0), "Floor 0");
		Thread elevator = new Thread(new Elevator(scheduler, 0), "Elevator 0");;

		floor.start();
		elevator.start();
	}

}
