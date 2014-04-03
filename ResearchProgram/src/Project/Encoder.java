package Project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	
	/** The path to the input file. */
	private String _in;
	
	/** The name of the output file to be generated. */
	private String _out;
	
	/**
	 * @param in
	 *  	The path to the input file.
	 * @param out
	 * 		The name of the file to be output.
	 */	
	public Encoder(String in, String out) {
		_imgWidth = Settings.WIDTH;
		_imgHeight = Settings.HEIGHT;
		_fps = Settings.DEFAULT_FPS;
		_in = in;
		_out = out;
	}

	public int getImgHeight() {
		return _imgHeight;
	}

	public void setImgHeight(int _imgHeight) {
		this._imgHeight = _imgHeight;
	}

	public int getImgWidth() {
		return _imgWidth;
	}

	public void setImgWidth(int _imgWidth) {
		this._imgWidth = _imgWidth;
	}

	public int getFps() {
		return _fps;
	}

	public void setFps(int _fps) {
		this._fps = _fps;
	}

	public int getFrames() {
		return _frames;
	}

	public void setFrames(int _frames) {
		this._frames = _frames;
	}

	public String get_in() {
		return _in;
	}

	public void setIn(String _in) {
		this._in = _in;
	}

	public String getOut() {
		return _out;
	}

	public void setOut(String _out) {
		this._out = _out;
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
	
	/**
	 * 
	 */
	public void encode() {
		// Set up encoder
		File encoderFile = new File(Settings.ENCODER);
		String encoder = encoderFile.getAbsolutePath();
	
		String[] args = {encoder, "-i", "fake.yuv"};
		
		try {
			Runtime rt = Runtime.getRuntime();
			
			// Execute encoder with given arguments.
			Process proc = rt.exec(args);
			
			// Get and print errors produced by running program
			InputStream stderr = proc.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String err = null;
			System.err.println("ERROR ---------------------------------");
			while ((err = br.readLine()) != null) {
				System.err.println(err);
			}
			
			
			
			int exitVal = proc.waitFor();
			System.out.println("Process exited with : " + exitVal);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
	}
	
}
