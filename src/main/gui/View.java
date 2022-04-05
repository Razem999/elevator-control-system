/**
 * 
 */
package main.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class View extends JFrame implements ActionListener{
	
	private JPanel panel;
	private ElevatorText[] elevators;
	
	private Model model;
	
	public View(Model m) {
		super("Elevator Sim");
		model = m;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1000, 1000));
		setMinimumSize(new Dimension(500, 500));
		
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		
		add(panel);
		
		elevators = new ElevatorText[model.getCurrentFloors().length];
		for (int i = 0; i < model.getCurrentFloors().length; i++) {
			elevators[i] = new ElevatorText(i, model.getCurrentFloors()[i], model.getNextFloors()[i], model.getStates()[i], model.getFaults()[i]);
			panel.add(elevators[i]);
		}
		
		pack();
		setVisible(true);
//		This is a potential alternative to using SwingWorker. Basically, every 1000ms, actionPerformed() will be called.
//		 Timer t = new Timer(1000, this);
//		 t.start();
	}
	
	public void actionPerformed(ActionEvent e) {
		// listen for updates?
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new View(new Model());

	}

}
