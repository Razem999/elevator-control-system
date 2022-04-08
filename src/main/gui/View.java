/**
 * 
 */
package main.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * The View class is responsible for drawing a user interface to make the state of Elevator's more readable.
 *
 */
public class View extends JFrame implements ActionListener{
	
	/** ElevatorText array will represent the state of every Elevator running in the program */
	private ElevatorText[] elevators;
	/** This Model instance provides all the information rendered by the View */
	private Model model;
	
	/**
	 * View constructor, instantiates model and builds the GUI for the user to look at.
	 * @param m The model passed into this View
	 */
	public View(Model m) {
		super("Dunton Tower Elevator System Super");
		
		model = m;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1200, 1000));
		setMinimumSize(new Dimension(500, 500));
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		
		JPanel titlePanel = new JPanel();
		titlePanel.setMaximumSize(new Dimension(275, 50));
		add(titlePanel);

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		add(panel);
		
		JLabel title = new JLabel("Dunton Tower Elevator System");
		title.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
		title.setHorizontalAlignment(JLabel.CENTER);
		titlePanel.add(title);
		
		elevators = new ElevatorText[model.getCurrentFloors().length];
		for (int i = 0; i < model.getCurrentFloors().length; i++) {
			elevators[i] = new ElevatorText(i, model.getCurrentFloors()[i], model.getNextFloors()[i], model.getDirection()[i], model.getStates()[i], model.getFaults()[i]);
			panel.add(elevators[i]);
		}
		
		pack();
		new Timer(500, this).start();
		setVisible(true);
	}
	
	/**
	 * Helper function which will go through all ElevatorText instances and update them
	 */
	
	private void updateText() {
		for (int i = 0; i < elevators.length; i++) {
			elevators[i].updateText(model.getCurrentFloors()[i], model.getNextFloors()[i], model.getDirection()[i], model.getStates()[i], model.getFaults()[i]);
		}
		this.repaint();
	}
	
	/**
	 * actionPerformed calls updateText() every timer interval
	 */
	public void actionPerformed(ActionEvent e) {
		updateText();
		repaint();
	}

	/**
	 * Creates an instance of View and runs the associated model
	 * @param args
	 */
	public static void main(String[] args) {
		new View(new Model()).model.start();
	}

}
