package Project;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;

/**
 * Interface implemented by interpolator in various colour spaces.
 * 
 * @author Marko Babic, Marcus Karpoff
 */
public interface Interpolator {

	/**
	 * Perform linear interpolation between the given endpoints.
	 * 
	 * @param v0
	 *            The first endpoint of the line of interpolation.
	 * @param v1
	 *            The second endpoint of the line of interpolation.
	 * @param t
	 *            Parameter indicating the position on the line whose value is
	 *            to be interpolated. 0 <= t <= 1.0
	 * @return CvScalar result of the interpolation.
	 */
	public CvScalar linearInterpolate(CvScalar v0, CvScalar v1, double t);

	/**
	 * Perform bilinear interpolation.
	 * 
	 * @param tl
	 *            The top left corner pixel of the interpolation square.
	 * @param tr
	 *            The top right corner pixel of the interpolation square.
	 * @param bl
	 *            The bottom left corner pixel of the interpolation square.
	 * @param br
	 *            The bottom right corner pixel of the interpolation square.
	 * @param boundaries
	 *            CvRect describing the boundaries of the interpolation square.
	 * @param x
	 *            The x-coordinate of the point whose value is to be
	 *            interpolated.
	 * @param y
	 *            The y-coordinate of the point whose value is to be
	 *            interpolated.
	 * @return The pixel resultant from the interpolation operation.
	 */
	public CvScalar bilinearInterpolate(CvScalar tl, CvScalar tr, CvScalar bl,
			CvScalar br, CvRect boundaries, double x, double y);

	/**
	 * Perform interpolation use barycentric coordinates (triangles).
	 * 
	 * @param v0
	 *            The pixel value of the first vertex of the triangle of
	 *            interpolation.
	 * @param v1
	 *            The pixel value of the second vertex of the triangle of
	 *            interpolation.
	 * @param v2
	 *            The pixel value of the third vertex of the triangle of
	 *            interpolation.
	 * @param x0
	 *            The point defining the position of the first vertex of the
	 *            triangle of interpolation.
	 * @param x1
	 *            The point defining the position of the second vertex of the
	 *            triangle of interpolation.
	 * @param x2
	 *            The point defining the position of the third vertex of the
	 *            triangle of interpolation.
	 * @param x
	 *            The x-coordinate of the point whose value is to be
	 *            interpolated.
	 * @param y
	 *            The y-coordinate of the point whose value is to be
	 *            interpolated.
	 * @return The pixel resultant from the interpolation operation.
	 */
	public CvScalar barycentricInterpolate(CvScalar v0, CvScalar v1,
			CvScalar v2, CvPoint x0, CvPoint x1, CvPoint x2, double x, double y);
}
