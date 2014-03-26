package Project;

import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

/**
 * Implements CAMshift algorithm for the tracking of ONE Face object.
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
	
	/** The object tracked by the CAMshift algorithm */
	private Face _face;
	
	/** 
	 * Range for backprojection histrogram calculated in CAMshift 
	 * tracking algorithm.
	 */
	private int _histRange[] = {0, 180};
	private int _bins;
	private int _range;
	private Mat _bgr;
	
	/**
	 * 
	 * @throws ClassiferLoadFailure 
	 */
	public FaceTracker() throws ClassiferLoadFailure {
		// Load cascade classifier for face detector.
		String classifierDir = Settings.CLASSIFIER_DIR
				+ "haarcascade_frontalface_default.xml";
		_faceCascade = new CvHaarClassifierCascade(cvLoad(classifierDir));
		
		// Init storage for CvSeqs.
		_storage = CvMemStorage.create();
		
		// Set up parameters for CAMshift.
		_bins = 30;
		
		// If loading of classifier failed, throw error.
		if (_faceCascade.isNull()) {
			throw new ClassiferLoadFailure(classifierDir);
		}
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
	
	public void trackNewObject(Mat mrgba, Rect rect) {
		_face.setHSV(new Mat(mrgba.size(), CvType.CV_8UC3));
		_face.setMask(new Mat(mrgba.size(), CvType.CV_8UC1));
		_face.setHue(new Mat(mrgba.size(), CvType.CV_8UC1));
		_face.setProb(new Mat(mrgba.size(), CvType.CV_8UC1));
		
		this.updateHueImage(mrgba);
		
		// Create histogram representation of the face.
		float max = 0.f;
		
		Mat tempMask = new Mat(_face._mask.size(), CvType.CV_8UC1);
		tempMask = _face._mask.submat(rect);
		
		MatOfFloat ranges = new MatOfFloat(0.f, 256.f);
		MatOfInt histSize = new MatOfInt(25);
		
		List<Mat> imgs = Arrays.asList(_face._hueList.get(0).submat(rect));
		Imgproc.calcHist(
			imgs, 
			new MatOfInt(0), 
			tempMask, 
			_face._hist, 
			histSize, 
			ranges
		);
		Core.normalize(_face._hist, _face._hist);
		_face._pRect = rect;
		
		System.out.println(
			"Normalized Histogram Starting"+_face._hist
		);
	}
	
	private void updateHueImage(Mat mrgba) {
		int vmin = 65, vmax = 256, smin = 55;
		
		// rgba to bgr
		_bgr = new Mat(mrgba.size(), CvType.CV_8UC3);
		Imgproc.cvtColor(mrgba, _bgr, Imgproc.COLOR_RGBA2BGR);
		
		// bgr to hsv
		Imgproc.cvtColor(_bgr, _face._hsv, Imgproc.COLOR_BGR2HSV);
		
		// Mask values outside range specified by vmin, vmax, smin
		Core.inRange(
			_face._hsv,
			new Scalar(0, smin, Math.min(vmin, vmax)),
			new Scalar(180, 256, Math.max(vmin, vmax)), 
			_face._mask
		);
		
		// Reset Face object hue and HSV arrays.
		_face._hsvList.clear();
		_face._hueList.clear();
		_face._hsvList.add(_face._hsv);
		_face._hueList.add(_face._hue);
		
		MatOfInt m = new MatOfInt(0, 0);
		Core.mixChannels(_face._hsvList, _face._hueList, m);
	}
	
	public Rect camshiftTrack(Mat mrgba, Rect rect) {
		
		MatOfFloat ranges = new MatOfFloat(0.f, 256.f);
		
		this.updateHueImage(mrgba);
		
		Imgproc.calcBackProject(
			_face._hueList,
			new MatOfInt(0), 
			_face._hist, 
			_face._prob, 
			ranges, 
			255
		);
		
		Core.bitwise_and(
			_face._prob,	// src1
			_face._mask,	// src2
			_face._prob,	// dist
			new Mat()		// bit mask
		);
		
		// Now that we have constructed a histogram, we can use it in
		// camshift algorithm to track object.
		
		_face._currBox = Video.CamShift(
			_face._prob, 
			_face._pRect, 
			new TermCriteria(TermCriteria.EPS,10,1)
		);
		
		_face._pRect = _face._currBox.boundingRect();
	
		return _face._pRect;
	}
	
	
}
