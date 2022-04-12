package main.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * The DownLamp Class is a JPanel displayed on the window, depicting whether the elevator is traveling down or not.
 *
 */
public class DownLamp extends JPanel{

	/* Graphics used to draw the shapes for the elevator lamp*/
	private Graphics2D g1;
	/* Color used to depict whether the lamp is on (yellow) or off (gray)*/
	private Color color;
	
	/**
	 * UpLamp constructor, instantiates the direction of the elevator
	 */
	public DownLamp() {
		color = Color.gray;
	}
	
	/* Draw the elevator lamps on the GUI*/
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g1 = (Graphics2D) g;
		g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g1.setColor(color);
		g1.fill(new Polygon(new int[] {50,30,70}, new int[] {30,0,0}, 3));
	}
	
	/* Set the Elevator Down Lamp on*/
	public void LightDownOn(Graphics g) {
		color = Color.yellow;
		paintComponent(g);
	}
	
	/* Set the Elevator Up Lamp off*/
	public void LightDownOff(Graphics g) {
		color = Color.gray;
		paintComponent(g);
	}
	
}
