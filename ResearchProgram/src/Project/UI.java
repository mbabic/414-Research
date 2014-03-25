package Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
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
	private BufferedImage image;
	
	
	/**
	 * Constructor. This UI panel holds the video stream.
	 */
	public MyPanel(String title){
		super(title);
		setDefaultCloseOperation(CanvasFrame.EXIT_ON_CLOSE);
	}

	public void putFrame(IplImage img) {
		this.showImage(image);
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

	/**
	 * Initializes the UI
	 */
	public UI() {
		super("Super Sexy Research Program -- Original");

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
		
		menuBar.add(menu1);
		setJMenuBar(menuBar);

		setSize(2 * Settings.WIDTH, 2 * Settings.HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
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
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == mi1_1 && mi1_1.isSelected()) {
			setTitle("Super Sexy Research Program -- Original");
			selected = 1;
		} else if (source == mi1_2 && mi1_2.isSelected()) {
			setTitle("Super Sexy Research Program -- Background");
			selected = 2;
		} else if (source == mi1_3 && mi1_3.isSelected()){
			setTitle("Super Sexy Research Program -- Foreground");
			selected = 3;
		}
	}
}
