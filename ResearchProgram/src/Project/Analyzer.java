package Project;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.googlecode.javacv.cpp.opencv_objdetect.CascadeClassifier;

/**
 * Does the facial recognition
 * 
 * @author Marcus
 * 
 */
public class Analyzer {
	private CascadeClassifier faceCascade;

	/**
	 * Constructor for the analyzer.
	 * 
	 * @throws ClassiferLoadFailure
	 */
	Analyzer() throws ClassiferLoadFailure {
		String classifierDir = Settings.CLASSIFIER_DIR
				+ "haarcascade_frontalface_default.xml";
		faceCascade = new CascadeClassifier(classifierDir);
		if (faceCascade.empty()) {
			throw new ClassiferLoadFailure(classifierDir);
		}
	}

	/**
	 * This will look for faces and return the coordinates of the faces
	 */
	public void detectFaces(Mat inputMat) {
		Mat rgbaMat = new Mat();
		Mat greyMat = new Mat();

		Imgproc.cvtColor(rgbaMat, greyMat, Imgproc.COLOR_BGR2GRAY);
	}

	/**
	 * This will return the split video streams. It will take the original video
	 * stream and the location of the faces as input. It will return two video
	 * streams as a tuple. One will have the faces only the other will have
	 * everything else.
	 */
	public void separateStreams() {

	}

	/**
	 * This function will write onto of the frames that have facial data.
	 */
	public void blackOutFaces() {

	}

	/**
	 * This function will do the foreground extraction. Having a standardized
	 * size may be useful for simplification.
	 */
	public void extractVideo() {

	}

	/**
	 * This function takes in the two split video streams and recombines them in
	 * to one video stream.
	 */
	public void recombineVideo() {

	}

}
