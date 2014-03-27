package Project;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_video.*;


public class ObjectTracker {

	
	TrackedObject _obj;
	
	public ObjectTracker() {
		_obj = new TrackedObject();
	}
	
	/**
	 * 
	 * @param img
	 * @param rect
	 */
	public void trackNewObject(IplImage img, CvRect rect) {
		float[][] ranges = {_obj._histRange};
		float[] minVal = {0.f};
		float[] maxVal = {0.f};
		int[] size = {_obj._bins};
		int[] tmp1 = {0}, tmp2 = {0};

		
		_obj._bgr 	= cvCreateImage(cvGetSize(img), 8, 3);
		_obj._hsv 	= cvCreateImage(cvGetSize(img), 8, 3);
		_obj._mask 	= cvCreateImage(cvGetSize(img), 8, 1);
		_obj._hue 	= cvCreateImage(cvGetSize(img), 8, 1);
		_obj._prob 	= cvCreateImage(cvGetSize(img), 8, 1);
		

		
		_obj._hist = cvCreateHist(
			1, 				// Number of histogram dimensions.
			size,			// Array of sizes of histogram dimensions
			CV_HIST_ARRAY, 	// Representation format.
			ranges, 		// Array of ranges for each bin
			1 				// Uniformity flag.
		);
		
		this.updateHueImage(img);
		
		// Create histogram representation for the object.
		cvSetImageROI(_obj._hue, rect);
		cvSetImageROI(_obj._mask, rect);
		
		IplImage[] hues = {_obj._hue};
		cvCalcHist(hues, _obj._hist, 0, _obj._mask);
		cvGetMinMaxHistValue(_obj._hist, minVal, maxVal, tmp1, tmp2);
		cvConvertScale(
			_obj._hist.bins(), 
			_obj._hist.bins(), 
			maxVal != null && maxVal[0] > 0.f ? 255.0/maxVal[0] : 0,
			0
		);
		cvResetImageROI(_obj._hue);
		cvResetImageROI(_obj._mask);
		
		_obj._pRect = rect;		
	}
	
	private void updateHueImage(IplImage img) {
		int vmin = 65, vmax = 256, smin = 55;
		
		// Convert image from RGB to BGR.
		cvCvtColor(img, _obj._bgr, CV_RGB2BGR);
		
		// Convert image to HSV colour model.
		cvCvtColor(img, _obj._hsv, CV_BGR2HSV);
		
		// Mask out-of-range values.
		cvInRangeS(
			_obj._hsv,
			cvScalar(0, smin, Math.min(vmin, vmax), 0),
			cvScalar(180, 256, Math.max(vmin, vmax), 0),
			_obj._mask
		);
		
		// Extract hue channel from HSV.
		cvSplit(_obj._hsv, _obj._hue, null, null, null);
	}
	
	public CvRect track(IplImage img) {
		CvConnectedComp components = new CvConnectedComp();
		
		
		
		// Create new hue image.
		updateHueImage(img);
		
		// Create a probability image based on the face histogram.
		IplImage[] imgs = {img};
		cvCalcBackProject(imgs, _obj._prob,	_obj._hist);
		cvAnd(_obj._prob, _obj._mask, _obj._prob, null);
		
		cvCamShift(
			_obj._prob,
			_obj._pRect,
			cvTermCriteria(CV_TERMCRIT_EPS | CV_TERMCRIT_ITER, 10, 1),
			components,
			_obj._currBox
		);
		
		_obj._pRect = components.rect();
		_obj._currRect = components.rect();
		System.out.println(_obj._currRect);
		return _obj._currRect;
	}
}
