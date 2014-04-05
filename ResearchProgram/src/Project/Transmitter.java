package Project;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameRecorder.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.avcodec;
import com.googlecode.javacv.cpp.avutil;
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
		
		recorderFacial = new FFmpegFrameRecorder(fFile, img.width(),
				img.height());
		recorderFacial.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
		
		recorderBackGround.start();
		recorderFacial.start();
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
//		grabber.setFormat(avcodec.AV_CODEC_ID_RAWVIDEO);
//		grabber.setFormat("rawvideo");
//		grabber.setFrameRate(30);
//		grabber.setFrameNumber(300);
//		grabber.setImageWidth(352);
//		grabber.setImageHeight(288);
//		grabber.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
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
