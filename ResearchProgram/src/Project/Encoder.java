package Project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Responsible for encoding an input video to a hevc compliant bitstream.
 * SEE testEncode() IN EncoderTests FOR SAMPLE USAGE 
 * @author Marko Babic, Marcus Karpoff
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
	
	/** The _path_ to the input file. */
	private String _in;
	
	/** The path to the YUV file to be encoded.  Set based on Settings.OUT
	 * and Encoder._out. */
	private String _yuv;
	
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
		_yuv = Settings.OUT + _out + ".yuv";
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
		// Delete files of the same name as one we are going to create if they
		// exist and hevc encoder doesn't like this.
		String hevcOut = Settings.OUT + _out + ".hevc";
		File hevcFile = new File(hevcOut);
		if (hevcFile.exists()) {
			hevcFile.delete();
		}
		// No need to close file, java.io.File opens no streams

	
		String hevcReconOut = Settings.OUT + "_out" + "_recon.yuv";
		File reconFile = new File(hevcReconOut);
		if (reconFile.exists()) {
			reconFile.delete();
		}
		// No need to close file, java.io.File opens no streams
		
		
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
			writer.write("BitstreamFile: " + Settings.OUT + _out + "\r\n");
			writer.write("ReconFile: " + hevcReconOut + "\r\n\r\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Produce an encoded file based on instance attribute values.
	 */
	public void encode() {
		toYUV();
		compress();
	}
	
	/**
	 * Convert given input file to a YUV420p compliant bitstream.
	 */
	public void toYUV() {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		exec.submit(new ToYUVTask());
		try {
			exec.shutdown();
			// Wait for thread to exit, but do not terminate prematurely.
			exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	/**
	 * Implements Callable interface such that results of conversion to .yuv
	 * done by external process "ffmpeg" can be waited on before calling another
	 * external process to perfrom encoding to hevc bitstream.
	 * @author Marko Babic, Marcus Karpoff
	 */
	private class ToYUVTask implements Callable<Integer> {
		public ToYUVTask() {
			
		}
		
		@Override
		public Integer call() {
			
			File ffmpegFile = new File(Settings.FFMPEG);
			String ffmpegPath = ffmpegFile.getAbsolutePath();
			
			File inFile = new File(_in);
			
			if (inFile.exists()) {
				inFile.delete();
			}
			
			String inputVideoPath = inFile.getAbsolutePath();
			// TODO: make determination as to what the input codec
			// will be.  For now, assuming .avi with h264 codec.
			String[] ffmpegArgs = {
				// Path to executable
				ffmpegPath,
					
				// Input video file
				"-i",
				_in,
				// Pixel format of output stream
				"-pix_fmt",
				"yuv420p",
				
				// Number of frames to place in output stream
				"-vframes",
				Integer.toString(_frames),
				
				// Ouput stream video codec
				"-vcodec",
				"rawvideo",
				
				// Output file
				_yuv
			};
			
			try {
				Runtime rt = Runtime.getRuntime();
				
				// Execute encoder with given arguments.
				Process proc = rt.exec(ffmpegArgs);
				
				// Get and print errors produced by running program
				InputStream stdin = proc.getErrorStream();
				InputStreamReader isr = new InputStreamReader(stdin);
				BufferedReader br = new BufferedReader(isr);
				String in = null;
				while ((in = br.readLine()) != null) {
					System.out.println(in);
				}
				
				int exitVal = proc.waitFor();
				return exitVal;
			} catch (Throwable t) {
				t.printStackTrace();
			}
			
			return -1;
		}
	}
	
	/**
	 * Compress _yuv to an hevc compliant bitstream.
	 */
	public void compress() {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		exec.submit(new EncodingTask());
		try {
			exec.shutdown();
			// Wait for thread to exit, but do not terminate prematurely.
			exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}

	/**
	 * Implements Callable interface such that results of encoding process
	 * can be waited on or the external process can be terminated after certain
	 * amount of time.
	 * @author Marko Babic, Marcus Karpoff
	 *
	 */
	private class EncodingTask implements Callable<Integer> {
		
		public EncodingTask() {
		}
		
		@Override
		public Integer call() {
	
			// Create configuration file needed to encode this video.
			writeConfigurationFile();			
			
			// Set up encoder
			File encoderFile = new File(Settings.ENCODER);
			File inFile = new File(_yuv);
			String encoder = encoderFile.getAbsolutePath();
			String inputVideoPath = inFile.getAbsolutePath();


			
			String[] hevcArgs = {
				// Path to executable
				encoder, 

				// Input video
				"-i", 			
				inputVideoPath,
				
				// Configuration file
				"-c", 			
				Settings.MAIN_CFG,
				
				// Configuration File
				"-c",			
				Settings.CFG + _out + ".cfg"
			};		
			
			try {
				Runtime rt = Runtime.getRuntime();
				
				// Execute encoder with given arguments.
				Process proc = rt.exec(hevcArgs);
				
				// Get and print errors produced by running program
				InputStream stdin = proc.getInputStream();
				InputStreamReader isr = new InputStreamReader(stdin);
				BufferedReader br = new BufferedReader(isr);
				String in = null;
				while ((in = br.readLine()) != null) {
					System.out.println(in);
				}
				int exitVal = proc.waitFor();
				return exitVal;
			} catch (Throwable t) {
				t.printStackTrace();
			}
			
			return -1;
		}
	}	
}
