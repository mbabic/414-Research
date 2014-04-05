package Project;

/**
 * Settings for global variables
 * 
 * @author Marcus Karpoff, Marko Babic
 */
public class Settings {

	public static final int HEIGHT = 400;
	public static final int WIDTH = 400;
	public static final int FRAMERATE = 10;
	public static final String CLASSIFIER_DIR = "res/haarcascades/";

	public static final String OUTF = "out/outf.avi";
	public static final String OUTB = "out/outb.avi";
	public static final String FACESTREAM_OUT = "out/face_stream.ser";
	public static final String DEFAULT_PASSWORD = "password";

	
	// Encoder settings
	/** Default encoding/decoding FPS. */
	public static final int DEFAULT_FPS = 30;
	/** Default number of frames to be encoded/decoded. */
	public static final int DEFAULT_FRAMES = 10;	

	/** Path to ffmpeg executable.*/
	public static final String FFMPEG = "libs/ffmpeg.exe"; 
	/** Path to HM HEVC Encoder executable. */
	public static final String ENCODER = "libs/TAppEncoder.exe";
	/** Path to HM HEVC Decoder executable. */
	public static final String DECODER = "libs/TAppDecoder.exe";	
	
	/** Folder in which configuration files for encoding/decoding are to be
	 * store */
	public static final String CFG = "cfg/";
	/** Main configuration file used in all encodings. */
	public static final String MAIN_CFG = "cfg/main.cfg";
	
	/** Folder in which to store intermediate conversion files */
	public static final String OUT = "out/";
	
}
