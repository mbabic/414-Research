package Project;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_core.CV_FILLED;
import static com.googlecode.javacv.cpp.opencv_core.cvAbsDiff;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

/**
 * Does the facial recognition
 * 
 * @author Marko Babic, Marcus Karpoff
 * 
 */
public class Analyzer {
	/** 
	 * Haar classifier cascade used in face detection algorithm.
	 */
	private CvHaarClassifierCascade faceCascade;
	/**
	 * Storage used in construction of CvSeq returning by face detector.
	 */
	private CvMemStorage storage;
	/**
	 * ArrayList of active object trackers.
	 */
	private List<ObjectTracker> _trackers;
	/**
	 * ArrayList of CvRects used in pairing.
	 */
	private ArrayList<CvRect> _faces;
	
	/**
	 * Threshold distance used in the determination if a face has been
	 * seen before or if it is new in the frame.
	 */
	private int _minDist = 25*25;

	/**
	 * Constructor for the analyzer.
	 * 
	 * @throws ClassiferLoadFailure
	 */
	Analyzer() throws ClassiferLoadFailure {
		String classifierDir = Settings.CLASSIFIER_DIR
				+ "haarcascade_frontalface_default.xml";
		faceCascade = new CvHaarClassifierCascade(cvLoad(classifierDir));
		storage = CvMemStorage.create();
		if (faceCascade.isNull()) {
			throw new ClassiferLoadFailure(classifierDir);
		}
		_trackers = new ArrayList<ObjectTracker>();
	}
	
	/**
	 * This will look for faces and return the coordinates of the faces
	 * 
	 * @param inputMat
	 *            The original unedited image
	 * @return The location of all the faces
	 */
	private CvSeq detectFaces(IplImage img) {
		CvSeq rects = cvHaarDetectObjects(img, faceCascade, storage, 1.5, 3,
				CV_HAAR_DO_CANNY_PRUNING);
		
		return rects;		
	}
	
	
	/**
	 * Converts the given CvSeq of CvRects to a list of CvRects.
	 * @param seq
	 * 		The CvSeq of CvRects to be converted to a list.
	 * @return
	 * 		The converted ArrayList of CvRects.
	 */
	private ArrayList<CvRect> cvSeqToList(CvSeq seq) {
		ArrayList<CvRect> rects = new ArrayList<CvRect>();
		for (int i = 0; i < seq.total(); i++) {
			CvRect cvr = new CvRect(cvGetSeqElem(seq, i));
			rects.add(cvr);
		}
		return rects;
	}
	
	/**
	 * Get ObjectTracker instance whose _obj._pRect is closest to the given
	 * rectangle.
	 * @param trackers
	 * 		The list of trackers.
	 * @param rect
	 * 		The CvRect for which we want to determine which tracker is closest.
	 * @return
	 * 		The closest tracker as per the criterion above.  Null if no 
	 * 		tracker is within _minDist of the given rect.
	 */
	public ObjectTracker getNearestTracker(List<ObjectTracker> trackers, CvRect rect) {
		ObjectTracker nearestNeighbour = null;
		int min = Integer.MAX_VALUE, dist = 0;
		for (int i = 0; i < trackers.size(); i++) {
			dist =  (int) Math.pow((trackers.get(i)._obj._pRect.x() - rect.x()), 2) + 
					(int) Math.pow((trackers.get(i)._obj._pRect.y() - rect.y()), 2);
			if ((dist < min) && (dist <= _minDist)) {
				min = dist;
				nearestNeighbour = trackers.get(i);
			}
		}
		return nearestNeighbour;
	}
	
