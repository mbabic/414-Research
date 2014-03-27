package Project;

import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;

public class TrackedObject {
	public IplImage _bgr, _hsv, _hue, _mask, _prob;

	public CvHistogram _hist;
	public int _bins = 30;
	public float[]_histRange = {0, 180};
	
	public CvRect _pRect;
	public CvBox2D _currBox;
	public CvRect _currRect;
	
	public TrackedObject() {
		_pRect = new CvRect();
		_currRect = new CvRect();
		_currBox = new CvBox2D();
	
	}
}
