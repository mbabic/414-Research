package Project;

import java.io.File;

import org.opencv.highgui.VideoCapture;

import com.googlecode.javacv.Frame;
import com.googlecode.javacv.FrameRecorder;
import com.googlecode.javacv.FrameRecorder.Exception;
import com.googlecode.javacv.OpenCVFrameRecorder;

/**
 * This section handles video loading, building, and saving.
 * 
 * @author Marcus Karpoff, Marko Babic
 * 
 */
public class Transmitter {

	private FrameRecorder recorderBackGround;
	private FrameRecorder recorderFacial;
	private VideoCapture capture;

	/**
	 * This initializes all the recorders. Must be called before transmitting
	 * files
	 * 
	 * @param bFile
	 *            file location for the background stream.
	 * @param fFile
	 *            file location for the facial stream.
	 */
	public Transmitter(File bFile, File fFile) {

		recorderBackGround = new OpenCVFrameRecorder(bFile, Settings.WIDTH,
				Settings.HEIGHT);
		recorderFacial = new OpenCVFrameRecorder(fFile, Settings.WIDTH,
				Settings.HEIGHT);

	}

	/**
	 * Default option will open a VideoCapture stream using the webcam as base
	 * input device
	 * 
	 * @return A video stream
	 * @throws InterruptedException
	 */
	public VideoCapture receiveStream() throws InterruptedException {
		return receiveStream(0);
	}

	/**
	 * receiveStream takes a file name opens it and returns a VideoCapture
	 * stream.
	 * 
	 * @param fileloc
	 *            Location of the file to be loaded
	 * @return A video stream
	 * @throws InterruptedException
	 */
	public VideoCapture receiveStream(String fileloc)
			throws InterruptedException {
		capture = new VideoCapture(fileloc);

		if (capture.isOpened()) {
			return capture;
		}
		System.err.println("Video Capture File failed to load");
		return null;

	}

	/**
	 * receiveStream takes a number corresponding to the video capture device
	 * that the user desires to use and initializes a video stream from that
	 * device. I will sleep for a second to give the device time to load.
	 * 
	 * @param input
	 *            The number corresponding to the device being loaded
	 * @return A video stream
	 * @throws InterruptedException
	 */
	public VideoCapture receiveStream(int input) throws InterruptedException {
		capture = new VideoCapture(input);

		if (capture.isOpened()) {
			Thread.sleep(900);
			return capture;
		}
		System.err.println("Video Capture Device failed to load");
		return null;

	}

	/**
	 * Adds frames to the video output streams
	 * 
	 * @param bFrame
	 *            Background frame to record
	 * @param fFrame
	 *            Facial data frame to record
	 */
	public void videoBuilder(Frame bFrame, Frame fFrame) {
		try {
			recorderBackGround.record(bFrame);
			recorderFacial.record(fFrame);
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