	/**
	 * Determine which rectangle is the nearest to the _pRect of the object
	 * which the given tracker is tracking.
	 * @param rects
	 * 		List of CvRects.
	 * @param tracker
	 * 		The tracker for which we want to determine which rect is closest.
	 * @return
	 * 		The CvRect amongst rects which is closest to tracker.  Null if no
	 * 		rect is within _minDist of the tracker's _obj._pRect.
	 */
	public CvRect getNearestRect(List<CvRect> rects, ObjectTracker tracker) {
		CvRect nearestNeighbour = null;
		int min = Integer.MAX_VALUE, dist = 0;
		for (int i = 0; i < rects.size(); i++) {
			dist =  (int) Math.pow((rects.get(i).x() - tracker._obj._pRect.x()), 2) + 
					(int) Math.pow((rects.get(i).y() - tracker._obj._pRect.y()), 2);
			if ((dist < min) && (dist <= _minDist)) {
				min = dist;
				nearestNeighbour = rects.get(i);
			}
		}
		return nearestNeighbour;
	}
	
	/**
	 * Set up new tracker pairs.
	 * @param img 
	 * 		The frame in which the faces to be tracked appears.
	 * @param facesDetected
	 *  	The CvSeq of CvRects returned by haar cascade classifier.
	 * @return
	 *  	Associative map such that:
	 *  		keys 	-> CvRects in which a face appears
	 *  		values 	-> An instance of object tracker which is tracking
	 *   				   the given face.
	 *   	The null key maps to a Collection of all trackers for which there
	 *      was no face within _minDist of the last known position of the face
	 *      that tracker was tracking.
	 */
	private Multimap<CvRect, ObjectTracker> getFaceTrackerPairs(IplImage img, CvSeq facesDetected) {
		// Initialize ret value.
		Multimap<CvRect, ObjectTracker> pairs = HashMultimap.create();
		
		// Transform CvSeq into ArrayList as the latter is easier to work with.
		_faces = cvSeqToList(facesDetected);
		
		// Temporary list of trackers used to ensure no tracker is considered
		// a second time after having been matched in the process below.
		ArrayList<ObjectTracker> trackers = new ArrayList<ObjectTracker>();
		
		ObjectTracker nearestTracker = null;
		CvRect rect = null, nearestRect = null;
		
		for (ObjectTracker ot: _trackers) trackers.add(ot);
		
		for (int i = 0; i < _faces.size(); i++) {
			rect = _faces.get(i);
			nearestTracker = getNearestTracker(trackers, rect);
			if (nearestTracker == null) {
				nearestTracker = new ObjectTracker();
				nearestTracker.trackNewObject(img, rect);
				nearestTracker._obj._pRect = 
					new CvRect(rect.x(), rect.y(), rect.width(), rect.height());
				pairs.put(rect, nearestTracker);
				_trackers.add(nearestTracker);		
				continue;
			} 
						
			nearestRect = getNearestRect(_faces, nearestTracker);
			
			if (nearestRect.equals(rect)) {
				pairs.put(rect, nearestTracker);
				trackers.remove(nearestTracker);
			} else {
				nearestTracker = new ObjectTracker();
				nearestTracker.trackNewObject(img, rect);
				nearestTracker._obj._pRect = 
					new CvRect(rect.x(), rect.y(), rect.width(), rect.height());
				pairs.put(rect, nearestTracker);
				_trackers.add(nearestTracker);	
			}
		}
		
		// For all unmatched trackers, pair them with key == null
		for (int i = 0; i < trackers.size(); i++) {
			pairs.put(null, trackers.get(i));
		}
		return pairs;
	}
	
