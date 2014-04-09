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
	
	public static void bilinearBoundaryInterpolate(
		IplImage cImage, IplImage bImage, IplImage fImage,
		ArrayList<CvRect> rects, Interpolator interpolator			
	) {
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
							interpolator.bilinearInterpolate(tl, tr, bl, br, boundaries,
									x - 1, j)
					);
					cvSet2D(cImage,
							j,
							x,
							interpolator.bilinearInterpolate(tl, tr, bl, br, boundaries,
									x, j)
					);
					cvSet2D(cImage,
							j,
							x+1,
							interpolator.bilinearInterpolate(tl, tr, bl, br, boundaries,
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
						interpolator.bilinearInterpolate(tl, tr, bl, br, boundaries, x + width - 1, j)
					);

					cvSet2D(
						cImage, j, x + width, 
						interpolator.bilinearInterpolate(tl, tr, bl, br, boundaries, x + width, j)
					);
					cvSet2D(
						cImage, j, x + width + 1, 
						interpolator.bilinearInterpolate(tl, tr, bl, br, boundaries, x + width + 1, j)
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
						interpolator.bilinearInterpolate(tl, tr, bl, br, boundaries, i, y-1)
					);
					cvSet2D(cImage,
						y,
						i,
						interpolator.bilinearInterpolate(tl, tr, bl, br, boundaries, i, y)
					);
					cvSet2D(cImage,
						y + 1,
						i,
						interpolator.bilinearInterpolate(tl, tr, bl, br, boundaries, i, y+1)
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
						interpolator.bilinearInterpolate(tl, tr, bl, br, boundaries, i, y + height - 1)
					);
					cvSet2D(cImage,
						y + height,
						i,
						interpolator.bilinearInterpolate(tl, tr, bl, br, boundaries, i, y + height)
					);
					cvSet2D(cImage,
						y + height + 1,
						i,
						interpolator.bilinearInterpolate(tl, tr, bl, br, boundaries, i, y + height + 1)
					);
					
				}
			}
		}
	}
	
	public static void bilinearRGBInterpolation(
		IplImage cImage, IplImage bImage, IplImage fImage, 
		ArrayList<CvRect> rects) {
		bilinearBoundaryInterpolate(cImage, bImage, fImage, rects, new RGBInterpolator());
	}
}
