package Project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

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
	
	/** The name of the output file to be generated. */
	private String _out;
	
	/**
	 * 
	 * @param out
	 * 		The name of the file to be output.
	 */
	public Encoder(String out) {
		_imgWidth = Settings.WIDTH;
		_imgHeight = Settings.HEIGHT;
		_fps = Settings.DEFAULT_FPS;
		_out = out;
	}
	
	public Encoder(String out, int imgWidth, int imgHeight, int fps, int frames) {
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
		
		_out = out;
	}
	
	/**
	 * Prepare a configuration file to be supplied to the HM HEVC encoder.
	 * (Made public for ease of testing such that we don't have to reflection
	 * or any tricks like that)
	 */
	public void writeConfigurationFile() {
		try {
			File f = new File(Settings.CFG + _out);
			Writer writer = new OutputStreamWriter(
				new FileOutputStream(Settings.CFG + _out + ".cfg"),
				"UTF-8"
			);
			writer.write("InputBitDepth: 8\r\n");
			writer.write("FrameRate: " + _fps + "\r\n");
			writer.write("FrameSkip: 0\r\n");
			writer.write("SourceWidth: " + _imgWidth + "\r\n");
			writer.write("SourceHeight: " + _imgHeight + "\r\n");
			writer.write("FramesToBeEncoded: " + _frames + "\r\n");
			writer.write("Level: 6\r\n");
			writer.write("QP: 27\r\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
}
