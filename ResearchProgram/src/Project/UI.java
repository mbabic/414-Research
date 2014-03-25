package Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * This UI panel holds the video stream
 * 
 * @author Marcus Karpoff, Marko Babic
 * 
 */
class MyPanel extends CanvasFrame {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private Boolean active;

	/**
	 * This is used for creating popout windows
	 * @param title The title for the window
	 * @param a the active status Boolean associated with the window
	 */
	public MyPanel(String title, Boolean a) {
		super(title);
		this.setSize(Settings.WIDTH, Settings.HEIGHT);
		active = a;
		setDefaultCloseOperation(0);
	}
	/**
	 * This sets the active status Boolean associated with the window to false on closure of the window
	 */
	@Override
	public void setDefaultCloseOperation(int operation) {
		active = false;
	}

	/**
	 * Fills the window with an IplImage
	 * @param img the IplImage to fill the window.
	 */
	public void putFrame(IplImage img) {
		this.showImage(img);
	}
}

/**
 * This is the main UI class
 * 
 * @author Marcus Karpoff, Marko Babic
 * 
 */
public class UI extends CanvasFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JRadioButton mi1_1;
	private JRadioButton mi1_2;
	private JRadioButton mi1_3;

	private int selected = 1;

	private JMenuItem mi2_1;
	private JMenuItem mi2_2;
	private JMenuItem mi2_3;

	private Boolean mi2_1_active = false;
	private Boolean mi2_2_active = false;
	private Boolean mi2_3_active = false;

	private MyPanel origPanel;
	private MyPanel backPanel;
	private MyPanel facePanel;

	/**
	 * Initializes the UI
	 */
	public UI() {
		super("Super Sexy Research Program -- Original");

		buildMenuBar();

		setSize(2 * Settings.WIDTH, 2 * Settings.HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Puts the image files into the frames.
	 * 
	 * @param orig
	 *            The original IplImage
	 * @param back
	 *            The background IplImage
	 * @param face
	 *            The extracted foreground IplImage
	 */
	public void putFrame(IplImage orig, IplImage back, IplImage face) {
		switch (selected) {
		case 1:
			showImage(orig);
			break;
		case 2:
			showImage(back);
			break;
		case 3:
			showImage(face);
			break;
		default:
			break;
		}
		if (mi2_1_active)
			origPanel.putFrame(orig);
		if (mi2_2_active)
			backPanel.putFrame(back);
		if (mi2_3_active)
			facePanel.putFrame(face);

	}
	
	/**
	 * Action listener
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == mi1_1 && mi1_1.isSelected()) {
			setTitle("Super Sexy Research Program -- Original");
			selected = 1;
		} else if (source == mi1_2 && mi1_2.isSelected()) {
			setTitle("Super Sexy Research Program -- Background");
			selected = 2;
		} else if (source == mi1_3 && mi1_3.isSelected()) {
			setTitle("Super Sexy Research Program -- Foreground");
			selected = 3;
		} else if (source == mi2_1 && !mi2_1_active) {
			origPanel = new MyPanel("Original", mi2_1_active);
			mi2_1_active = true;
		} else if (source == mi2_2 && !mi2_2_active) {
			backPanel = new MyPanel("Background", mi2_2_active);
			mi2_2_active = true;
		} else if (source == mi2_3 && !mi2_3_active) {
			facePanel = new MyPanel("ForeGround", mi2_3_active);
			mi2_3_active = true;
		}
	}
	
	/**
	 * Builds the menu bar
	 */
	private void buildMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menu1 = new JMenu("Mode");
		menu1.setMnemonic(KeyEvent.VK_M);
		ButtonGroup bg = new ButtonGroup();

		mi1_1 = new JRadioButton("Original", true);
		mi1_1.setMnemonic(KeyEvent.VK_O);
		mi1_1.addActionListener(this);
		bg.add(mi1_1);
		menu1.add(mi1_1);

		mi1_2 = new JRadioButton("Background");
		mi1_2.setMnemonic(KeyEvent.VK_B);
		mi1_2.addActionListener(this);
		bg.add(mi1_2);
		menu1.add(mi1_2);

		mi1_3 = new JRadioButton("Foreground");
		mi1_3.setMnemonic(KeyEvent.VK_F);
		mi1_3.addActionListener(this);
		bg.add(mi1_3);
		menu1.add(mi1_3);

		JMenu menu2 = new JMenu("Popout");
		menu2.setMnemonic(KeyEvent.VK_P);

		mi2_1 = new JMenuItem("Original");
		mi2_1.setMnemonic(KeyEvent.VK_O);
		mi2_1.addActionListener(this);
		menu2.add(mi2_1);

		mi2_2 = new JMenuItem("Background");
		mi2_2.setMnemonic(KeyEvent.VK_B);
		mi2_2.addActionListener(this);
		menu2.add(mi2_2);

		mi2_3 = new JMenuItem("Foreground");
		mi2_3.setMnemonic(KeyEvent.VK_F);
		mi2_3.addActionListener(this);
		menu2.add(mi2_3);

		menuBar.add(menu1);
		menuBar.add(menu2);
		setJMenuBar(menuBar);
	}
}
