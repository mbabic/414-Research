package Project;

import org.opencv.highgui.VideoCapture;

public class Transmitter {

	Transmitter() {
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
		VideoCapture capture = new VideoCapture(fileloc);

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
		VideoCapture capture = new VideoCapture(input);

		if (capture.isOpened()) {
			Thread.sleep(900);
			return capture;
		}
		System.err.println("Video Capture Device failed to load");
		return null;

	}

	/**
	 * TransmitStream will send the video stream out to a file or viewer.
	 */
	public void transmitStream() {

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
