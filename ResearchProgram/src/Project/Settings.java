package Project;

/**
 * Settings for global variables
 * 
 * @author Marcus Karpoff, Marko Babic
 */
public class Settings {

	public static final int HEIGHT = 576;
	public static final int WIDTH = 704;
	public static final int FRAMERATE = 60;
	public static final String CLASSIFIER_DIR = "res/haarcascades/";

	public static final String OUTF = "out/outf.avi";
	public static final String OUTB = "out/outb.avi";
	/** 
	 * Name of the hevc encoded stream corresponding to the video stream
	 * containing the face data.
	 */
	public static final String ENCODED_OUTF_NAME = "encoded_outf.hevc";
	/**
	 * Name of the hevc encoded stream corresponding to the video stream 
	 * containing the background data.
	 */
	public static final String ENCODED_OUTB_NAME = "encoded_outb.hevc";
	/**
	 * Name of the encrypted hevc stream corresponding to the video stream 
	 * containing the face data.
	 */
	public static final String ENCRYPTED_OUTF_NAME = "encrypted_outf";
	/** 
	 * Name of the decoded file containing the face data video stream.
	 */
	public static final String DECODED_OUTF_NAME = "decoded_outf";
	/**
	 * Name of the decoded file containing the background data video stream.
	 */
	public static final String DECODED_OUTB_NAME = "decoded_outb";	
	
	/**
	 * Name of the decrypted file containing the face data video stream.
	 */
	public static final String DECRYPTED_OUTF_NAME = "decrypted_outf.hevc";
	
	public static final String FACESTREAM_OUT = "out/face_stream.ser";
	public static final String DEFAULT_PASSWORD = "password";

	
	// Encoder settings
	/** Default encoding/decoding FPS. */
	public static final int DEFAULT_FPS = 60;
	/** Default number of frames to be encoded/decoded. */
	public static final int DEFAULT_FRAMES = 100;	

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
	
	/** Boolean value indicating if pixel values on rectangle border should
	 * be saved. */
	public static boolean SAVE_PIXELS = true;
	
}
