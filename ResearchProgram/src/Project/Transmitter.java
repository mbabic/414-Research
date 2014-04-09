package Project;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameRecorder.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.avcodec;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * This section handles video loading, building, and saving.
 * 
 * @author Marcus Karpoff, Marko Babic
 * 
 */
public class Transmitter {

	private FFmpegFrameRecorder recorderBackGround;
	private FFmpegFrameRecorder recorderFacial;
	
	/**
	 * The encoder to be used to encode the stream containing the face data.
	 */
	private Encoder _faceEncoder;
	/**
	 * The encoder to be used to encode the stream containing the background
	 * data.
	 */
	private Encoder _bgEncoder;
	/**
	 * The decoder to be used to decode the stream containing the face data.
	 */
	private Decoder _faceDecoder;
	/**
	 * The decoder to be used to decode the stream containing the background
	 * data.
	 */
	private Decoder _bgDecoder;

	/**
	 * This initializes all the recorders. Must be called before transmitting
	 * files
	 * 
	 * @param bFile
	 *            file location for the background stream.
	 * @param fFile
	 *            file location for the facial stream.
	 */
	public Transmitter() {

	}

	/**
	 * Initializes the video recorders. Using the first image grabbed to set the
	 * dimension.
	 * 
	 * @param bFile
	 *            Background save file.
	 * @param fFile
	 *            Foreground save file.
	 * @param img
	 *            The image that is used to set recording parameters
	 * @throws Exception
	 */
	public void initializeRecorders(File bFile, File fFile, IplImage img)
			throws Exception {
		
		recorderBackGround = new FFmpegFrameRecorder(bFile, img.width(),
				img.height());
		recorderBackGround.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
		// Indicate that we want the encoding to be lossless.
		recorderBackGround.setVideoQuality(0);
		
		recorderFacial = new FFmpegFrameRecorder(fFile, img.width(),
				img.height());
		recorderFacial.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
		// Indicate that we want the encoding to be lossless
		recorderFacial.setVideoQuality(0);
		
		// Initialize encoders
		_bgEncoder = new Encoder(
			bFile.getAbsolutePath(), 
			Settings.ENCODED_OUTB_NAME
		);
		
		_faceEncoder = new Encoder(
			fFile.getAbsolutePath(),
			Settings.ENCODED_OUTF_NAME
		);

		// TODO: get and same frame rate from cmd line, for now using default
		// values defines in Settings.
		_bgEncoder.setImgWidth(img.width());
		_bgEncoder.setImgHeight(img.height());		
		_faceEncoder.setImgWidth(img.width());
		_faceEncoder.setImgHeight(img.height());
		
		recorderBackGround.start();
		recorderFacial.start();
	}
	
	/**
	 * Once a frame has been captured, called to pass the width and height
	 * of a frame to the decoder used by the Transmitter instance.
	 * @param inf
	 * 		Path the the encrypted file containing the face stream.
	 * @param imgWidth
	 * 		The width of a frame in the streams to be recombined.
	 * @param imgHeight
	 * 		The height of a frame in the streams to be recombined.
	 */
	public void setUpDecoders(String inf, String inb, int imgWidth, int imgHeight) {
		_faceDecoder = new Decoder(
			inf, 
			Settings.DECODED_OUTF_NAME,
			imgWidth,
			imgHeight
		);
		_bgDecoder = new Decoder(
			inb,
			Settings.DECODED_OUTB_NAME,
			imgWidth,
			imgHeight
		);
	}

	public void close() throws com.googlecode.javacv.FrameGrabber.Exception,
			Exception {
		recorderBackGround.stop();
		recorderFacial.stop();
		recorderBackGround.release();
		recorderFacial.release();
	}

	/**
	 * Default option will open a FrameGrabber stream using the webcam as input
	 * device
	 * 
	 * @return A video stream
	 * @throws com.googlecode.javacv.FrameGrabber.Exception
	 */
	public FrameGrabber receiveStream()
			throws com.googlecode.javacv.FrameGrabber.Exception {
		return receiveStream(0);
	}

