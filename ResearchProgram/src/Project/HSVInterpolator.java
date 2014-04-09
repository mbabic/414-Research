package Project;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;

public class HSVInterpolator implements Interpolator {
	/**
	 * Returns an interpolated CvScalar in the HSV color space based on
	 * parameters passed.
	 * 
	 * @param v0
	 *            The first CvScalar on the interpolation line.
	 * @param v1
	 *            The second CvScalar on the interpolation line.
	 * @param t
	 *            Value indicating position at line whose interpolated scalar
	 *            values we want to calculate (0 <= t < 0. 5 => closer to v0,
	 *            0.5 < t <= 1 => closer to v1)
	 * @return A CvScalar in the HSV color space with Hue, Saturation, and Value
	 *         values set as per the result of the interpolation operation.
	 */
	public CvScalar linearInterpolate(CvScalar v0, CvScalar v1, double t) {
		return new CvScalar(
				Math.floor(v0.val(0) + (v1.val(0) - v0.val(0)) * t),
				Math.floor(v0.val(1) + (v1.val(1) - v0.val(1)) * t),
				Math.floor(v0.val(2) + (v1.val(2) - v0.val(2)) * t), 1f);	
	}
	
	public CvScalar bilinearInterpolate(
			CvScalar tl, CvScalar tr, CvScalar bl, CvScalar br,
			CvRect boundaries, double x, double y) {
		
		double h, s, v, commonRatio;
		double  x1 = boundaries.x(), x2 = boundaries.x() + boundaries.width(),
				y1 = boundaries.y(), y2 = boundaries.y() + boundaries.height();
		
		commonRatio = 1f / ((x2 - x1)*(y2 - y1));
		
		h = commonRatio * (
				(tl.val(0) * (x2 - x)  * (y2 - y)) +
				(tr.val(0) * (x - x1)  * (y2 - y)) +
				(bl.val(0) * (x2 - x)  * (y - y1)) +
				(br.val(0) * (x - x1)  * (y - y1))
			);
		s = commonRatio * (
				(tl.val(1) * (x2 - x)  * (y2 - y)) +
				(tr.val(1) * (x - x1)  * (y2 - y)) +
				(bl.val(1) * (x2 - x)  * (y - y1)) +
				(br.val(1) * (x - x1)  * (y - y1))
			);
		v = commonRatio * (
			(tl.val(2) * (x2 - x)  * (y2 - y)) +
			(tr.val(2) * (x - x1)  * (y2 - y)) +
			(bl.val(2) * (x2 - x) * (y - y1)) +
			(br.val(2) * (x - x1)  * (y - y1))
		);
		return new CvScalar(h, s, v, 1f);
	}
	
}
