package dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import widget.RoundedBorder;
import widget.impl.GlassLookPanel;
//import widget.impl.WeatherWidget.WeatherPanel;

public class Dashboard {

	/**
	 * @author Harry Anuszewski
	 */	
	
	public Dashboard(){	
		
		 JPanel panel = new GlassLookPanel(30);
	        panel.setForeground(Color.LIGHT_GRAY);
	        panel.setLayout(new BorderLayout());
	        panel.setPreferredSize(new Dimension(80, 80));
	        panel.setOpaque(false);
	        Color color = new Color(255, 255, 255);
	        panel.setBackground(color);
	        panel.setBorder(new RoundedBorder(20, 2f, Color.LIGHT_GRAY));
	        //panel.setVisible(true);
	        JFrame f = new JFrame();
	        f.add(panel);
	        f.setUndecorated(true);
	        f.pack();
	        f.setVisible(true);
	        //panel.add(new WeatherPanel());
	}
	
	public static void main(String[] args){
		new Dashboard();
	}

}
