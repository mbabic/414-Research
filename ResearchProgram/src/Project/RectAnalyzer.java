package Project;

import java.util.ArrayList;

import com.googlecode.javacv.cpp.opencv_core.CvRect;

/**
 * Performs operations such as the retrival of bounding rectangles, intersection
 * checks, etc. on CvRects.
 * @author Marko Babic, Marcus Karpoff
 */
public class RectAnalyzer {

	/**
	 * A list of CvRects which bound the sets rects which have non-empty
	 * intersection in the original list.
	 *  
	 * @param rects
	 * 		The list of rects for which the list of bounding rects is to be
	 * 		determined.
	 * @return
	 * 		List of bounding rects.
	 */
	public static ArrayList<CvRect> getBoundingRects(
		ArrayList<CvRect> origRects) {
		CvRect r1, r2, boundingRect;
		ArrayList<CvRect> rects = new ArrayList<CvRect>(origRects);
		int i, j;
		for (i = 0; i < rects.size(); i++) {
			r1 = rects.get(i);				
			for (j = i+1; j < rects.size(); j++) {
				r2 = rects.get(j);
				if (intersect(r1, r2)) {
					boundingRect = getMinBoundingRect(r1, r2);
					rects.remove(j);
					rects.set(i, boundingRect);
					return getBoundingRects(rects);
				}
			}
		}
		// No rectangles intersect.
		return new ArrayList<CvRect>(rects);
	}
	
	/**
	 * Determine if the given rectangles intersect.
	 * @param r1
	 * 		The first rectangle
	 * @param r2
	 * 		The second rectangle.
	 * @return
	 * 		True if the two rectangles intersect, false otherwise.
	 */
	public static boolean intersect(CvRect r1, CvRect r2) {
		int x1 = r1.x(), y1 = r1.y(), w1 = r1.width(), h1 = r1.height();
		int x2 = r2.x(), y2 = r2.y(), w2 = r2.width(), h2 = r1.height();
		if (((x1 + w1) < x2) || ((x2 + w2) < x1) || ((y1 + h1) < y2) ||
				((y2 + h2) < y1) ) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the minimum bounding rectangle of the two given rectangles.
	 */
	public static CvRect getMinBoundingRect(CvRect r1, CvRect r2) {
		CvRect ret = new CvRect();
		int x1 = r1.x(), y1 = r1.y(), w1 = r1.width(), h1 = r1.height();
		int x2 = r2.x(), y2 = r2.y(), w2 = r2.width(), h2 = r2.height();
		
		// Get x() and width() of ret
		if (x1 < x2 && ((x1 + w1) < (x2 + w2))) {
			// r1 is to the left of r2 and the width of r1 does not contain
			// the width of r2
			ret.x(x1);
			ret.width(x2 - x1 + w2);
		} else if (x1 < x2) {
			// r1 is to the left of r2 and contains the width of r2
			ret.x(x1);
			ret.width(w1);
		} else if (x2 < x1 && ((x2 + w2) < (x1 + w1))) {
			// r2 is to the left of r1 and does not contain the width of r1
			ret.x(x2);
			ret.width(x1 - x2 + w1);
		} else {
			// r2 is to the left of r1 and contains the width of r1, or
			// the two rectangles have equally sized and positioned widths
			ret.x(x2);
			ret.width(w2);
		}
		
		// Get y() and height() of ret
		if ((y1 < y2) && ((y1 + h1) < (y2 + h2))) {
			// r1 is above r2 and the height of r1 does not contain the height
			// of r2
			ret.y(y1);
			ret.height(y2 - y1 + h2);
		} else if (y1 < y2) {
			// r1 is above r2 and the height of r1 contains the height of r2
			ret.y(y1);
			ret.height(h1);
		} else if ((y2 < y1) && ((y2 + h2) < (y1 + h1))) {
			// r2 is above r1 and the height of r2 does not contain the height of
			// r1
			ret.y(y2);
			ret.height(y1 - y2 + h1);
		} else {
			// r2 is above r1 and contains its height or the two rectangles 
			// have equally size and positioned heights
			ret.y(y2);
			ret.height(h2);
		}
		return ret;
	}
	
	
	/**
	 * @param r1
	 * 		The first rectangle.
	 * @param r2
	 * 		The second rectangle
	 * @return	
	 * Returns the min between topLeftDist and midpointDist.
	 */
	public static double dist(CvRect r1, CvRect r2) {
		return Math.min(midpointDist(r1, r2), topLeftDist(r1, r2));
	}	
	
	/**

	 * @param r1
	 * 		The first rectangle.
	 * @param r2
	 * 		The second rectangle
	 * @return	
	 * Returns the square of the distance between the top left corners of the given
	 * CvRects.
	 */
	public static double topLeftDist(CvRect r1, CvRect r2) {
		return 
		Math.pow(r1.x() - 
				 r2.x(), 2) +
		Math.pow(r1.y() - 
				 r2.y(), 2);
	}	
	
	/**
	 * @param r1
	 * 		The first rectangle.
	 * @param r2
	 * 		The second rectangle
	 * @return	
	 * 		The square of the distance between the centers of the given 
	 * 		rectangles.
	 */
	public static double midpointDist(CvRect r1, CvRect r2) {
		return 
		Math.pow(r1.x() + ((double) r1.width() 	/ 2f) - 
				 r2.x() - ((double) r2.width() 	/ 2f), 2) +
		Math.pow(r1.y() + ((double) r1.height() / 2f) - 
				 r2.x() - ((double) r2.height() / 2f), 2);
	}
	/**
	 * Determines if the point given by x, y coordinates provided lies inside
	 * the CvRect provided.
	 * @param x
	 * 			The x coordinate of the point whose intersection with the 
	 * 			rectangle is to be determined.
	 * @param y
	 * 			The y coordinate of the point whose intersection with the 
	 * 			rectangle is to be determined.
	 * @param rect
	 * 			The rectangle for which we wish to determine if the given point
	 * 			lies within.
	 */
	public static boolean isInsideRect(double x, double y, CvRect rect) {
		
		if ((rect.x() < x && x < rect.x() + rect.width()) &&
			(rect.y() < y && y < rect.y() + rect.height())) {
			return true;
		}
		return false;
	}
}
