package Project;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.text.NumberFormatter;

public class LoadingPanel extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	public static int WEBCAM = 0;
	public static int FILE = 1;

	private JButton button_Done;
	private JButton button_Cancel;
	private JPasswordField passField;
	private JFormattedTextField tField_FPS;
	private JFormattedTextField tField_Frames;
	
	private int modeSelected = FILE;
	private String password = "";
	private int fPS;
	private int frames;

	/**
	 * JPanel Item with some specialized functions
	 */
	LoadingPanel() {
		super();

		ButtonGroup bG_Mode = new ButtonGroup();
		JRadioButton loadVideoRButton = new JRadioButton("Load a file", true);
		loadVideoRButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				modeSelected = FILE;
			}
		});
		JRadioButton loadWebcamRButton = new JRadioButton("Use Webcam");
		loadWebcamRButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				modeSelected = WEBCAM;
			}
		});
		bG_Mode.add(loadVideoRButton);
		bG_Mode.add(loadWebcamRButton);
		JPanel panel_Mode = new JPanel(new BorderLayout());
		panel_Mode.add(loadVideoRButton, BorderLayout.NORTH);
		panel_Mode.add(loadWebcamRButton, BorderLayout.SOUTH);

		JLabel passLabel = new JLabel("Password");
		passField = new JPasswordField(10);
		passField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				char[] text = passField.getPassword();
				password = new String(text);
			}
		});

		JPanel panel_Password = new JPanel(new BorderLayout());
		panel_Password.add(passLabel, BorderLayout.WEST);
		panel_Password.add(passField, BorderLayout.CENTER);

		JLabel label_FPS = new JLabel("FPS");
		NumberFormatter formatter_FPS = new NumberFormatter();
		formatter_FPS.setMaximum(60);
		formatter_FPS.setAllowsInvalid(false);
		tField_FPS = new JFormattedTextField(formatter_FPS);
		tField_FPS.setColumns(2);
		tField_FPS.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fPS = (int) tField_FPS.getValue();
			}
		});
		JPanel panel_FPS = new JPanel(new BorderLayout());
		panel_FPS.add(label_FPS, BorderLayout.WEST);
		panel_FPS.add(tField_FPS, BorderLayout.CENTER);

		JLabel label_Frames = new JLabel("Number of Frames to encode");
		NumberFormatter formatter_Frames = new NumberFormatter();
		formatter_Frames.setAllowsInvalid(false);
		tField_Frames = new JFormattedTextField(formatter_Frames);
		tField_Frames.setColumns(10);
		tField_Frames.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frames = (int) tField_Frames.getValue();
			}
		});
		
		JPanel panel_Frames = new JPanel(new BorderLayout());
		panel_Frames.add(label_Frames, BorderLayout.NORTH);
		panel_Frames.add(tField_Frames, BorderLayout.SOUTH);
		
		button_Done = new JButton("Done");
		button_Cancel = new JButton("Cancel");
		button_Done.addActionListener(this);
		button_Cancel.addActionListener(this);
		JPanel panel_Button = new JPanel(new BorderLayout());
		panel_Button.add(button_Done, BorderLayout.WEST);
		panel_Button.add(button_Cancel, BorderLayout.EAST);
		
		JPanel panel_North = new JPanel(new BorderLayout());
		panel_North.add(panel_Mode, BorderLayout.NORTH);
		panel_North.add(panel_Frames, BorderLayout.CENTER);
		
		JPanel panel_South = new JPanel(new BorderLayout());
		panel_South.add(panel_FPS, BorderLayout.NORTH);
		panel_South.add(panel_Password, BorderLayout.CENTER);
		panel_South.add(panel_Button, BorderLayout.SOUTH);
		
		this.add(panel_North, BorderLayout.NORTH);
		this.add(panel_South, BorderLayout.SOUTH);

		this.setVisible(true);
		this.pack();
		
		while (this.isVisible());
			Thread.yield();
	}

	/**
	 * Shows a panel that will find out what type of input the user wants
	 * 
	 * @return The mode selected. Will either be FILE or WEBCAM.
	 */
	public int getInputMode() {
		return modeSelected;
	}

	/**
	 * Gets a password from the user
	 * 
	 * @return The password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Gets the FPS that the user enter
	 * @return The number of fps
	 */
	public int getFPS() {
		return fPS;
	}
	
	/**
	 * gets the number of frames the user wants to record
	 * @return number of frames to record
	 */
	public int getframes() {
		return frames;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o.equals(button_Done)) {
			this.setVisible(false);
		} else if (o.equals(button_Cancel)) {
			System.exit(0);
		}
	}
}
