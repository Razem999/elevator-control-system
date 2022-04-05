package main.gui;

import java.awt.*;
import javax.swing.*;

import main.common.Input.FaultType;
import main.elevator.Elevator.ElevatorState;

public class ElevatorText extends JPanel {

	public ElevatorText(int elevNumber, int currFloor, int destFloor, ElevatorState state, FaultType fault) {
		super();
		setBorder(BorderFactory.createLineBorder(new Color(107, 106, 104), 1));
		setPreferredSize(new Dimension(175, 185));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		
		JLabel elevNum = new JLabel("<html><u>ELEV: " + elevNumber + "</u></html>");
		JLabel currfloor = new JLabel("Current Floor: " + currFloor);
		JLabel destfloor = new JLabel("Destination Floor: " + destFloor);
		JLabel status = new JLabel("Status: " + (state == null ? "Uninitialized" : state.toString()));
		JLabel faultStatus = new JLabel("Fault: " + fault);
		
		add(elevNum);
		add(currfloor);
		add(destfloor);
		add(status);
		add(faultStatus);
	}

}
