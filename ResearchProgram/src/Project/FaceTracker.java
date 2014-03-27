package Project;

import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
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
		
		_face = new Face();
		
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
	
	
	
//	http://stackoverflow.com/questions/14877383/iplimage-pixel-access-javacv
	/**
	 * Converts the given JavaCV IplImage to a OpenCV Mat.
	 * @param img
	 *  	The IplImage instance to be converted.
	 * @return
	 *  	OpenCV Mat representation of the given IplImage
	 */
	private static Mat imgToMat(IplImage img) {
		int rows = img.height(), cols = img.width();
		ByteBuffer buffer = img.getByteBuffer();
		Mat mat = new Mat(rows, cols, CvType.CV_8UC3);
//		System.out.println(img);
////		double img_vec[][] = new double[rows][cols];
//		for (int i=0; i < rows; i++) {
//		    for (int j =0; j < cols; j++){
//		        int ind = i * img.widthStep() + j * img.nChannels() + 1;
////		        img_vec[i][j] = (buffer.get(ind) & 0xFF);
//		    	System.out.println("get(ind) returns" + buffer.get(ind));
//
//		        mat.put(i, j, (buffer.get(ind)));
//		    }
//		}
		for (int i = 0; i < rows; i ++) {
			for (int j = 0; j < cols; j++) {
				int index = i * img.widthStep() + j * img.nChannels();
							
				mat.put(i, j, buffer.get(index) & 0xFF, buffer.get(index+1) & 0xFF, buffer.get(index+2) & 0xFF);
			}
		}
		System.out.println(mat);
		System.out.println(mat.get(100, 100));
		return mat;
	}
	
	/**
	 * Set up new object for instance of FaceTracker to track.  Wrapper to 
	 * call of trackNewObject using OpenCV objects as opposed to JavaCV
	 * objects.
	 * @param img 
	 * 		Image in which the object to be tracked appears.
	 * @param cvr
	 *  	Rectangle bounding the object to be tracked.
	 */
	public void trackNewObject(IplImage img, CvRect cvr) {
		
	}
	
	/**
	 * Set up new object for instance of FaceTracker to track.
	 * @param mrgba 
	 * 		RGBA Mat of scene in which Face to be tracked appears.
	 * @param rect 
	 * 		The rectangle bounding the object to be tracked.
	 */
	public void trackNewObject(Mat mrgb, Rect rect) {
		_face._hsv  = 	new Mat(mrgb.size(), CvType.CV_8UC3);
		_face._mask = 	new Mat(mrgb.size(), CvType.CV_8UC1);
		_face._hue  = 	new Mat(mrgb.size(), CvType.CV_8UC1);
		_face._prob = 	new Mat(mrgb.size(), CvType.CV_8UC1);
		
		this.updateHueImage(mrgb);
		
		// Create histogram representation of the face.
		float max = 0.f;
		
		Mat tempMask = new Mat(_face._mask.size(), CvType.CV_8UC1);
		tempMask = _face._mask.submat(rect);
		
		MatOfFloat ranges = new MatOfFloat(0.f, 256.f);
		MatOfInt histSize = new MatOfInt(25);
		
		// Extract rectangular submatrix of mat specified by rect.
		// We extract as array because Imgproc.calcHist() expect list of 
		// images for which to calculate histograms.
		List<Mat> imgs = new ArrayList<Mat>();
		imgs.add(_face._hueList.get(0).submat(rect));
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
			"Normalized Histogram " + _face._hist
		);
	}
	
	private void updateHueImage(Mat mrgb) {
		int vmin = 65, vmax = 256, smin = 55;
		
		// rgba to bgr
		_bgr = new Mat(mrgb.size(), CvType.CV_8UC3);
		Imgproc.cvtColor(mrgb, _bgr, Imgproc.COLOR_RGB2BGR);
		
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
	
	public CvRect camshiftTrack(IplImage img, CvRect cvr) {
		System.out.println(cvr);
		Rect r = (cvr.isNull() != true) ? new Rect(cvr.x(), cvr.y(), cvr.width(), cvr.height()) : _face._pRect;
		_face._pRect = r;
		Rect ret = this.camshiftTrack(
				imgToMat(img)
			);
		System.out.println("Ret: " + ret);
		return new CvRect(ret.x, ret.y, ret.width, ret.height);
	}
	
	public Rect camshiftTrack(Mat rgb) {
		
		MatOfFloat ranges = new MatOfFloat(0.f, 256.f);
		
		this.updateHueImage(rgb);
		
		Imgproc.calcBackProject(
			_face._hueList,
			new MatOfInt(0), 
			_face._hist, 
			_face._prob, 
			ranges, 
			255
		);
		
		int rows = _face._prob.rows(), cols = _face._prob.cols();
		launcher.stupid = IplImage.create(cols, rows, IPL_DEPTH_8U, 1);
		byte[] data = new byte[rows * cols];
		for (int i = 0; i < rows; i ++) {
			for (int j = 0; j < cols; j ++) {
//				int index = i * _face._prob.depth() + j * _face._prob.channels();
				byte[] temp = new byte[1];
				_face._prob.get(i, j, temp);
				data[j + (i * rows)] = temp[0];
			}
		}
		launcher.stupid.getByteBuffer().put(data);
		

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
