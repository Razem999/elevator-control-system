/**
 * 
 */
package main.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Deen
 *
 */
public class Instructions {
	private final String dateFormat = "hh:mm:ss.mmm";
	private Date time;
	private Direction direction;
	private int currentFloor, destinationFloor;
	
	public Instructions(String time, String currentFloor, String direction , String destinationFloor) {
		
		try {
			this.time = new SimpleDateFormat(dateFormat).parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		this.currentFloor = Integer.parseInt(currentFloor);
		this.direction = Direction.valueOf(direction.toUpperCase());
		this.destinationFloor = Integer.parseInt(destinationFloor);
	}
	
	public Instructions(String[] instructions) {
		this(instructions[0], instructions[1], instructions[2], instructions[3]);
	}
	
	public int getDestinationFloor() {
		return destinationFloor;
	}
	
	public String toString() {
		return String.format("INS:[%s,%s,%d,%d]", time.toString(), direction,  currentFloor, destinationFloor);
	}
	
	
}
