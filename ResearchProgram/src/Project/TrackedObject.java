package Project;

import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;

public class TrackedObject {
	public IplImage _hsv, _hue, _mask, _prob;
	public CvHistogram _hist;
	
	public CvRect _pRect;
	public CvBox2D _currRect;
	
	public TrackedObject() {
		
	}
}
