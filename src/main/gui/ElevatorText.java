package main.gui;

import java.awt.*;
import java.util.Objects;

import javax.swing.*;

import main.common.Input.FaultType;
import main.elevator.Elevator.ElevatorState;

public class ElevatorText extends JPanel {
	
	JLabel elevNum, currfloor, destfloor, status, faultStatus;

	public ElevatorText(int elevNumber, int currFloor, int destFloor, ElevatorState state, FaultType fault) {
		super();
		setBorder(BorderFactory.createLineBorder(new Color(107, 106, 104), 1));
		setPreferredSize(new Dimension(175, 185));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		
		elevNum = new JLabel("<html><u>ELEV: " + elevNumber + "</u></html>");
		currfloor = new JLabel("Current Floor: " + currFloor);
		destfloor = new JLabel("Destination Floor: " + destFloor);
		status = new JLabel("Status: " + (state == null ? "Uninitialized" : state.toString()));
		faultStatus = new JLabel("Fault: " + fault);
		
		add(elevNum);
		add(currfloor);
		add(destfloor);
		add(status);
		add(faultStatus);
	}
	
	public void updateText(int currFloor, int destFloor, ElevatorState state, FaultType fault) {
		currfloor.setText("Current Floor: " + currFloor);
		destfloor.setText("Destination Floor: " + destFloor);
		status.setText("Status: " + (state == null ? "Uninitialized" : state.toString()));
		faultStatus.setText(Objects.isNull(fault) ? "No fault" : fault.toString());
	}

}
