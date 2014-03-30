package Project;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_video.*;

/**
 * Tracks a ROI of an IplImage using the camshift tracking algorithm.
 * @author Marko Babic, Marcus Karpoff
 * Based on the C++ code available at:
 * https://gist.github.com/lamberta/231696
 */
public class ObjectTracker {

	/**
	 * The object which this instance of ObjectTracker tracks.
	 */
	TrackedObject _obj;
	
	/**
	 * Constructor.  Init object to be tracked.
	 */
	public ObjectTracker() {
		_obj = new TrackedObject();
	}
	
	/**
	 * NOT GOOD O-O
	 */
	public boolean _isPaired;
	
	/**
	 * 
	 * @param img 
	 * 		The frame in which the object to be tracked exists.
	 * @param rect
	 *  	Bounding rect of object to be tracked (ROI).
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
	
	/**
	 * Update hue channel image for tracked object given current frame.
	 * TODO: move to TrackedObject?
	 * @param img
	 *  	The image from which to update tracked object's hue image.
	 */
	private void updateHueImage(IplImage img) {
		int vmin = 65, vmax = 256, smin = 55;
		
		// Convert image from RGB to BGR.
		cvCvtColor(img, _obj._bgr, CV_RGB2BGR);
		
		// Convert image to HSV colour model.
		cvCvtColor(_obj._bgr, _obj._hsv, CV_BGR2HSV);
		
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
	
	/**
	 * 
	 * @param img
	 *  	The image in which to find the object being tracked.
	 * @return
	 * 		Bounding rect of image if found in object.  If object not found,
	 * 		rect with 0 width and height returned.
	 */
	public CvRect track(IplImage img) {
		CvConnectedComp components = new CvConnectedComp();
		// Create new hue image.
		updateHueImage(img);
				
		// Create a probability image based on the face histogram.
		IplImage[] imgs = {_obj._hue};
		cvCalcBackProject(imgs, _obj._prob,	_obj._hist);
		cvAnd(_obj._prob, _obj._mask, _obj._prob, null);
		
		cvCamShift(
			_obj._prob,
			_obj._pRect,
			// TODO: make thorough examination of termination criteria
			// object
			cvTermCriteria(CV_TERMCRIT_EPS | CV_TERMCRIT_ITER, 10, 1),
			components,
			_obj._currBox
		);
				
		// Create new CvRect as reference to components is destroyed at next
		// iteration of the algorithm and _pRect will be set to null before 
		// application of camshift algorithm.
		_obj._pRect = new CvRect(
			components.rect().x(), 
			components.rect().y(), 
			components.rect().width(), 
			components.rect().height()
		);
		
		// TODO: test if creation of new CvRect is necessary or if we can simply
		// return reference to _obj._pRect
		return new CvRect(
			components.rect().x(), 
			components.rect().y(), 
			components.rect().width(), 
			components.rect().height()
		);
	}
	
	/**
	 * Uses the following criterion to determine if the object tracker instance
	 * has lost the object it was meant to be tracking.
	 * - If _obj._pRect has 0 length/width, then the object has been lost.
	 * - If _obj._prect has width > 5 * height or height > 10 * width, the 
	 *   object is considered to have been lost.
	 * @return
	 */
	public boolean hasLostObject() {
		if (_obj._pRect.x() == 0 && _obj._pRect.y() == 0 &&
			_obj._pRect.width() == 0 && _obj._pRect.height() == 0) {
			return true;
		} else if (_obj._pRect.width() > (5 * _obj._pRect.height())){
			
		}
		return false;
	}
	
	/**
	 * Determines this instance of ObjectTracker is "equal" to the one passed.
	 * Equality is determined by whether or not they are tracking the same 
	 * object.
	 * @param x
	 * 		The ObjectTracker instance against which equality is to be checked.
	 * @return
	 * 		True if the two instance are equal.  False otherwise.
	 */
	public boolean equals(ObjectTracker x) {
		if (x == null) return false;
		CvRect xRect = x._obj._pRect, thisRect = _obj._pRect;
		if ((xRect.x() == thisRect.x()) && 
			(xRect.y() == thisRect.y())) return true;
		return false;
	}
}
