package Project;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;

/**
 * 
 * @author Marko Babic, Marcus Karpoff
 *
 */
public interface Interpolator {

	/**
	 * 
	 * @param v1
	 * @param v2
	 * @param t
	 * @return
	 */
	public CvScalar linearInterpolate(CvScalar v1, CvScalar v2, double t);
	
	/**
	 * 
	 * @param tl
	 * @param tr
	 * @param bl
	 * @param br
	 * @param boundaries
	 * @param x
	 * @param y
	 * @return
	 */
	public CvScalar bilinearInterpolate(
		CvScalar tl, CvScalar tr, CvScalar bl, CvScalar br,
		CvRect boundaries, double x, double y);
}
