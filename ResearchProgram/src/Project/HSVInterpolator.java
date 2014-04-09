package Project;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_imgproc.CvDistanceFunction;

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
	
	public CvScalar barycentricInterpolate(
			CvScalar v0, CvScalar v1, CvScalar v2, CvPoint x0, CvPoint x1,
			CvPoint x2, double x, double y) {
		
		double A0, A1, A2, A;		// Sub-triangle areas
		double a, b, c;				// Triangle side lengths
		double x0p, x1p, x2p;		// Sub-triangles side lengths
		double x0x1, x0x2, x1x2; 	// Lengths of sides of interpolation triangle
		double s;					// half the triangle perimeter
		double B, G, R;				// Interpolated colour component values
				
		x0x1 = Math.sqrt(
			Math.pow(x0.x() - x1.x(), 2) + 
			Math.pow(x0.y() - x1.y(), 2)
		);
		x1x2 = Math.sqrt(
			Math.pow(x1.x() - x2.x(), 2) + 
			Math.pow(x1.y() - x2.y(), 2)
		);
		x0x2 = Math.sqrt(
			Math.pow(x2.x() - x0.x(), 2) + 
			Math.pow(x2.y() - x0.y(), 2)
		);
		x0p = Math.sqrt(
			Math.pow(x - x0.x(), 2) +
			Math.pow(y - x0.y(), 2)
		);
		x1p = Math.sqrt(
			Math.pow(x - x1.x(), 2) +
			Math.pow(y - x1.y(), 2)
		);		
		x2p = Math.sqrt(
			Math.pow(x2.x() - x, 2) +
			Math.pow(x2.y() - y, 2)
		);
		// Calculate A
		a = x0x1;
		b = x1x2;
		c = x0x2;
		s = (a + b + c) / 2f;
		
		A = Math.sqrt(s * (s - a) * (s - b) * (s - c));
		
		// Calculate A0
		a = x1p;
		b = x1x2;
		c = x2p;
		s = (a + b + c) / 2f;
		A0 = Math.sqrt(s * (s - a) * (s - b) * (s - c));
		
		// Calculate A1
		a = x2p;
		b = x0x2;
		c = x0p;
		s = (a + b + c) / 2f;
		A1 = Math.sqrt(s * (s - a) * (s - b) * (s - c));
		
		// Calculate A2
		a = x0x1;
		b = x0p;
		c = x1p;
		A2 = Math.sqrt(s * (s - a) * (s - b) * (s - c));
		
		// Finally, we can interpolate.
		B = ( (A0 * v0.val(0)) + (A1 * v1.val(0)) + (A2 * v2.val(0))) / A;
		G = ( (A0 * v0.val(1)) + (A1 * v1.val(1)) + (A2 * v2.val(1))) / A;
		R = ( (A0 * v0.val(2)) + (A1 * v1.val(2)) + (A2 * v2.val(2))) / A;
		
		return new CvScalar(B, G, R, 1f);
	}
}
