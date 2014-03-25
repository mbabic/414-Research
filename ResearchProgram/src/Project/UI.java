package Project;

import java.awt.BorderLayout;

import javax.swing.JFrame;

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
	 * Constructor. This UI panel holds the video stream.
	 */
	public MyPanel(String title) {
		super(title);
		this.setSize(Settings.WIDTH, Settings.HEIGHT);
		this.setVisible(true);;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void putFrame(IplImage image) {
		this.showImage(image);
	}

}

/**
 * This is the main UI class
 * 
 * @author Marcus Karpoff, Marko Babic
 * 
 */
public class UI extends JFrame {

	private static final long serialVersionUID = 1L;
	private MyPanel origPanel;
	private MyPanel facePanel;
	private MyPanel backPanel;
	/**
	 * Initializes the UI
	 */
	public UI() {
		super("Super Sexy Research Program");
		this.setSize(2 * Settings.WIDTH, 2 * Settings.HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setLayout(new BorderLayout());
		
		origPanel = new MyPanel("Original");
		facePanel = new MyPanel("Faces");
		backPanel = new MyPanel("Background");
	}
	
	public void putFrame(IplImage orig, IplImage face, IplImage back) {
		origPanel.putFrame(orig);
		facePanel.putFrame(face);
		backPanel.putFrame(back);
	}

}
