package Project;

/**
 * Class responsible for receiving a 
 * @author Marko Babic, Marcus Karpoff
 *
 */
public class Encoder {

	/** Width of a frame in the video to be encoded. */
	private int _imgHeight;
	
	/** Height of a frame in the video to be encoded. */
	private int _imgWidth;
	
	/** Frame rate of the video to be encoded. */
	private int _fps;
	
	/** The number of frames in the video to be encoded. */
	private int _frames;
	
	
	public Encoder() {
		_imgWidth = Settings.WIDTH;
		_imgHeight = Settings.HEIGHT;
		_fps = Settings.DEFAULT_FPS;

	}
	
	public Encoder(int imgWidth, int imgHeight, int fps, int frames) {
		if (imgWidth > 0) {
			_imgWidth = imgWidth;
		} else {
			_imgWidth = Settings.WIDTH;
		}
		
		if (imgHeight > 0) {
			_imgHeight = imgHeight;
		} else {
			_imgHeight = Settings.HEIGHT;
		}
		
		if (fps > 0) {
			_fps = fps;
		} else {
			_fps = Settings.DEFAULT_FPS;
		}
		
		if (frames > 0) {
			_frames = frames;
		} else {
			_frames = Settings.DEFAULT_FRAMES;
		}
	}
	
}
