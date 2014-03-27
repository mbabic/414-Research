package Project;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_core.cvAbsDiff;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import org.opencv.core.Rect;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

/**
 * Does the facial recognition
 * 
 * @author Marcus
 * 
 */
public class Analyzer {
	private CvHaarClassifierCascade faceCascade;
	private CvMemStorage storage;
	private ObjectTracker _tracker;

	/**
	 * Constructor for the analyzer.
	 * 
	 * @throws ClassiferLoadFailure
	 */
	Analyzer() throws ClassiferLoadFailure {
		String classifierDir = Settings.CLASSIFIER_DIR
				+ "haarcascade_frontalface_default.xml";
		faceCascade = new CvHaarClassifierCascade(cvLoad(classifierDir));
		storage = CvMemStorage.create();
		if (faceCascade.isNull()) {
			throw new ClassiferLoadFailure(classifierDir);
		}
		_tracker = new ObjectTracker();
	}

	public static int flag = 0;
	
	/**
	 * This will look for faces and return the coordinates of the faces
	 * 
	 * @param inputMat
	 *            The original unedited image
	 * @return The location of all the faces
	 */
	private CvSeq detectFaces(IplImage input) {
		CvSeq rects = cvHaarDetectObjects(input, faceCascade, storage, 1.5, 3,
				CV_HAAR_DO_CANNY_PRUNING);
		
		if ((rects.total() > 0) && (flag == 0)) {
			CvRect cvr = new CvRect(cvGetSeqElem(rects, 0));
			_tracker.trackNewObject(input, cvr);
			System.out.println("setting flag.  cvr = " + cvr);
			flag = 1;
		} else if (flag == 1) {
			CvRect cvr = new CvRect(cvGetSeqElem(rects, 0));
			System.out.println(cvr);
			if (cvr.isNull() != true) {
				_tracker._obj._pRect = cvr;
			}
			CvRect newCvr = _tracker.track(input);
			CvSeq newRects = cvCreateSeq(0, Loader.sizeof(CvSeq.class), Loader.sizeof(CvRect.class), storage);
			cvSeqPush(newRects, newCvr);
			return newRects;
		}
		cvClearMemStorage(storage);
		return rects;

	}

	/**
	 * * This will return the split video streams. It will take the original
	 * video stream and the location of the faces as input. It will return two
	 * video streams as a tuple. One will have the faces only the other will
	 * have everything else.
	 * 
	 * @param orig
	 *            Original IplImage
	 * @param back
	 *            Where the background images will go
	 * @param face
	 *            Where the facial image will go
	 */
	public void separateStreams(IplImage orig, IplImage back, IplImage face) {
		blackOutFaces(back, detectFaces(orig));
		cvAbsDiff(orig, back, face);
	}

	/**
	 * This function will write onto of the frames that have facial data.
	 * 
	 * @param inputMat
	 *            The Mat of that is going to be blacked out
	 * @param rect
	 *            The rectangle that will be drawn
	 */
	private void blackOutFaces(IplImage input, CvSeq rects) {
		int total_Faces = rects.total();
		for (int i = 0; i < total_Faces; i++) {
			CvRect r = new CvRect(cvGetSeqElem(rects, i));
			cvRectangle(input,
					cvPoint(r.x(), r.y() - (int) (r.height() * .25)),
					cvPoint(r.width() + r.x(), r.height() + r.y()),
					CvScalar.BLACK, opencv_core.CV_FILLED, CV_AA, 0);
		}
	}

	/**
	 * This function takes in the two split video streams and recombines them in
	 * to one video stream.
	 */
	public void recombineVideo(IplImage cImage, IplImage bImage, IplImage fImage) {
		// For now, return image ipl as recombined for face tracking testing.
//		cImage = fImage.clone();
		cvAbsDiff(fImage, fImage, cImage);
	}


}
