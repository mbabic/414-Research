package Project;

import org.opencv.core.MatOfRect;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_imgproc;
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
	 * 
	 * @param inputMat
	 *            The original unedited image
	 * @return The location of all the faces
	 */
	public MatOfRect detectFaces(CvMat inputMat) {
		CvMat greyMat = inputMat.clone();
		MatOfRect faces = new MatOfRect();
	
		opencv_imgproc.cvCvtColor(inputMat, greyMat, opencv_imgproc.CV_BGR2GRAY);
		opencv_imgproc.cvEqualizeHist(greyMat, greyMat);
//		faceCascade.detectMultiScale(greyMat, faces);
		return faces;
	}

	/**
	 * This will return the split video streams. It will take the original video
	 * stream and the location of the faces as input. It will return two video
	 * streams as a tuple. One will have the faces only the other will have
	 * everything else.
	 */
	public void separateStreams(CvMat inputMat, MatOfRect faces) {

	}

	/**
	 * This function will write onto of the frames that have facial data.
	 * 
	 * @param inputMat
	 *            The Mat of that is going to be blacked out
	 * @param rect
	 *            The rectangle that will be drawn
	 */
	public void blackOutFaces(CvMat inputMat, CvRect rect) {
		opencv_core.cvRectangle(inputMat, new CvPoint(rect.x(),
				(int) (rect.y() - rect.height() * 0.25)), new CvPoint(rect.x()
				+ rect.width(), rect.y() + rect.height()), new CvScalar(0, 255,
				0, 0), opencv_core.CV_FILLED, 8, 0);

	}

	/**
	 * This function will do the foreground extraction. Having a standardized
	 * size may be useful for simplification.
	 */
	public void extractForeground(CvMat origMat, CvMat maskMat) {
		CvMat newMat = new CvMat();
		opencv_core.cvCopy(origMat, maskMat, newMat);

	}

	/**
	 * This function takes in the two split video streams and recombines them in
	 * to one video stream.
	 */
	public void recombineVideo() {

	}

}
