package Project;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.*;
public class ObjectTracker {

	
	TrackedObject _obj;
	
	public ObjectTracker() {
		_obj = new TrackedObject();
	}
	
	public void trackNewObject(IplImage img) {
		_obj._hsv 	= cvCreateImage(cvGetSize(img), CV_U8, 3);
		_obj._mask 	= cvCreateImage(cvGetSize(img), 8, 1);
	}
	
}
