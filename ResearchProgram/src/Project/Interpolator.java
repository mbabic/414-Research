package Project;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
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
	public CvScalar linearInterpolate(CvScalar v0, CvScalar v1, double t);
	
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
	
	public CvScalar barycentricInterpolate(
		CvScalar v0, CvScalar v1, CvScalar v2, CvPoint x0, CvPoint x1,
		CvPoint x2, double x, double y);
}
