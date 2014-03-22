package Project;

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
	public MyPanel() {
		super("Video Frame");
		this.setSize(Settings.WIDTH, Settings.HEIGHT);
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
	private MyPanel videoPanel;

	/**
	 * Initializes the UI
	 */
	public UI() {
		super("Super Sexy Research Program");
		this.setSize(Settings.WIDTH, Settings.HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		videoPanel = new MyPanel();
		this.setContentPane(videoPanel);
		this.setVisible(true);
	}

	/**
	 * Puts a image onto the image screen
	 * 
	 * @param image
	 *            the image to be placed
	 */
	public void putFrame(IplImage image) {
		videoPanel.putFrame(image);
	}

}
