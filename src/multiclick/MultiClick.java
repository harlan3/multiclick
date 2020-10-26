package multiclick;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MultiClick {

	private String fileName = "settings.xml";
	private HashMap<String, String> xmlMap = new HashMap<>();
	private JFrame frame;
	private Point locPrev;

	public static void main(String[] args) {

		MultiClick multiClick = new MultiClick();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				multiClick.displayJFrame();
			}
		});

		while (true) {

			Point locCur = MouseInfo.getPointerInfo().getLocation();

			if (multiClick.locPrev != null) {
				if ((locCur.x != multiClick.locPrev.x) || (locCur.y != multiClick.locPrev.y))
					System.out.println("mouse pos = " + locCur.x + "," + locCur.y);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
			multiClick.locPrev = locCur;
		}
	}

	void displayJFrame() {

		frame = new JFrame("Multi Click");

		JButton readXMLButton = new JButton("Read XML Click Inputs");
		JButton executeClicksButton = new JButton("Execute Clicks");

		readXMLButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadXML();
			}
		});

		executeClicksButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				int numMultiClicks = Integer.parseInt(xmlMap.get("NumMultiClicks"));

				for (int i = 0; i < numMultiClicks; i++) {

					String positionString = xmlMap.get("MultiClick" + Integer.toString(i + 1));
					String[] positionTokens = positionString.split(",");
					try {
						buttonClick(Integer.parseInt(positionTokens[0]), Integer.parseInt(positionTokens[1]));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					String sleepString = xmlMap.get("MultiClick" + Integer.toString(i + 1) + "Sleep");
					try {
						Thread.sleep(Integer.parseInt(sleepString));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		frame.getContentPane().setLayout(new FlowLayout());
		frame.add(readXMLButton);
		frame.add(executeClicksButton);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(225, 125));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void buttonClick(int x, int y) throws AWTException {

		Robot robot = new Robot();
		robot.mouseMove(x, y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	private void loadXML() {

		xmlMap.clear();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(fileName);
			Element rootElem = doc.getDocumentElement();

			if (rootElem != null) {
				parseElements(rootElem);
			}
		} catch (Exception e) {

			System.out.println("Exception in loadXML(): " + e.toString());
		}
	}

	private void parseElements(Element root) {

		String name = "";

		if (root != null) {

			NodeList nl = root.getChildNodes();

			if (nl != null) {

				for (int i = 0; i < nl.getLength(); i++) {
					Node node = nl.item(i);

					if (node.getNodeName().equalsIgnoreCase("setting")) {

						NodeList childNodes = node.getChildNodes();

						for (int j = 0; j < childNodes.getLength(); j++) {

							Node child = childNodes.item(j);

							if (child.getNodeName().equalsIgnoreCase("name"))
								name = child.getTextContent();
							else if (child.getNodeName().equalsIgnoreCase("value"))
								xmlMap.put(name, child.getTextContent());
						}
					}
				}
			}
		}
	}
}
