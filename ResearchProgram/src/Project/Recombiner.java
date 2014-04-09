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
		return ret;
	}
	
	public static void bilinearRGBInterpolation(
		IplImage cImage, IplImage bImage, IplImage fImage, 
		ArrayList<CvRect> rects) {
		
		CvScalar b, f, tl, tr, bl, br;
		CvRect boundaries;
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
				if ((2 <= j && j <= imgHeight - 2) && (2 <= x && x <= imgWidth - 2)) {
					// Replace along left hand edge.
					tl = cvGet2D(bImage, j-1, x-2);
					bl = cvGet2D(bImage, j+1, x-2);
					tr = cvGet2D(fImage, j-1, x+2);
					br = cvGet2D(fImage, j+1, x+2);
					boundaries = new CvRect(x-2, j-1, 4, 2);		
						
					cvSet2D(cImage,
							j,
							x-1,
							bilinearRGBInterpolate(tl, tr, bl, br, boundaries,
									x - 1, j)
					);
					cvSet2D(cImage,
							j,
							x,
							bilinearRGBInterpolate(tl, tr, bl, br, boundaries,
									x, j)
					);
					cvSet2D(cImage,
							j,
							x+1,
							bilinearRGBInterpolate(tl, tr, bl, br, boundaries,
									x + 1, j)
					);

					// Interpolate along right hand edge.
					tl = cvGet2D(fImage, j - 1, x + width - 2);
					bl = cvGet2D(fImage, j + 1, x + width - 2);
					tr = cvGet2D(bImage, j - 1, x + width + 2);
					br = cvGet2D(bImage, j + 1, x + width + 2);
					boundaries = new CvRect(x + width - 2, j - 2, 4, 2);
					cvSet2D(
						cImage, j, x + width - 1, 
						bilinearRGBInterpolate(tl, tr, bl, br, boundaries, x + width - 1, j)
					);

					cvSet2D(
						cImage, j, x + width, 
						bilinearRGBInterpolate(tl, tr, bl, br, boundaries, x + width, j)
					);
					cvSet2D(
						cImage, j, x + width + 1, 
						bilinearRGBInterpolate(tl, tr, bl, br, boundaries, x + width + 1, j)
					);
				}
			}	
			// Interpolate along top and bottom edges.
			for (int i = x; i < x + width; i++) {
				// Handle boundary conditions
				if ((0 <= i && i <= imgWidth-2) && (2 <= y && y <= imgHeight - 2)) {
					// Interpolate along top edge.
					tl = cvGet2D(bImage, y-2, i-1);
					bl = cvGet2D(fImage, y+2, i-1);
					tr = cvGet2D(bImage, y-2, i+1);
					br = cvGet2D(fImage, y+2, i+1);
					boundaries = new CvRect(i-1, y-2, 2, 4);
					cvSet2D(cImage,
						y - 1,
						i,
						bilinearRGBInterpolate(tl, tr, bl, br, boundaries, i, y-1)
					);
					cvSet2D(cImage,
						y,
						i,
						bilinearRGBInterpolate(tl, tr, bl, br, boundaries, i, y)
					);
					cvSet2D(cImage,
						y + 1,
						i,
						bilinearRGBInterpolate(tl, tr, bl, br, boundaries, i, y+1)
					);					

					// Interpolate along bottom edge.
					tl = cvGet2D(fImage, y + height - 2, i - 1);
					bl = cvGet2D(bImage, y + height + 2, i - 1);
					tr = cvGet2D(fImage, y + height - 2, i + 1);
					br = cvGet2D(bImage, y + height + 2, i + 1);
					boundaries = new CvRect(i-1, y + height - 2, 2, 4);
					cvSet2D(cImage,
						y + height - 1,
						i,
						bilinearRGBInterpolate(tl, tr, bl, br, boundaries, i, y + height - 1)
					);
					cvSet2D(cImage,
						y + height,
						i,
						bilinearRGBInterpolate(tl, tr, bl, br, boundaries, i, y + height)
					);
					cvSet2D(cImage,
						y + height + 1,
						i,
						bilinearRGBInterpolate(tl, tr, bl, br, boundaries, i, y + height + 1)
					);
					
				}
			}
		}
	}
}
