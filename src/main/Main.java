/**
 * 
 */
package main;

import main.floor.Floor;
import main.scheduler.Scheduler;

/**
 * The main entryway into the program
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO make and start 3 threads
		Scheduler s1 = new Scheduler();
		Floor f1 = new Floor(null, null, 0, s1);
		f1.getInput();
		System.out.println(s1.toString());
	}

}
