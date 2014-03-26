package Project;

import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

/**
 * TODO: description
 * @author Marko Babic, Marcus Karpoff
 *
 */
public class FaceTracker {
	
	/** Instance of class -- one per run-time, implements singleton pattern. */
	FaceTracker _instance;
	
	// Private attribute variables.
	/** The previous frame processed. TODO: needed? */
	private IplImage _pFrame;
	
	/** The sect of Rect describing the faces found in the previous frame. */
	private CvSeq _pFaceSeq;

	/** Cascade classifier used in face detection. */
	private CvHaarClassifierCascade _faceCascade;
	
	/** Storage instance needed by face detector. */
	CvMemStorage _storage;
	
	/**
	 * 
	 * TODO: implement singleton pattern?
	 * @throws ClassiferLoadFailure 
	 */
	public FaceTracker() throws ClassiferLoadFailure {
		String classifierDir = Settings.CLASSIFIER_DIR
				+ "haarcascade_frontalface_default.xml";
		_faceCascade = new CvHaarClassifierCascade(cvLoad(classifierDir));
		_storage = CvMemStorage.create();
		if (_faceCascade.isNull()) {
			throw new ClassiferLoadFailure(classifierDir);
		}
	}
	
	public FaceTracker getInstance() {
		if (_instance == null) {
			try {
			_instance = new FaceTracker();
			} catch (ClassiferLoadFailure clf) {
				System.out.println(clf.toString());
				System.exit(1);
			}
		}
		return _instance;
	}
	
	/**
	 * Uses Haar cascade classifier to detect faces in the given 
	 * input IplImage.
	 * 
	 * @param inputMat
	 *            The original unedited image
	 * @return The location of all the faces
	 */
	public CvSeq detectFaces(IplImage input) {
		CvSeq rects = cvHaarDetectObjects(input, _faceCascade, _storage, 1.5, 3,
				CV_HAAR_DO_CANNY_PRUNING);
		cvClearMemStorage(_storage);
		return rects;
	}
	
	
}