	/**
	 * receiveStream takes a file name opens it and returns a FrameGrabber
	 * stream.
	 * 
	 * @param fileloc
	 *            Location of the file to be loaded
	 * @return A video stream
	 * @throws com.googlecode.javacv.FrameGrabber.Exception
	 */
	public FrameGrabber receiveStream(String fileloc)
			throws com.googlecode.javacv.FrameGrabber.Exception {
		FrameGrabber grabber = new FFmpegFrameGrabber(fileloc);
		grabber.start();
		return grabber;

	}

	/**
	 * receiveStream takes a file name opens it and returns a FrameGrabber
	 * stream.
	 * 
	 * @param file
	 *            The file to be loaded
	 * @return A video stream
	 * @throws com.googlecode.javacv.FrameGrabber.Exception
	 */
	public FrameGrabber receiveStream(File file) throws com.googlecode.javacv.FrameGrabber.Exception {
		FrameGrabber grabber = new FFmpegFrameGrabber(file);
		grabber.start();
		return grabber;
	}
	
	/**
	 * receiveStream takes a number corresponding to the video capture device
	 * that the user desires to use and initializes a video stream from that
	 * device. I will sleep for a second to give the device time to load.
	 * 
	 * @param input
	 *            The number corresponding to the device being loaded
	 * @return A video stream
	 * @throws com.googlecode.javacv.FrameGrabber.Exception
	 */
	public FrameGrabber receiveStream(int input)
			throws com.googlecode.javacv.FrameGrabber.Exception {
		FrameGrabber grabber = new OpenCVFrameGrabber(input);
		grabber.start();
		return grabber;
	}

	/**
	 * Adds frames to the video output streams
	 * 
	 * @param bImage
	 *            Background frame to record
	 * @param fImage
	 *            Facial data frame to record
	 * @throws Exception
	 */
	public void videoBuilder(IplImage bImage, IplImage fImage) throws Exception {
		recorderBackGround.record(bImage);
		recorderFacial.record(fImage);
	}

	/**
	 * Encodes the files produces by the frame grabbers to HEVC
	 */
	public void encodeHECV() {	
		ExecutorService exec = Executors.newFixedThreadPool(2);
		exec.submit(new EncodeFaceTask());
		exec.submit(new EncodeBackgroundTask());
		try {
			// Tell executor to accept no more new processes.
			exec.shutdown();
			// Wait for thread to exit, but do not terminate prematurely.
			exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}	
	}

	/**
	 * Decodes the streams from HEVC
	 */
	public void decodeHEVC() {
		ExecutorService exec = Executors.newFixedThreadPool(2);
		exec.submit(new DecodeFaceTask());
		exec.submit(new DecodeBackgroundTask());
		try {
			// Tell executor to accept no more new processes.
			exec.shutdown();
			// Wait for thread to exit, but do not terminate prematurely.
			exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private class EncodeFaceTask implements Callable<Integer> {
		public EncodeFaceTask() {
			
		}
		
		public Integer call() {
			Encryption encrypter = new Encryption(null);
			_faceEncoder.encode();
			
			encrypter.encryptFile(
				Settings.OUT + _faceEncoder.getOut(),
				Settings.ENCRYPTED_OUTF_NAME
			);
			
			// TODO: clean up outf.avi, encoded_outf.yuv, anything else?
			
			return 0;
		}
	}
	
	private class EncodeBackgroundTask implements Callable<Integer> {
		public EncodeBackgroundTask() {
			
		}
		
		public Integer call() {
			_bgEncoder.encode();
			return 0;
		}
	}
	
	private class DecodeFaceTask implements Callable<Integer> {
		public DecodeFaceTask() {
			
		}
		
		public Integer call() {
			System.out.println(_faceDecoder.getIn());
			Encryption decrypter = new Encryption(null);
			decrypter.decryptFile(
				Settings.OUT + Settings.ENCRYPTED_OUTF_NAME, 
				(_faceDecoder.getIn()).substring(4)
			);
			_faceDecoder.decode();
			return 0;
		}
	}
	
	private class DecodeBackgroundTask implements Callable<Integer> {
		public DecodeBackgroundTask() {
			
		}
		
		public Integer call() {
			_bgDecoder.decode();
			return 0;
		}
	}
}
