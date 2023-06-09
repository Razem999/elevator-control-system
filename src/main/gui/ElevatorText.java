package main.gui;

import java.awt.*;
import java.util.Objects;

import javax.swing.*;

import main.common.Direction;
import main.common.Input.Fault;
import main.common.Input.FaultType;
import main.elevator.Elevator.ElevatorState;

/**
 * The ElevatorText class is a JPanel containing labels depicting the state of a specific elevator. 
 *
 */
public class ElevatorText extends JPanel {
	
	/* JLabels used to display the elevator number, current floor, destination floor, status, and any faults */
	private JLabel elevNum, currfloor, destfloor, status, faultStatus;
	/* UpLamp represents the up direction when the elevator is moving up*/
	private UpLamp upLamp;
	/* DownLamp represents the down direction when the elevator is moving down*/
	private DownLamp downLamp;

	/**
	 * ElevatorText constructor, instantiates JPanel and fills panels with values
	 * @param elevNumber The elevator's number
	 * @param currFloor The elevator's current floor 
	 * @param destFloor The elevator's destination floor
	 * @param direction The elevator's direction of travel
	 * @param status The elevator's status
	 * @param faultStatus The elevator's faults
	 */
	public ElevatorText(int elevNumber, int currFloor, int destFloor, Direction direction, ElevatorState state, FaultType fault) {
		super();
		setBorder(BorderFactory.createLineBorder(new Color(107, 106, 104), 1));
		setPreferredSize(new Dimension(175, 200));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		
		elevNum = new JLabel("<html><u>ELEV: " + elevNumber + "</u></html>");
		currfloor = new JLabel("Current Floor: " + currFloor);
		destfloor = new JLabel("Destination Floor: " + destFloor);
		status = new JLabel("Status: " + (state == null ? "Uninitialized" : state.toString()));
		faultStatus = new JLabel("Fault: " + fault);
		upLamp = new UpLamp();
		downLamp = new DownLamp();
		
		add(elevNum);
		add(currfloor);
		add(destfloor);
		add(status);
		add(faultStatus);
		add(upLamp);
		add(downLamp);
	}
	
	/**
	 * Updates JLabel values
	 * @param currFloor The new current floor 
	 * @param destFloor The new destination floor
	 * @param state The new status
	 * @param fault The new faults
	 * @param direction The new direction
	 */
	public void updateText(int currFloor, int destFloor, Direction direction, ElevatorState state, FaultType fault) {
		currfloor.setText("Current Floor: " + currFloor);
		destfloor.setText("Destination Floor: " + destFloor);
		status.setText("Status: " + ((fault == FaultType.MotorFault) ? "DEAD" : (state == null ? "Uninitialized" : state.toString())));
		faultStatus.setText(Objects.isNull(fault) ? "No fault" : fault.toString());
    
		if (direction == Direction.UP) {
			upLamp.LightUpOn(upLamp.getGraphics());
			downLamp.LightDownOff(downLamp.getGraphics());
		} else if (direction == Direction.DOWN) {
			upLamp.LightUpOff(upLamp.getGraphics());
			downLamp.LightDownOn(downLamp.getGraphics());
		} else {
			upLamp.LightUpOff(upLamp.getGraphics());
			downLamp.LightDownOff(downLamp.getGraphics());
		}

	}
}
