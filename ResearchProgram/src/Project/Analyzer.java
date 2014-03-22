package Project;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvCloneFunc;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.googlecode.javacv.cpp.opencv_imgproc;;

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
	 * 
	 * @param inputMat
	 *            The original unedited image
	 * @return The location of all the faces
	 */
	public MatOfRect detectFaces(Mat inputMat) {
		Mat greyMat = new Mat();
		MatOfRect faces = new MatOfRect();

		inputMat.copyTo(greyMat);

		Imgproc.cvtColor(inputMat, greyMat, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(greyMat, greyMat);
		faceCascade.detectMultiScale(greyMat, faces);
		return faces;
	}

	/**
	 * This will return the split video streams. It will take the original video
	 * stream and the location of the faces as input. It will return two video
	 * streams as a tuple. One will have the faces only the other will have
	 * everything else.
	 */
	public void separateStreams(Mat inputMat, MatOfRect faces) {

	}

	/**
	 * This function will write onto of the frames that have facial data.
	 * 
	 * @param inputMat
	 *            The Mat of that is going to be blacked out
	 * @param rect
	 *            The rectangle that will be drawn
	 */
	public void blackOutFaces(Mat inputMat, Rect rect) {
		Core.rectangle(inputMat,
				new Point(rect.x, rect.y - rect.height * 0.25), new Point(
						rect.x + rect.width, rect.y + rect.height), new Scalar(
						0, 255, 0), Core.FILLED, 8, 0);

	}

	/**
	 * This function will do the foreground extraction. Having a standardized
	 * size may be useful for simplification.
	 */
	public void extractForeground(Mat origMat, Mat newMat) {
		
	}

	/**
	 * This function takes in the two split video streams and recombines them in
	 * to one video stream.
	 */
	public void recombineVideo() {

	}

}
