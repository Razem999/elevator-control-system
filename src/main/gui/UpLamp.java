package main.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class UpLamp extends JPanel{

	private Graphics2D g1;
	private Color color;
	
	public UpLamp() {
		color = Color.gray;	
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g1 = (Graphics2D) g;
		g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g1.setColor(color);
		g1.fill(new Polygon(new int[] {50,30,70}, new int[] {10,40,40}, 3));
	}
	
	public void LightUpOn(Graphics g) {
		color = Color.yellow;
		paintComponent(g);
	}
	
	public void LightUpOff(Graphics g) {
		color = Color.gray;
		paintComponent(g);
	}
}
