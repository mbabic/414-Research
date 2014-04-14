package Project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

public class LoadingPanel extends JFrame implements ActionListener {
	private static final long serialVersionUID = 4752359407583649068L;

	public static int WEBCAM = 0;
	public static int FILE = 1;

	private JButton button_File;
	private JButton button_Done;
	private JButton button_Cancel;
	private JPasswordField tField_Password;
	private JFormattedTextField tField_FPS;
	private JFormattedTextField tField_Frames;
	private JTextField tField_File;
	
	private JFrame FNF;

	private int modeSelected = FILE;
	private boolean savePixels = true;
	private String password = "";
	private int fPS;
	private int frames;
	private File file;

	/**
	 * JPanel Item with some specialized functions
	 */
	LoadingPanel() {
		super();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		makeFNFFrame();
		
		ButtonGroup bG_Mode = new ButtonGroup();
		JRadioButton loadVideoRButton = new JRadioButton("Load a file", true);
		loadVideoRButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				modeSelected = FILE;
				tField_File.setForeground(new Color(0, 0, 0));
				tField_File.setEditable(true);
			}
		});
		JRadioButton loadWebcamRButton = new JRadioButton("Use Webcam");
		loadWebcamRButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				modeSelected = WEBCAM;
				tField_File.setForeground(new Color(158, 158, 158));
				tField_File.setEditable(false);
			}
		});
		bG_Mode.add(loadVideoRButton);
		bG_Mode.add(loadWebcamRButton);
		JPanel panel_Mode = new JPanel(new BorderLayout());
		panel_Mode.add(loadVideoRButton, BorderLayout.WEST);
		panel_Mode.add(loadWebcamRButton, BorderLayout.EAST);

		JLabel label_File = new JLabel("File");
		tField_File = new JTextField();
		tField_File.setColumns(25);

		button_File = new JButton("Browse");
		button_File.addActionListener(this);
		JPanel panel_File = new JPanel(new BorderLayout());
		panel_File.add(label_File, BorderLayout.WEST);
		panel_File.add(tField_File, BorderLayout.CENTER);
		panel_File.add(button_File, BorderLayout.EAST);

		JLabel label_Password = new JLabel("Password");
		tField_Password = new JPasswordField(10);
		JPanel panel_Password = new JPanel(new BorderLayout());
		panel_Password.add(label_Password, BorderLayout.WEST);
		panel_Password.add(tField_Password, BorderLayout.CENTER);

		JLabel label_FPS = new JLabel("FPS (1-60)");
		NumberFormatter formatter_FPS = new NumberFormatter();
		formatter_FPS.setMinimum(1);
		formatter_FPS.setMaximum(60);
		formatter_FPS.setAllowsInvalid(false);
		tField_FPS = new JFormattedTextField(formatter_FPS);
		tField_FPS.setColumns(2);
		tField_FPS.setValue(30);
		JPanel panel_FPS = new JPanel(new BorderLayout());
		panel_FPS.add(label_FPS, BorderLayout.WEST);
		panel_FPS.add(tField_FPS, BorderLayout.CENTER);

		JLabel label_Frames = new JLabel("Num of Frames");
		NumberFormatter formatter_Frames = new NumberFormatter();
		formatter_Frames.setMinimum(1);
		formatter_Frames.setAllowsInvalid(false);
		tField_Frames = new JFormattedTextField(formatter_Frames);
		tField_Frames.setColumns(10);
		tField_Frames.setValue(5);
		JPanel panel_Frames = new JPanel(new BorderLayout());
		panel_Frames.add(label_Frames, BorderLayout.WEST);
		panel_Frames.add(tField_Frames, BorderLayout.CENTER);

		button_Done = new JButton("Done");
		button_Cancel = new JButton("Cancel");
		button_Done.addActionListener(this);
		button_Cancel.addActionListener(this);
		JPanel panel_Button1 = new JPanel(new BorderLayout());
		panel_Button1.add(button_Done, BorderLayout.WEST);
		panel_Button1.add(button_Cancel, BorderLayout.EAST);
		JPanel panel_Button = new JPanel(new BorderLayout());
		panel_Button.add(panel_Button1, BorderLayout.EAST);

		JLabel label_PixelSave = new JLabel("Pixel Save");
		JRadioButton radio_PixelSaveON = new JRadioButton("On", true);
		radio_PixelSaveON.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				savePixels = true;
				
			}
		});
		JRadioButton radio_PixelSaveOff = new JRadioButton("Off");
		radio_PixelSaveOff.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePixels = false;
				
			}
		});
		
		ButtonGroup bG_PixelSave = new ButtonGroup();
		bG_PixelSave.add(radio_PixelSaveON);
		bG_PixelSave.add(radio_PixelSaveOff);
		
		JPanel panel_PixelSave1 = new JPanel(new BorderLayout());
		JPanel panel_PixelSave = new JPanel(new BorderLayout());
		panel_PixelSave.add(label_PixelSave, BorderLayout.WEST);
		panel_PixelSave1.add(radio_PixelSaveON, BorderLayout.CENTER);
		panel_PixelSave1.add(radio_PixelSaveOff, BorderLayout.EAST);
		panel_PixelSave.add(panel_PixelSave1, BorderLayout.EAST);
		
		JPanel panel_North = new JPanel(new BorderLayout());
		panel_North.add(panel_Mode, BorderLayout.NORTH);
		panel_North.add(panel_PixelSave, BorderLayout.CENTER);
		panel_North.add(panel_Frames, BorderLayout.SOUTH);

		this.add(panel_FPS, BorderLayout.CENTER);

		JPanel panel_South = new JPanel(new BorderLayout());
		panel_South.add(panel_File, BorderLayout.NORTH);
		panel_South.add(panel_Password, BorderLayout.CENTER);
		panel_South.add(panel_Button, BorderLayout.SOUTH);

		this.add(panel_North, BorderLayout.NORTH);
		this.add(panel_South, BorderLayout.SOUTH);

		this.setVisible(true);
		this.pack();

		while (this.isVisible())
			Thread.yield();
	}

	private void makeFNFFrame() {
		FNF = new JFrame();
		JTextArea text = new JTextArea(
				"The file your are looking for does not \n" +
				"exist too bad for you. Try a real one \n" +
				"next time.");
		text.setEditable(false);
		JButton close = new JButton("Ok");
		close.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FNF.setVisible(false);
			}
		});
		FNF.add(text, BorderLayout.CENTER);
		FNF.add(close, BorderLayout.SOUTH);
		FNF.pack();
		FNF.setVisible(false);
	}
	
	private File grabMediaFile() {
		File file = null;
		JFileChooser chooser;
		int option;

		chooser = new JFileChooser(System.getProperty("user.dir"));

		chooser.setFileFilter(new FileFilter() {

			private FileNameExtensionFilter exFilter = new FileNameExtensionFilter(
					"mp4", "avi");

			@Override
			public String getDescription() {
				return null;
			}

			@Override
			public boolean accept(File f) {

				if (f.isDirectory())
					return true;
				return exFilter.accept(f);
			}
		});
		chooser.setAcceptAllFileFilterUsed(false);
		option = chooser.showOpenDialog(chooser);

		if (option == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
		}
		return file;
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
	 * 
	 * @return The number of fps
	 */
	public int getFPS() {
		return fPS;
	}

	/**
	 * gets the number of frames the user wants to record
	 * 
	 * @return number of frames to record
	 */
	public int getframes() {
		return frames;
	}
	
	/**
	 * Gets the File they entered.
	 * @return
	 */
	public File getFile() {
		return file;
	}
	
	public boolean getPixelSave() {
		return savePixels;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o.equals(button_Done)) {
			if (modeSelected == FILE) {
				file = new File(tField_File.getText());
				frames = (int) tField_Frames.getValue();
				fPS = (int) tField_FPS.getValue();
				char[] text = tField_Password.getPassword();
				password = new String(text);
				
				if (file.exists()) {
					this.setVisible(false);
				} else {
					FNF.setVisible(true);
				}
			}
		} else if (o.equals(button_Cancel)) {
			System.exit(0);
		} else if (o.equals(button_File)) {
			if (modeSelected == FILE) {
				File f = grabMediaFile();
				tField_File.setText(f.getPath());
			}
		}
	}
}
