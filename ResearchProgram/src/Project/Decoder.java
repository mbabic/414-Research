package Project;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Provides functionality with regard to decoding an HEVC compliant bitstream.
 * @author Marko Babic, Marcus Karpoff
 *
 */
public class Decoder {
	
	
	/** Path to the input file to be decoded.  Must be a HEVC compliant
	 * bit stream. */
	private String _in;
	
	/** Path to intermediate .yuv file generated at decompression time. */
	private String _yuv;
	
	
	/** Name of the file to be output.  Placed in Settings.OUT. */
	private String _out;
	
	/** Frame width of the input video file. */
	private int _imgWidth;
	
	/** Frame height of the input video file. */
	private int _imgHeight;
	
	
	public Decoder(String in, String out, int imgWidth, int imgHeight) {
		_in = in;
		_out = out;
		_yuv = Settings.OUT + _out + ".yuv";
		_imgWidth = imgWidth;
		_imgHeight = imgHeight;
	}
	
	public String getIn() {
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
	 * Produce an output .avi file based on instance attribute values.
	 */
	public void decode() {
		decompress();
		toAVI();
	}
	
	public void toAVI() {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		exec.submit(new ToAVITask());
		try {
			// Tell executor to not accept any new jobs.
			exec.shutdown();
			// Wait for thread to exit, but do not terminate prematurely.
			exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}	
	}
	
	/**
	 * Implements Callable interface such that results of conversion to .avi
	 * done by external process "ffmpeg" can be waited on.
	 * @author Marko Babic, Marcus Karpoff
	 */
	private class ToAVITask implements Callable<Integer> {
		public ToAVITask() {
			
		}
		public Integer call() {
			File ffmpegFile = new File(Settings.FFMPEG);
			File inFile = new File(_in);
			String ffmpeg = ffmpegFile.getAbsolutePath();
			String inputVideoPath = inFile.getAbsolutePath();
			
			String[] args = {
				// Path to ffmpeg
				ffmpeg,
				
				// Define input yuv width x height
				"-s",
				Integer.toString(_imgWidth) + "x" + Integer.toString(_imgHeight),

				// Input .yuv file
				"-i",
				_yuv,
				
				// Define video codec to use
				"-vcodec",
				"copy",
				
				// Output .avi binary file
				Settings.OUT + _out + ".avi"
			};
			
			try {
				Runtime rt = Runtime.getRuntime();
				
				// Execute encoder with given arguments.
				Process proc = rt.exec(args);
				
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
	 * Produce a decompressed .yuv file based on instance attribute values.
	 */
	public void decompress() {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		exec.submit(new DecodingTask());
		try {
			// Tell executor to not accept any new jobs.
			exec.shutdown();
			// Wait for thread to exit, but do not terminate prematurely.
			exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}		
	}
	
	/**
	 * 
	 * @author Marko Babic, Marcus Karpoff
	 */
	private class DecodingTask implements Callable<Integer> {
		public DecodingTask() {
			
		}
		
		@Override
		public Integer call() {
			File decoderFile = new File(Settings.DECODER);
			File inFile = new File(_in);
			String decoder = decoderFile.getAbsolutePath();
			String inputVideoPath = inFile.getAbsolutePath();
			
			String[] args = {
				// Path to decoder
				decoder,
				
				// Input hevc binary file
				"-b",
				inputVideoPath,
				
				// Output yuv file
				"-o",
				_yuv
			};
			
			try {
				Runtime rt = Runtime.getRuntime();
				
				// Execute encoder with given arguments.
				Process proc = rt.exec(args);
				
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
