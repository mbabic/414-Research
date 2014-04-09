package Project;

import static com.googlecode.javacv.cpp.opencv_core.cvAbsDiff;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvSet2D;

import java.util.ArrayList;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Recombiner {

	
	public static void linearHSVInterpolation(
			IplImage bImage, IplImage fImage, IplImage cImage) {
		
		
		
	}
	
	/**
	 * 
	 * @param tl
	 * @param tr
	 * @param bl
	 * @param br
	 * @param boundaries
	 * 		CvRect whose four corners are the values the CvScalars
	 * 		tl, tr, bl, r correspond to.
	 * @param x
	 * @param y
	 * @return
	 */
	private static CvScalar bilinearRGBInterpolate(
		CvScalar tl, CvScalar tr, CvScalar bl, CvScalar br,
		CvRect boundaries, double x, double y
	) {

		double r, g, b, commonRatio;
		double  x1 = boundaries.x(), x2 = boundaries.x() + boundaries.width(),
				y1 = boundaries.y(), y2 = boundaries.y() + boundaries.height();
		
		commonRatio = 1f / ((x2 - x1)*(y2 - y1));

		r = commonRatio * (
			(tl.val(0) * (x2 - x)  * (y2 - y)) +
			(tr.val(0) * (x - x1)  * (y2 - y)) +
			(bl.val(0) * (x2 - x1) * (y - y1)) +
			(br.val(0) * (x - x1)  * (y - y1))
		);

		g = commonRatio * (
			(tl.val(1) * (x2 - x)  * (y2 - y)) +
			(tr.val(1) * (x - x1)  * (y2 - y)) +
			(bl.val(1) * (x2 - x1) * (y - y1)) +
			(br.val(1) * (x - x1)  * (y - y1))
		);
		b = commonRatio * (
			(tl.val(2) * (x2 - x)  * (y2 - y)) +
			(tr.val(2) * (x - x1)  * (y2 - y)) +
			(bl.val(2) * (x2 - x1) * (y - y1)) +
			(br.val(2) * (x - x1)  * (y - y1))
		);
		
		return new CvScalar(r, g, b, 1f);
	}
	
	public static void bilinearRGBInterpolation(
		IplImage bImage, IplImage fImage, IplImage cImage, 
		ArrayList<CvRect> rects) {
		
		CvScalar b, f;
		int x, y, width, height;
		int imgWidth = cImage.width();
		int imgHeight = cImage.height();
		
		cvAbsDiff(bImage, fImage, cImage);
		for (CvRect cvr: rects) {
			x = cvr.x();
			y = cvr.y();
			height = cvr.height();
			width = cvr.width();
			
			// Interpolate along left and right hand edges.
			for (int j = y; j < y + height; j++) {
				// Handle boundary conditions
				if ((0 <= j && j <= imgHeight) && (2 <= x && x <= imgWidth - 2)) {
					// Replace along left hand edge.
					b = cvGet2D(bImage, j, x-2);
					f = cvGet2D(fImage, j, x+2);				

					if (!(b.val(0) == 0 && b.val(1) == 0 && b.val(2) == 0)) {
						cvSet2D(cImage, j, x-1, interpolateRGB(b, f, 0.25));
						cvSet2D(cImage, j, x, interpolateRGB(b, f, 0.5));
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, j, x+1, interpolateRGB(b, f, 0.75));
					}
					
					// Interpolate along right hand edge.
					b = cvGet2D(bImage, j, x + width + 2);
					f = cvGet2D(fImage, j, x + width - 2);

					if (!(b.val(0) == 0 && b.val(1) == 0 && b.val(2) == 0)) {
						cvSet2D(cImage, j, x + width, interpolateRGB(b, f, 0.5));
						cvSet2D(cImage, j, x + width + 1, interpolateRGB(b, f, 0.25));
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, j, x + width - 1, interpolateRGB(b, f, 0.75));
					}
				}
			}	
			// Interpolate along top and bottom edges.
			for (int i = x; i < x + width; i++) {
				// Handle boundary conditions
				if ((0 <= i && i <= imgWidth) && (2 <= y && y <= imgHeight - 2)) {
					// Interpolate along top edge.
					b = cvGet2D(bImage, y - 2, i);
					f = cvGet2D(fImage, y + 2, i);
					
					if (!(b.val(0) == 0 && b.val(1) == 0 && b.val(2) == 0)) {
						cvSet2D(cImage, y - 1, i, interpolateRGB(b, f, 0.25));
						cvSet2D(cImage, y, i, interpolateRGB(b, f, 0.5));
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, y + 1, i, interpolateRGB(b, f, 0.75));
					}	
					
					// Interpolate along bottom edge.
					b = cvGet2D(bImage, y + height + 2, i);
					f = cvGet2D(fImage, y + height - 2, i);
					if (!(b.val(0) == 0 && b.val(1) == 0 && b.val(2) == 0)) {
						cvSet2D(cImage, y + height + 1, i, interpolateRGB(b, f, 0.25));
						cvSet2D(cImage, y + height, i, interpolateRGB(b, f, 0.5));
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, y + height - 1, i, interpolateRGB(b, f, 0.75));
					}
				}
			}
		}
	}
		
		
	}
	
}
