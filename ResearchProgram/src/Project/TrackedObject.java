package Project;

import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;

/**
 * Instance of an object to be tracked by ObjectTracker class.
 * @author Marko Babic, Marcus Karpoff
 * @version 0.1
 *
 */
public class TrackedObject {
	
	/** 
	 * BGR coding of image in which object appears.
	 */
	public IplImage _bgr;
	
	/** 
	 * HSV coding of image in which object appears. 
	 */
	public IplImage _hsv;
	
	/** 
	 * Hue map of image in which object appears.
	 */
	public IplImage _hue;
	
	/** 
	 * Mask used in construction of probability coded image in which object
	 * appears.
	 */
	public IplImage _mask;
	
	/**
	 * Probability coded image in which object appears.  Each pixel's grayscale
	 * value corresponds to the likelyhood that that particular pixel belongs
	 * to the object.
	 */
	public IplImage _prob;

	/**
	 * Histogram constructed in construction of probability coded image.
	 */
	public CvHistogram _hist;
	
	/**
	 * Number of bins in _hist.
	 */
	public int _bins = 30;
	
	/**
	 * Range of values in _hist.
	 */
	public float[]_histRange = {0, 180};
	
	/**
	 * ROI rectangle corresponding to position of object in image in previous
	 * frame tracked.
	 */
	public CvRect _pRect;
	
	/**
	 * The CvBOX2d (e.g., rotated rectangle) in which the tracking algorithm
	 * has determined the object is in.
	 */
	public CvBox2D _currBox;
	
	public TrackedObject() {
		_pRect = new CvRect();
		_currBox = new CvBox2D();
	
	}
}
