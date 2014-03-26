package Project;

import java.util.List;
import java.util.Vector;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;

/**
 * Contains information necesary to track face using CAMshift
 * algorithm.
 * TODO: better description
 * @author Marko Babic, Marcus Karpoff
 *
 */
public class Face {
	
	// Attributes need to be made public due to need to set values via
	// 'pass by reference'.
	public Mat _hsv;
	public Mat _hue;
	public Mat _mask;
	public Mat _prob;
	public Mat _hist;
	
	Rect _pRect;
	RotatedRect _currBox;
	
	public List<Mat> _hsvList;
	public List<Mat> _hueList;
	
	/**
	 * @constructor
	 */
	public Face() {
		_hist = new Mat();
		_pRect = new Rect();
		_currBox = new RotatedRect();
		_hsvList = new Vector<Mat>();
		_hueList = new Vector<Mat>();
	}

	
	
	
	public void setHSV(Mat mat) {
		_hsv = mat;
	}

	public void setMask(Mat mat) {
		_mask = mat;
	}

	public void setProb(Mat mat) {
		_prob = mat;
	}

	public void setHue(Mat mat) {
		_hue = mat;
	}




	public Mat getHSV() {
		return _hsv;
	}
	
}
