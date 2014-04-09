package Project;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;

public class HSVInterpolator implements Interpolator {

	public CvScalar linearInterpolate(CvScalar v1, CvScalar v2, double t) {
		return null;
	}
	
	public CvScalar bilinearInterpolate(
			CvScalar tl, CvScalar tr, CvScalar bl, CvScalar br,
			CvRect boundaries, double x, double y) {
		return null;
	}
	
}
