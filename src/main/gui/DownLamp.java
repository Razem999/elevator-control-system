package main.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class DownLamp extends JPanel{

	private Graphics2D g1;
	private Color color;
	
	public DownLamp() {
		color = Color.gray;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g1 = (Graphics2D) g;
		g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g1.setColor(color);
		g1.fill(new Polygon(new int[] {50,30,70}, new int[] {30,0,0}, 3));
	}
	
	public void LightDownOn(Graphics g) {
		color = Color.yellow;
		paintComponent(g);
	}
	
	public void LightDownOff(Graphics g) {
		color = Color.gray;
		paintComponent(g);
	}
	
}
