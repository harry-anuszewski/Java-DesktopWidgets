package widget.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import widget.DoneButton;
import widget.RoundedBorder;

/**
 * @author Harry Anuszewski
 */
public class WeatherWidget extends PlainWidget {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8538953086314899839L;
	private static String WEATHER_FEED = "http://api.openweathermap.org/data/2.5/weather?mode=xml&zip={zip},us&APPID=";
	private String zip = "19130"; // should be a preference on back of widget
	private String api_key = "";
	private String icon_url = "http://openweathermap.org/img/w/";
	private int update_interval = 60; // in minutes

	private static Forecast forecast = new Forecast();

	public WeatherWidget() {
		super();
		getForecastXML();

		// update every x minutes
		Timer t = new Timer(1000 * 60 * update_interval, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getForecastXML();
			}
		});
		t.setRepeats(true);
		t.start();
	}

	public static void main(String[] args) {
		new WeatherWidget();
	}

	protected JComponent createFront() {
		JPanel panel = new GlassLookPanel(30);
		panel.setForeground(Color.LIGHT_GRAY);
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(80, 80));
		panel.setOpaque(false);
		Color color = new Color(255, 255, 255);
		panel.setBackground(color);
		panel.setBorder(new RoundedBorder(20, 2f, Color.LIGHT_GRAY));
		panel.add(new WeatherPanel());
		return panel;
	}

	protected JComponent createBack() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(80, 80));
		panel.setOpaque(false);
		Color color = new Color(60, 60, 60);
		panel.setBackground(color);
		Border border = new RoundedBorder(20, 2f, color.brighter().brighter());
		panel.setBorder(border);

		JPanel donePanel = new JPanel(new BorderLayout());
		donePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		donePanel.setOpaque(false);
		donePanel.add(new DoneButton(getRevesiblecomponent()), BorderLayout.EAST);
		panel.add(donePanel, BorderLayout.SOUTH);

		return panel;
	}

	protected void addInfoButton() {
		// no info button
	}

	private void getForecastXML() {
		if ("" != api_key) {
			String feed = WEATHER_FEED.replace("{zip}", zip) + api_key;
			// System.out.println("Feed url:> "+feed);
			URL url;
			try {
				url = new URL(feed);
				SAXParser saxP = SAXParserFactory.newInstance().newSAXParser();
				InputStream is = url.openStream();
				saxP.parse(is, new ForecastHandler());
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("Need to set the api_key");
		}
	}

	private class ForecastHandler extends DefaultHandler {
		private boolean readImage = false;

		@Override
		public void endDocument() throws SAXException {
			repaint();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			// System.out.println("qname:> " + qName);
			if (qName.equals("weather") && attributes.getValue("icon") != null) {
				readImage = true;
				String imgUrl = icon_url + attributes.getValue("icon") + ".png";
				System.out.println(imgUrl);
				forecast.setImage(imgUrl);
			}

			if (qName.equals("temperature")) {
				forecast.temp = attributes.getValue("value");
				double k = Double.parseDouble(forecast.temp);
				double f = 1.8 * (k - 273) + 32;
				int rounded_f = (int) Math.round(f);
				forecast.temp = String.valueOf(rounded_f);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			super.characters(ch, start, length);
			if (readImage) {
				readImage = false;
			}
		}
	}

	private class WeatherPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2634171336379838534L;
		Font f = new Font("Arial", Font.BOLD, 16);

		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if (forecast.image != null)
				g2.drawImage(forecast.image, null, 7, 0);

			g2.setFont(f);
			g2.setColor(Color.BLACK);

			if (forecast.temp != null)
				g2.drawString(forecast.temp + '\u00B0', 25, 65);
		}
	}

	private static class Forecast {
		String temp;
		private BufferedImage image;

		public void setImage(String urlString) {
			try {
				URL url = new URL(urlString);
				image = ImageIO.read(url);
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			}
		}
	}
}
