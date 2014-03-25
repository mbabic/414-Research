package Project;

import java.io.File;

import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameRecorder;
import com.googlecode.javacv.FrameRecorder.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * This section handles video loading, building, and saving.
 * 
 * @author Marcus Karpoff, Marko Babic
 * 
 */
public class Transmitter {

	private FrameRecorder recorderBackGround;
	private FFmpegFrameRecorder recorderFacial;
	private FrameGrabber grabber;

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

	public  void initializeRecorders(File bFile, File fFile, IplImage img) throws Exception{
		recorderBackGround = new FFmpegFrameRecorder(bFile, img.width(),
				img.height());
		recorderFacial = new FFmpegFrameRecorder(fFile, img.width(),
				img.height());
		
		
		recorderBackGround.start();
		recorderFacial.start();
	}
	
	
	/**
	 * Default option will open a FrameGrabber stream using the webcam as input
	 * device
	 * 
	 * @return A video stream
	 * @throws InterruptedException
	 */
	public FrameGrabber receiveStream() {
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
	public FrameGrabber receiveStream(String fileloc) {
		grabber = new OpenCVFrameGrabber(fileloc);
		try {
			grabber.start();
		} catch (com.googlecode.javacv.FrameGrabber.Exception e) {
			System.err.println("FrameGrabber failed to start.");
			e.printStackTrace();
		}
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
	public FrameGrabber receiveStream(int input) {

		grabber = new OpenCVFrameGrabber(input);
		try {
			grabber.start();
		} catch (com.googlecode.javacv.FrameGrabber.Exception e) {
			System.err.println("FrameGrabber failed to start.");
			e.printStackTrace();
		}
		return grabber;
	}

	/**
	 * Adds frames to the video output streams
	 * 
	 * @param bImage
	 *            Background frame to record
	 * @param fImage
	 *            Facial data frame to record
	 */
	public void videoBuilder(IplImage bImage, IplImage fImage) {
		try {
			recorderBackGround.record(bImage);
			recorderFacial.record(fImage);
		} catch (Exception e) {
			System.err.println("Failed to write frame to recorder");
			e.printStackTrace();
		}
	}

	/**
	 * TransmitStream will send the video stream out to a file or viewer.
	 * 
	 * @throws Exception
	 */
	public void transmitStream() throws Exception {
		recorderBackGround.stop();
		recorderFacial.stop();
	}

	/**
	 * Encodes the stream with HEVC standard
	 */
	public void encodeHECV() {

	}

	/**
	 * Decodes the stream from HEVC
	 */
	public void decodeHEVC() {

	}
}