	private CvSeq getFaces(IplImage img) {
		CvSeq facesDetected = detectFaces(img);
		return facesDetected;
		
// OBJECT TRACKING DISABLED DURING DEVELOPMENT OF RECOMBINATOR METHODS FOR
// SIMPLICITY OF IMPLEMENTATION
// IT IS LIKELY THE CASE THAT THE RECTANGLES RETURNED BY THIS PROCEDURE WILL
// HAVE TO BE SIMPLIFIED IN SOME WAY
		
//		CvSeq faces = cvCreateSeq(
//			0, 
//			Loader.sizeof(CvSeq.class), 
//			Loader.sizeof(CvRect.class), 
//			storage
//		);
//
//		Multimap<CvRect, ObjectTracker> pairs = getFaceTrackerPairs(img, facesDetected);
//		
//		// For trackers for which no face was within _minDist of the tracker:
//		// If the tracker has lost the object we destroy it.  
//		// Else, we push the face returned by the tracker.
//		Iterator<ObjectTracker> iter = pairs.get(null).iterator();
//		while (iter.hasNext()) {
//			ObjectTracker tracker = iter.next();
//			if (tracker.hasLostObject()) {
//				_trackers.remove(tracker);
//			} else {
//				cvSeqPush(faces, tracker.track(img));
//			}
//		}
//		
//		for (int i = 0; i < _faces.size(); i++) {
//			ObjectTracker tracker = pairs.get(_faces.get(i)).iterator().next();
//			tracker._obj._pRect = new CvRect(
//				_faces.get(i).x(),
//				_faces.get(i).y(),
//				_faces.get(i).width(),
//				_faces.get(i).height()
//			);
//			// Update object tracker pRect, but push face returned by
//			// haar classifier.
//			cvSeqPush(faces, _faces.get(i));
//		}
//		return faces;
	}

	/**
	 * * This will return the split video streams. It will take the original
	 * video stream and the location of the faces as input. It will return two
	 * video streams as a tuple. One will have the faces only the other will
	 * have everything else.
	 * 
	 * @param orig
	 *            Original IplImage
	 * @param back
	 *            Where the background images will go
	 * @param face
	 *            Where the facial image will go
	 */
	public void separateStreams(IplImage orig, IplImage back, IplImage face,
			FaceStream fs) {
		CvSeq faces = getFaces(orig);
		fs.add(new SerializableRectList(faces));
		blackOutFaces(back, faces);
		cvAbsDiff(orig, back, face);
	}

	/**
	 * This function will write onto of the frames that have facial data.
	 * 
	 * @param inputMat
	 *            The Mat of that is going to be blacked out
	 * @param rect
	 *            The rectangle that will be drawn
	 */
	private void blackOutFaces(IplImage input, CvSeq rects) {
		int total_Faces = rects.total();
		for (int i = 0; i < total_Faces; i++) {
			CvRect r = new CvRect(cvGetSeqElem(rects, i));
			cvRectangle(input,
					cvPoint(r.x(), r.y()),
					cvPoint(r.width() + r.x(), r.height() + r.y()),
					CvScalar.BLACK, CV_FILLED, CV_AA, 0);
		}
	}

	/**
	 * This function takes in the two split video streams and recombines them in
	 * to one video stream.
	 */
	public void recombineVideo(IplImage cImage, IplImage bImage, IplImage fImage,
			ArrayList<CvRect> rects) {
		// TODO: change which recombinator called based on param set ... somewhere
		linearInterpolationRecombination(cImage, bImage, fImage, rects);
	}
	
