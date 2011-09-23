package com.ickik.ladder.view;

import java.awt.Toolkit;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Common code of all view of the application.
 * @author Patrick Allgeyer
 * @version 0.1.000, 10/03/11
 */
public abstract class AbstractTestLadderView {

	private final ResourceBundle resourceBundle;
	protected static final String VERSION = "0.1";
	protected static final String SOFTWARE = "TestLadder";
	
	public AbstractTestLadderView(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}
	
	ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	void displayErrorMessage(JFrame frame, String message) {
		JOptionPane.showMessageDialog(frame, resourceBundle.getString(message), LadderFrame.SOFTWARE + " " + LadderFrame.VERSION, JOptionPane.ERROR_MESSAGE);
	}
	
	void displayErrorMessage(JFrame frame, String message, String[] messages) {
		StringBuilder str = new StringBuilder(resourceBundle.getString(message));
		str.append("\n");
		for (String msg : messages) {
			str.append(resourceBundle.getString(msg));
			str.append("\n");
		}
		JOptionPane.showMessageDialog(frame, str.toString(), LadderFrame.SOFTWARE + " " + LadderFrame.VERSION, JOptionPane.ERROR_MESSAGE);
	}
	
	void centeredFrame(JFrame frame) {
		double w = frame.getWidth();
		double l = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		frame.setLocation((int) (l / 2 - w / 2), 0);
		frame.setSize(frame.getWidth(), (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 25));
	}
	
	void centeredFrameInScreen(JFrame frame) {
		double w = frame.getWidth();
		double h = frame.getHeight();
		double l = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double l2 = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		frame.setLocation((int) (l / 2 - w / 2), (int)(l2 / 2 - h / 2));
	}
}
