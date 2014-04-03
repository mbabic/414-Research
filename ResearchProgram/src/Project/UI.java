package Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
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

	/**
	 * This is used for creating pop out windows
	 * 
	 * @param title
	 *            The title for the window
	 */
	public MyPanel(String title) {
		super(title);
		setVisible(false);
		setSize(Settings.WIDTH, Settings.HEIGHT);
		setDefaultCloseOperation(CanvasFrame.HIDE_ON_CLOSE);
	}

	/**
	 * Fills the window with an IplImage
	 * 
	 * @param img
	 *            the IplImage to fill the window.
	 */
	public void putFrame(IplImage img) {
		this.showImage(img);
	}

	public void destroy() {
		this.dispose();
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

	// Menu items
	private JRadioButton mi1_1;
	private JRadioButton mi1_2;
	private JRadioButton mi1_3;

	private int selected = 1;

	private JMenuItem mi2_1;
	private JMenuItem mi2_2;
	private JMenuItem mi2_3;
	
	private JRadioButton mi3_1;
	private JRadioButton mi3_2;
	
	private boolean videoOn = true;

	private MyPanel origPanel = new MyPanel("Original");
	private MyPanel backPanel = new MyPanel("Background");
	private MyPanel facePanel = new MyPanel("ForeGround");

	/**
	 * Initializes the UI
	 */
	public UI() {
		super("Super Sexy Research Program -- Original");

		buildMenuBar();
		setDefaultCloseOperation(CanvasFrame.HIDE_ON_CLOSE);
		setSize(Settings.WIDTH, Settings.HEIGHT);
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
		if (videoOn) {
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
			origPanel.putFrame(orig);
			backPanel.putFrame(back);
			facePanel.putFrame(face);
		}
	}

	/**
	 * Destroys the UI panel and all the pop out panels.
	 */
	public void destroy() {
		origPanel.destroy();
		backPanel.destroy();
		facePanel.destroy();
		this.dispose();
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
		} else if (source == mi2_1) {
			origPanel.setVisible(true);
		} else if (source == mi2_2) {
			backPanel.setVisible(true);
		} else if (source == mi2_3) {
			facePanel.setVisible(true);
		} else if (source == mi3_1) {
			videoOn = true;
		} else if (source == mi3_2) {
			videoOn = false;
		}
	}

	/**
	 * Builds the menu bar
	 */
	private void buildMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menu1 = new JMenu("Mode");
		menu1.setMnemonic(KeyEvent.VK_M);
		ButtonGroup buttonGroup1 = new ButtonGroup();

		mi1_1 = new JRadioButton("Original", true);
		mi1_1.setMnemonic(KeyEvent.VK_O);
		mi1_1.addActionListener(this);
		buttonGroup1.add(mi1_1);
		menu1.add(mi1_1);

		mi1_2 = new JRadioButton("Background");
		mi1_2.setMnemonic(KeyEvent.VK_B);
		mi1_2.addActionListener(this);
		buttonGroup1.add(mi1_2);
		menu1.add(mi1_2);

		mi1_3 = new JRadioButton("Foreground");
		mi1_3.setMnemonic(KeyEvent.VK_F);
		mi1_3.addActionListener(this);
		buttonGroup1.add(mi1_3);
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

		JMenu menu3 = new JMenu("Video");
		menu3.setMnemonic(KeyEvent.VK_V);
		
		ButtonGroup buttonGroup3 = new ButtonGroup();
		
		mi3_1 = new JRadioButton("On", true);
		mi3_1.setMnemonic(KeyEvent.VK_O);
		mi3_1.addActionListener(this);
		buttonGroup3.add(mi3_1);
		menu3.add(mi3_1);
		
		mi3_2 = new JRadioButton("Off");
		mi3_2.setMnemonic(KeyEvent.VK_F);
		mi3_2.addActionListener(this);
		buttonGroup3.add(mi3_2);
		menu3.add(mi3_2);
		
		menuBar.add(menu1);
		menuBar.add(menu2);
		menuBar.add(menu3);
		setJMenuBar(menuBar);
	}
}
