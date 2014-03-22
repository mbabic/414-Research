package Project;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This UI panel holds the video stream
 * 
 * @author Marcus Karpoff, Marko Babic
 *
 */
class MyPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor. This UI panel holds the video stream.
	 */
	public MyPanel() {
		super();
		this.setSize(Settings.WIDTH, Settings.HEIGHT);
	}
}
/**
 * This is the main UI class
 * 
 * @author Marcus Karpoff, Marko Babic
 *
 */
public class UI extends JFrame{

	private static final long serialVersionUID = 1L;
	public MyPanel videoPanel;
	
	/**
	 * This is the main UI class
	 */
	public UI() {
		super("Super Sexy Research Program");
		this.setSize(Settings.WIDTH, Settings.HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		videoPanel = new MyPanel();
		this.setContentPane(videoPanel);
		this.setVisible(true);
	}
}
