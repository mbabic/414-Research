package Project;

import static com.googlecode.javacv.cpp.opencv_core.cvAbsDiff;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvSet2D;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2RGB;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_HSV2BGR;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2BGR;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

import java.util.ArrayList;
import java.util.Iterator;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Recombiner {

	/**
	 * 
	 * @param cImage
	 * @param bImage
	 * @param fImage
	 * @param rects
	 * @param interpolator
	 */
	public static void linearBoundaryInterpolate(IplImage cImage,
			IplImage bImage, IplImage fImage, ArrayList<CvRect> rects,
			Interpolator interpolator) {

		CvScalar b, f;
		int x, y, width, height;
		int imgWidth = cImage.width();
		int imgHeight = cImage.height();

		cvAbsDiff(bImage, fImage, cImage);
		for (CvRect cvr : rects) {
			x = cvr.x();
			y = cvr.y();
			height = cvr.height();
			width = cvr.width();

			// Interpolate along left and right hand edges.
			for (int j = y; j < y + height; j++) {
				// Handle boundary conditions
				if ((0 <= j && j <= imgHeight) && (2 <= x && x <= imgWidth - 2)) {
					// Replace along left hand edge.
					b = cvGet2D(bImage, j, x - 2);
					f = cvGet2D(fImage, j, x + 2);

					cvSet2D(cImage, j, x - 1,
							interpolator.linearInterpolate(b, f, 0.25));
					cvSet2D(cImage, j, x,
							interpolator.linearInterpolate(b, f, 0.5));
					cvSet2D(cImage, j, x + 1,
							interpolator.linearInterpolate(b, f, 0.75));

					// Interpolate along right hand edge.
					b = cvGet2D(bImage, j, x + width + 2);
					f = cvGet2D(fImage, j, x + width - 2);

					if (!(b.val(0) == 0 && b.val(1) == 0 && b.val(2) == 0)) {
						cvSet2D(cImage, j, x + width,
								interpolator.linearInterpolate(b, f, 0.5));
						cvSet2D(cImage, j, x + width + 1,
								interpolator.linearInterpolate(b, f, 0.25));
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, j, x + width - 1,
								interpolator.linearInterpolate(b, f, 0.75));
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
						cvSet2D(cImage, y - 1, i,
								interpolator.linearInterpolate(b, f, 0.25));
						cvSet2D(cImage, y, i,
								interpolator.linearInterpolate(b, f, 0.5));
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, y + 1, i,
								interpolator.linearInterpolate(b, f, 0.75));
					}

					// Interpolate along bottom edge.
					b = cvGet2D(bImage, y + height + 2, i);
					f = cvGet2D(fImage, y + height - 2, i);
					if (!(b.val(0) == 0 && b.val(1) == 0 && b.val(2) == 0)) {
						cvSet2D(cImage, y + height + 1, i,
								interpolator.linearInterpolate(b, f, 0.25));
						cvSet2D(cImage, y + height, i,
								interpolator.linearInterpolate(b, f, 0.5));
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, y + height - 1, i,
								interpolator.linearInterpolate(b, f, 0.75));
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param cImage
	 * @param bImage
	 * @param fImage
	 * @param rects
	 * @param interpolator
	 */
	public static void bilinearBoundaryInterpolate(IplImage cImage,
			IplImage bImage, IplImage fImage, ArrayList<CvRect> rects,
			Interpolator interpolator) {
		CvScalar b, f, tl, tr, bl, br;
		CvRect boundaries;
		int x, y, width, height;
		int imgWidth = cImage.width();
		int imgHeight = cImage.height();

		cvAbsDiff(bImage, fImage, cImage);
		for (CvRect cvr : rects) {
			x = cvr.x();
			y = cvr.y();
			height = cvr.height();
			width = cvr.width();

			// Interpolate along left and right hand edges.
			for (int j = y; j < y + height; j++) {
				// Handle boundary conditions
				if ((2 <= j && j <= imgHeight - 2)
						&& (2 <= x && x <= imgWidth - 2)) {
					// Replace along left hand edge.
					tl = cvGet2D(bImage, j - 1, x - 2);
					bl = cvGet2D(bImage, j + 1, x - 2);
					tr = cvGet2D(fImage, j - 1, x + 2);
					br = cvGet2D(fImage, j + 1, x + 2);
					boundaries = new CvRect(x - 2, j - 1, 4, 2);

					cvSet2D(cImage, j, x - 1, interpolator.bilinearInterpolate(
							tl, tr, bl, br, boundaries, x - 1, j));
					cvSet2D(cImage, j, x, interpolator.bilinearInterpolate(tl,
							tr, bl, br, boundaries, x, j));
					cvSet2D(cImage, j, x + 1, interpolator.bilinearInterpolate(
							tl, tr, bl, br, boundaries, x + 1, j));

					// Interpolate along right hand edge.
					tl = cvGet2D(fImage, j - 1, x + width - 2);
					bl = cvGet2D(fImage, j + 1, x + width - 2);
					tr = cvGet2D(bImage, j - 1, x + width + 2);
					br = cvGet2D(bImage, j + 1, x + width + 2);
					boundaries = new CvRect(x + width - 2, j - 2, 4, 2);
					cvSet2D(cImage, j, x + width - 1,
							interpolator.bilinearInterpolate(tl, tr, bl, br,
									boundaries, x + width - 1, j));

					cvSet2D(cImage, j, x + width,
							interpolator.bilinearInterpolate(tl, tr, bl, br,
									boundaries, x + width, j));
					cvSet2D(cImage, j, x + width + 1,
							interpolator.bilinearInterpolate(tl, tr, bl, br,
									boundaries, x + width + 1, j));
				}
			}
			// Interpolate along top and bottom edges.
			for (int i = x; i < x + width; i++) {
				// Handle boundary conditions
				if ((0 <= i && i <= imgWidth - 2)
						&& (2 <= y && y <= imgHeight - 2)) {
					// Interpolate along top edge.
					tl = cvGet2D(bImage, y - 2, i - 1);
					bl = cvGet2D(fImage, y + 2, i - 1);
					tr = cvGet2D(bImage, y - 2, i + 1);
					br = cvGet2D(fImage, y + 2, i + 1);
					boundaries = new CvRect(i - 1, y - 2, 2, 4);
					cvSet2D(cImage, y - 1, i, interpolator.bilinearInterpolate(
							tl, tr, bl, br, boundaries, i, y - 1));
					cvSet2D(cImage, y, i, interpolator.bilinearInterpolate(tl,
							tr, bl, br, boundaries, i, y));
					cvSet2D(cImage, y + 1, i, interpolator.bilinearInterpolate(
							tl, tr, bl, br, boundaries, i, y + 1));

					// Interpolate along bottom edge.
					tl = cvGet2D(fImage, y + height - 2, i - 1);
					bl = cvGet2D(bImage, y + height + 2, i - 1);
					tr = cvGet2D(fImage, y + height - 2, i + 1);
					br = cvGet2D(bImage, y + height + 2, i + 1);
					boundaries = new CvRect(i - 1, y + height - 2, 2, 4);
					cvSet2D(cImage, y + height - 1, i,
							interpolator.bilinearInterpolate(tl, tr, bl, br,
									boundaries, i, y + height - 1));
					cvSet2D(cImage, y + height, i,
							interpolator.bilinearInterpolate(tl, tr, bl, br,
									boundaries, i, y + height));
					cvSet2D(cImage, y + height + 1, i,
							interpolator.bilinearInterpolate(tl, tr, bl, br,
									boundaries, i, y + height + 1));

				}
			}
		}
	}

	
	/**
	 * Perform interpolation on rectangle boundaries using barycentric
	 * coordinates (triangles) and saved pixel data.
	 * @param cImage
	 * 		The image 
	 * @param bImage
	 * 		Background image.
	 * @param fImage
	 * 		Face image.
	 * @param fse
	 * 		The FaceStreamElement containing the rectangles defining the 
	 * 		regions in which the faces lie and the associated pixel data.
	 * @param interpolator
	 *		The interpolator to be used in interpolation operations. 
	 */
	public static void barycentricBoundaryInterpolation(IplImage cImage,
			IplImage bImage, IplImage fImage, FaceStreamElement fse,
			Interpolator interpolator) {
		
		PixelBlockList pbl = fse.getPixelBlocks();
		ArrayList<CvRect> rects = fse.getRectangles().toCvRectList();
		ArrayList<CvScalar> pixels;
		Iterator iter;
		CvRect rect;
		PixelBlock pb;
		CvScalar pixel, v0, v1;
		int x, y, height, width;
		
		cvAbsDiff(bImage, fImage, cImage);
		for (int n = 0; n < rects.size(); n++) {
			pb = pbl.get(n);
			pixels = pb.reconstructPixels();
			iter = pixels.iterator();
			rect = rects.get(n);
			x = rect.x();
			y = rect.y();
			width = rect.width();
			height = rect.height();

			// Pixels were saved in top-left to bottom-left, bottom-left 
			// to bottom-right, bottom-right to top-right, top-right to
			// top-left order.
			// So, we begin along left hand edge.
			for (int j = y; j < y + height; j++) {
				// Handle boundary conditions
				if ((2 <= j && j <= height - 1)
						&& (2 <= x && x <= width - 2)) {
					pixel = pixels.get(j - y);
					v0 = cvGet2D(bImage, j, x-2);
					v1 = cvGet2D(fImage, j, x+2);
					cvSet2D(cImage, j, x,pixel);
					System.out.println(pixel);
					cvSet2D(cImage, j, x - 1,
						interpolator.linearInterpolate(v0, pixel, 0.666)
					);
					cvSet2D(cImage, j, x - 2,
							interpolator.linearInterpolate(v0, pixel, 0.333)
						);
					cvSet2D(cImage, j, x + 1,
						interpolator.linearInterpolate(v1, pixel, 0.5)
					);
				}
				
			}
			
			// Interpolate along bottom edge
			for (int i = x; i < x + width; i++) {
				// Handle boundary conditions
				if ((0 <= i && i <= width - 2)
						&& (2 <= y && y <= height - 2)) {
					pixel = (CvScalar) iter.next();
					v0 = cvGet2D(fImage, y + width - 2, i);
					v1 = cvGet2D(bImage, y + width + 2, i);
					cvSet2D(cImage, y + width - 1, i,
							interpolator.linearInterpolate(v0, pixel, 0.5)
					);
					cvSet2D(cImage, y + width + 1, i,
							interpolator.linearInterpolate(v1, pixel, 0.5)
					);
				}
			}
		}
		
	}
	
	/**
	 * Perform interpolation on rectangle boundaries using barycentric
	 * coordinates (triangles) with no saved pixel data.
	 * @param cImage
	 * 		The image 
	 * @param bImage
	 * 		Background image.
	 * @param fImage
	 * 		Face image.
	 * @param rects
	 * 		Rects defining the regions in which the faces lie.
	 * @param interpolator
	 *		The interpolator to be used in interpolation operations. 
	 */
	public static void barycentricBoundaryInterpolation(IplImage cImage,
			IplImage bImage, IplImage fImage, ArrayList<CvRect> rects,
			Interpolator interpolator) {
		CvScalar v0, v1, v2; // Three vertices of interpolation triangle
		CvPoint x0, x1, x2;
		int x, y, width, height;
		int imgWidth = cImage.width();
		int imgHeight = cImage.height();

		cvAbsDiff(bImage, fImage, cImage);
		for (CvRect cvr : rects) {
			x = cvr.x();
			y = cvr.y();
			height = cvr.height();
			width = cvr.width();
			// Interpolate along left and right hand edges.
			for (int j = y; j < y + height; j++) {
				// Handle boundary conditions
				if ((2 <= j && j <= imgHeight - 1)
						&& (2 <= x && x <= imgWidth - 2)) {
					// Replace along left hand edge.
					// v0 is the top vertex, assumed to have already been
					// interpolated and thus its value is good to use
					v0 = cvGet2D(cImage, j - 1, x);
					v1 = cvGet2D(bImage, j, x - 2);
					v2 = cvGet2D(fImage, j, x + 2);
					x0 = new CvPoint(x, j - 1);
					x1 = new CvPoint(x - 2, j);
					x2 = new CvPoint(x + 2, j);
					cvSet2D(cImage, j, x - 1,
							interpolator.barycentricInterpolate(v0, v1, v2, x0,
									x1, x2, x - 1, j));
					cvSet2D(cImage, j, x, interpolator.barycentricInterpolate(
							v0, v1, v2, x0, x1, x2, x, j));
					cvSet2D(cImage, j, x + 1,
							interpolator.barycentricInterpolate(v0, v1, v2, x0,
									x1, x2, x + 1, j));

					// Interpolate along the right hand edge.
					v0 = cvGet2D(cImage, j - 1, x + width);
					v1 = cvGet2D(fImage, j, x + width - 2);
					v2 = cvGet2D(bImage, j, x + width + 2);
					x0 = new CvPoint(x + width, j - 1);
					x1 = new CvPoint(x + width - 2, j);
					x2 = new CvPoint(x + width + 2, j);
					cvSet2D(cImage, j, x + width - 1,
							interpolator.barycentricInterpolate(v0, v1, v2, x0,
									x1, x2, x + width - 1, j));
					cvSet2D(cImage, j, x + width,
							interpolator.barycentricInterpolate(v0, v1, v2, x0,
									x1, x2, x + width, j));
					cvSet2D(cImage, j, x + width + 1,
							interpolator.barycentricInterpolate(v0, v1, v2, x0,
									x1, x2, x + width + 1, j));

				}
			}
			// Interpolate along top and bottom edges.
			for (int i = x; i < x + width; i++) {
				// Handle boundary conditions
				if ((0 <= i && i <= imgWidth - 2)
						&& (2 <= y && y <= imgHeight - 2)) {
					// Interpolate along top edge.
					v0 = cvGet2D(cImage, y, i - 1);
					v1 = cvGet2D(bImage, y - 2, i);
					v2 = cvGet2D(fImage, y + 2, i);
					x0 = new CvPoint(i - 1, y);
					x1 = new CvPoint(i, y - 2);
					x2 = new CvPoint(i, y + 2);
					cvSet2D(cImage, y - 1, i,
							interpolator.barycentricInterpolate(v0, v1, v2, x0,
									x1, x2, i, y - 1));
					cvSet2D(cImage, y, i, interpolator.barycentricInterpolate(
							v0, v1, v2, x0, x1, x2, i, y));
					cvSet2D(cImage, y + 1, i,
							interpolator.barycentricInterpolate(v0, v1, v2, x0,
									x1, x2, i, y + 1));
					// Interpolate along bottom edge.
					v0 = cvGet2D(cImage, y + height, i - 1);
					v1 = cvGet2D(fImage, y + height - 2, i);
					v2 = cvGet2D(bImage, y + height + 2, i);
					x0 = new CvPoint(i - 1, y + height);
					x1 = new CvPoint(i, y + height - 2);
					x2 = new CvPoint(i, y + height + 2);
					cvSet2D(cImage, y + height - 1, i,
							interpolator.barycentricInterpolate(v0, v1, v2, x0,
									x1, x2, i, y + height - 1));
					cvSet2D(cImage, y + height, i,
							interpolator.barycentricInterpolate(v0, v1, v2, x0,
									x1, x2, i, y + height));
					cvSet2D(cImage, y + height + 1, i,
							interpolator.barycentricInterpolate(v0, v1, v2, x0,
									x1, x2, i, y + height + 1));
				}
			}

		}

	}

	/**
	 * 
	 * @param cImage
	 * @param bImage
	 * @param fImage
	 * @param rects
	 */
	public static void linearRGBInterpolation(IplImage cImage, IplImage bImage,
			IplImage fImage, ArrayList<CvRect> rects) {
		linearBoundaryInterpolate(cImage, bImage, fImage, rects,
				new RGBInterpolator());
	}

	/**
	 * 
	 * @param cImage
	 * @param bImage
	 * @param fImage
	 * @param rects
	 */
	public static void linearHSVInterpolation(IplImage cImage, IplImage bImage,
			IplImage fImage, ArrayList<CvRect> rects) {
		linearBoundaryInterpolate(cImage, bImage, fImage, rects,
				new HSVInterpolator());
	}

	/**
	 * 
	 * @param cImage
	 * @param bImage
	 * @param fImage
	 * @param rects
	 */
	public static void bilinearHSVInterpolation(IplImage cImage,
			IplImage bImage, IplImage fImage, ArrayList<CvRect> rects) {
		IplImage bgrImg = cvCreateImage(cvGetSize(cImage), 8, 3);
		IplImage fHsv = cvCreateImage(cvGetSize(cImage), 8, 3);
		IplImage bHsv = cvCreateImage(cvGetSize(cImage), 8, 3);
		IplImage cHsv = cvCreateImage(cvGetSize(cImage), 8, 3);

		// Convert images to HSV
		cvCvtColor(fImage, bgrImg, CV_RGB2BGR);
		cvCvtColor(bgrImg, fHsv, CV_BGR2HSV);
		cvCvtColor(bImage, bgrImg, CV_RGB2BGR);
		cvCvtColor(bgrImg, bHsv, CV_BGR2HSV);
		cvCvtColor(cImage, bgrImg, CV_RGB2BGR);
		cvCvtColor(bgrImg, cHsv, CV_BGR2HSV);

		bilinearBoundaryInterpolate(cHsv, bHsv, fHsv, rects,
				new HSVInterpolator());

		// Convert back to RGB scale
		cvCvtColor(cHsv, bgrImg, CV_HSV2BGR);
		cvCvtColor(bgrImg, cImage, CV_BGR2RGB);

	}

	public static void bilinearRGBInterpolation(IplImage cImage,
			IplImage bImage, IplImage fImage, ArrayList<CvRect> rects) {
		bilinearBoundaryInterpolate(cImage, bImage, fImage, rects,
				new RGBInterpolator());
	}

	public static void barycentricRGBInterpolation(IplImage cImage,
			IplImage bImage, IplImage fImage, FaceStreamElement fse) {
		
		
		if (fse.getPixelBlocks() != null) {
			barycentricBoundaryInterpolation(cImage, bImage, fImage, fse,
					new RGBInterpolator());		
		} else {
			ArrayList<CvRect> rects = fse.getRectangles().toCvRectList();
			barycentricBoundaryInterpolation(cImage, bImage, fImage, rects,
					new RGBInterpolator());
		}
		

	}
	
	public static void barycentricHSVInterpolation(IplImage cImage,
			IplImage bImage, IplImage fImage, ArrayList<CvRect> rects) {
		IplImage bgrImg = cvCreateImage(cvGetSize(cImage), 8, 3);
		IplImage fHsv = cvCreateImage(cvGetSize(cImage), 8, 3);
		IplImage bHsv = cvCreateImage(cvGetSize(cImage), 8, 3);
		IplImage cHsv = cvCreateImage(cvGetSize(cImage), 8, 3);

		// Convert images to HSV
		cvCvtColor(fImage, bgrImg, CV_RGB2BGR);
		cvCvtColor(bgrImg, fHsv, CV_BGR2HSV);
		cvCvtColor(bImage, bHsv, CV_RGB2HSV);
		cvCvtColor(bgrImg, bHsv, CV_BGR2HSV);
		cvCvtColor(cImage, bgrImg, CV_RGB2BGR);
		cvCvtColor(bgrImg, cHsv, CV_BGR2HSV);
		barycentricBoundaryInterpolation(cHsv, bHsv, fHsv, rects,
				new HSVInterpolator());	
		
		// Convert back to RGB scale
		cvCvtColor(cHsv, bgrImg, CV_HSV2BGR);
		cvCvtColor(bgrImg, cImage, CV_BGR2RGB);
	}
	
}
