package Project;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;

public class RGBInterpolator implements Interpolator {

	public CvScalar linearInterpolate(CvScalar v1, CvScalar v2, double t) {
		return null;
	}
	
	public CvScalar bilinearInterpolate(
			CvScalar tl, CvScalar tr, CvScalar bl, CvScalar br,
			CvRect boundaries, double x, double y) {
		
		double r, g, b, commonRatio;
		double  x1 = boundaries.x(), x2 = boundaries.x() + boundaries.width(),
				y1 = boundaries.y(), y2 = boundaries.y() + boundaries.height();
		
		commonRatio = 1f / ((x2 - x1)*(y2 - y1));

		r = commonRatio * (
			(tl.val(2) * (x2 - x)  * (y2 - y)) +
			(tr.val(2) * (x - x1)  * (y2 - y)) +
			(bl.val(2) * (x2 - x) * (y - y1)) +
			(br.val(2) * (x - x1)  * (y - y1))
		);

		g = commonRatio * (
			(tl.val(1) * (x2 - x)  * (y2 - y)) +
			(tr.val(1) * (x - x1)  * (y2 - y)) +
			(bl.val(1) * (x2 - x) * (y - y1)) +
			(br.val(1) * (x - x1)  * (y - y1))
		);
		b = commonRatio * (
			(tl.val(0) * (x2 - x)  * (y2 - y)) +
			(tr.val(0) * (x - x1)  * (y2 - y)) +
			(bl.val(0) * (x2 - x)  * (y - y1)) +
			(br.val(0) * (x - x1)  * (y - y1))
		);
		
		CvScalar ret = new CvScalar(b, g, r, 1f);
		return ret;	}
	
}