	/**
	 * Recombine the foreground image (fImage) with the background image 
	 * (bImage) storing the result in cImage in a naive way:
	 * Replace pixels falling along the rectangles in rects with the values
	 * of its neighbouring pixels (without interpolation, simple value
	 * replacement)
	 * @param cImage
	 * @param bImage
	 * @param fImage
	 * @param rects
	 */
	public void naiveRecombination(IplImage cImage, IplImage bImage, IplImage fImage,
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
			
			// Do pixel replacement along left and right hand edges.
			for (int j = y; j < y + height; j++) {
				// Handle boundary conditions
				if ((0 <= j && j <= imgHeight) && (2 <= x && x <= imgWidth - 2)) {
					// Replace along left hand edge.
					b = cvGet2D(bImage, j, x-2);
					f = cvGet2D(fImage, j, x+2);				

					if (!(b.val(0) == 0 && b.val(1) == 0 && b.val(2) == 0)) {
						cvSet2D(cImage, j, x-1, b);
						cvSet2D(cImage, j, x, b);
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, j, x+1, f);
					}
					
					// Replace along right hand edge.
					b = cvGet2D(bImage, j, x + width + 2);
					f = cvGet2D(fImage, j, x + width - 2);

					if (!(b.val(0) == 0 && b.val(1) == 0 && b.val(2) == 0)) {
						cvSet2D(cImage, j, x + width, b);
						cvSet2D(cImage, j, x + width + 1, b);
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, j, x + width - 1, f);
					}
				}
			}
			
			// Do pixel replacement along top and bottom edges.
			for (int i = x; i < x + width; i++) {
				// Replace along top edge.
				if ((0 <= i && i <= imgWidth) && (2 <= y && y <= imgHeight - 2)) {
					b = cvGet2D(bImage, y - 2, i);
					f = cvGet2D(fImage, y + 2, i);
					
					if (!(b.val(0) == 0 && b.val(1) == 0 && b.val(2) == 0)) {
						cvSet2D(cImage, y - 1, i, b);
						cvSet2D(cImage, y, i, b);
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, y + 1, i, f);
					}	
					
					// Replace along bottom edge.
					b = cvGet2D(bImage, y + height + 2, i);
					f = cvGet2D(fImage, y + height - 2, i);
					if (!(b.val(0) == 0 && b.val(1) == 0 && b.val(2) == 0)) {
						cvSet2D(cImage, y + height + 1, i, b);
						cvSet2D(cImage, y + height, i, b);
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, y + height - 1, i, f);
					}
				}
			}
		}			
	}
	
	private void linearInterpolationRecombination(
		IplImage cImage, IplImage bImage, IplImage fImage, 
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
						cvSet2D(cImage, j, x-1, interpolate(b, f, 0.25));
						cvSet2D(cImage, j, x, interpolate(b, f, 0.5));
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, j, x+1, interpolate(b, f, 0.75));
					}
					
					// Interpolate along right hand edge.
					b = cvGet2D(bImage, j, x + width + 2);
					f = cvGet2D(fImage, j, x + width - 2);

					if (!(b.val(0) == 0 && b.val(1) == 0 && b.val(2) == 0)) {
						cvSet2D(cImage, j, x + width, interpolate(b, f, 0.5));
						cvSet2D(cImage, j, x + width + 1, interpolate(b, f, 0.25));
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, j, x + width - 1, interpolate(b, f, 0.75));
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
						cvSet2D(cImage, y - 1, i, interpolate(b, f, 0.25));
						cvSet2D(cImage, y, i, interpolate(b, f, 0.5));
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, y + 1, i, interpolate(b, f, 0.75));
					}	
					
					// Interpolate along bottom edge.
					b = cvGet2D(bImage, y + height + 2, i);
					f = cvGet2D(fImage, y + height - 2, i);
					if (!(b.val(0) == 0 && b.val(1) == 0 && b.val(2) == 0)) {
						cvSet2D(cImage, y + height + 1, i, interpolate(b, f, 0.25));
						cvSet2D(cImage, y + height, i, interpolate(b, f, 0.5));
					}
					if (!(f.val(0) == 0 && f.val(1) == 0 && f.val(2) == 0)) {
						cvSet2D(cImage, y + height - 1, i, interpolate(b, f, 0.75));
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param l
	 * @param y
	 * @param t
	 * @return
	 */
	private CvScalar interpolate(CvScalar v0, CvScalar v1, double t) {
		double	r = Math.floor(v0.val(2) + (v1.val(2) - v0.val(2))*t),
				g = Math.floor(v0.val(1) + (v1.val(1) - v0.val(1))*t),
				b = Math.floor(v0.val(0) + (v1.val(0) - v0.val(0))*t);
		return new CvScalar(b, g, r, 1f); 
	}
}
