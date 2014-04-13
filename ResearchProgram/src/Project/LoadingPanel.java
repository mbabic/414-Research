package Project;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

public class LoadingPanel extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static int WEBCAM = 0;
	public static int FILE = 1;
	private int modeSelected;
	private String password;
	private JPasswordField passField;

	/**
	 * JPanel Item with some specialized functions
	 */
	LoadingPanel() {
		super();
	}

	/**
	 * Shows a panel that will find out what type of input the user wants
	 * @return The mode selected. Will either be FILE or WEBCAM.
	 */
	public int getInputMode() {
		modeSelected = -1;
		JButton loadVideoButton = new JButton("Load a File");
		JButton loadWebcamButton = new JButton("Use WebCam");

		loadVideoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modeSelected = FILE;

			}
		});

		loadWebcamButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				modeSelected = WEBCAM;
			}
		});
		this.add(loadVideoButton, BorderLayout.WEST);
		this.add(loadWebcamButton, BorderLayout.EAST);
		this.pack();
		this.setVisible(true);
		while (modeSelected == -1) {
			Thread.yield();
		}
		this.setVisible(false);
		this.remove(loadVideoButton);
		this.remove(loadWebcamButton);
		return modeSelected;
	}

	/**
	 * Gets a password from the user
	 * @return The password
	 */
	public String getPassword() {
		password = "";
		
		JLabel passLabel = new JLabel("Password");
		passField = new JPasswordField();
		passField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				char[] text = passField.getPassword();
				System.out.println(text);
				password = new String(text);
				System.out.println(password);
			}
		});
		
		passField.setSize(400, 50);

		this.add(passLabel, BorderLayout.WEST);
		this.add(passField, BorderLayout.CENTER);
		this.setSize(250, 75);
		this.setVisible(true);
		while (password == "") {
			Thread.yield();
		}
		this.setVisible(false);
		this.remove(passLabel);
		this.remove(passField);
		return password;
	}
}
