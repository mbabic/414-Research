package Project;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2RGB;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_HSV2BGR;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2BGR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.googlecode.javacpp.Loader;
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
		ArrayList<CvRect> faceList = new ArrayList<CvRect>();
		ArrayList<CvRect> simplifiedFaceList;
//		return facesDetected;
		
		CvSeq faces = cvCreateSeq(
			0, 
			Loader.sizeof(CvSeq.class), 
			Loader.sizeof(CvRect.class), 
			storage
		);

		Multimap<CvRect, ObjectTracker> pairs = getFaceTrackerPairs(img, facesDetected);
		
		// For trackers for which no face was within _minDist of the tracker:
		// If the tracker has lost the object we destroy it.  
		// Else, we push the face returned by the tracker.
		Iterator<ObjectTracker> iter = pairs.get(null).iterator();
		while (iter.hasNext()) {
			ObjectTracker tracker = iter.next();
			if (tracker.hasLostObject()) {
				_trackers.remove(tracker);
			} else {
				faceList.add(tracker.track(img));
			}
		}
		
		for (int i = 0; i < _faces.size(); i++) {
			// Update object tracker pRect, but push face returned by
			// haar classifier.
			ObjectTracker tracker = pairs.get(_faces.get(i)).iterator().next();
			tracker._obj._pRect = new CvRect(
				_faces.get(i).x(),
				_faces.get(i).y(),
				_faces.get(i).width(),
				_faces.get(i).height()
			);
			faceList.add(_faces.get(i));
		}
		
		simplifiedFaceList = RectAnalyzer.getBoundingRects(faceList);
		
		for (int i = 0; i < simplifiedFaceList.size(); i++) {
			cvSeqPush(faces, simplifiedFaceList.get(i));
		}
		
		return faces;
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
	 * @param fs
	 * 			  Where the stream of FaceStreams will go
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
		hsvInterpolationRecombination(cImage, bImage, fImage, rects);
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
	
	/**
	 * Combine the given foreground image (fImage) and background image
	 * (bImage) into cImage by taking the absolute difference between the 
	 * images and interpolating RGB components linearly and separately along
	 * each line defined by the given rects.
	 * @param cImage
	 * @param bImage
	 * @param fImage
	 * @param rects
	 */
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
	
	/**
	 * Return an interpolated CvScalar in the RGB color space based on 
	 * parameters passed.
	 * @param v0
	 * 		The first CvScalar on the interpolation line.
	 * @param v1
	 * 		The second CvScalar on the interpolation line.
	 * @param t
	 * 		Value indicating position at line whose interpolated scalar
	 * 		values we want to calculate (0 <= t < 0. 5 => closer to v0,
	 * 		0.5 < t <= 1 => closer to v1)
	 * @return
	 * 		A CvScalar in the BGR color space with values set as per the
	 * 		result of the interpolation operation.
	 */
	private CvScalar interpolateRGB(CvScalar v0, CvScalar v1, double t) {
		double	r = Math.floor(v0.val(2) + (v1.val(2) - v0.val(2))*t),
				g = Math.floor(v0.val(1) + (v1.val(1) - v0.val(1))*t),
				b = Math.floor(v0.val(0) + (v1.val(0) - v0.val(0))*t);
		return new CvScalar(b, g, r, 1f); 
	}
	
	/**
	 * Returns an interpolated CvScalar in the HSV color space based on 
	 * parameters passed.
	 * @param v0
	 *  	The first CvScalar on the interpolation line.
	 * @param v1
	 *  	The second CvScalar on the interpolation line.
	 * @param t
	 * 		Value indicating position at line whose interpolated scalar
	 * 		values we want to calculate (0 <= t < 0. 5 => closer to v0,
	 * 		0.5 < t <= 1 => closer to v1)
	 * @return
	 * 		A CvScalar in the HSV color space with Hue, Saturation, and Value
	 * 		values set as per the result of the interpolation operation.
	 */
	private CvScalar interpolateHSV(CvScalar v0, CvScalar v1, double t) {
		return new CvScalar(
			Math.floor(v0.val(0) + (v1.val(0) - v0.val(0))*t),
			Math.floor(v0.val(1) + (v1.val(1) - v0.val(1))*t),
			Math.floor(v0.val(2) + (v1.val(2) - v0.val(2))*t),
			1f
		);
	}
	
	/**
	 * Combine the given foreground image (fImage) and background image
	 * (bImage) into cImage by taking the absolute difference between the 
	 * images, transform the image to the HSV color space, interpolating
	 * along the lines defines by the rectangles in rects, and re-converting
	 * to the RGB color space.
	 * @param cImage
	 * @param bImage
	 * @param fImage
	 * @param rects
	 */
	private void hsvInterpolationRecombination(
			IplImage cImage, IplImage bImage, IplImage fImage, 
			ArrayList<CvRect> rects) {
			
			IplImage bgrImg = cvCreateImage(cvGetSize(cImage), 8, 3);
			IplImage fHsv = cvCreateImage(cvGetSize(cImage), 8, 3);
			IplImage bHsv = cvCreateImage(cvGetSize(cImage), 8, 3);
			IplImage cHsv = cvCreateImage(cvGetSize(cImage), 8, 3);
		
			CvScalar b, f;
			int x, y, width, height;
			int imgWidth = cImage.width();
			int imgHeight = cImage.height();	
			
			// Take absolute difference.
			cvAbsDiff(bImage, fImage, cImage);
			
			// Convert image to BGR, then to HSV
			cvCvtColor(fImage, bgrImg, CV_RGB2BGR);
			cvCvtColor(bgrImg, fHsv, CV_BGR2HSV);
			cvCvtColor(bImage, bgrImg, CV_RGB2BGR);
			cvCvtColor(bgrImg, bHsv, CV_BGR2HSV);
			cvCvtColor(cImage, bgrImg, CV_RGB2BGR);
			cvCvtColor(bgrImg, cHsv, CV_BGR2HSV);

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
						b = cvGet2D(bHsv, j, x-2);
						f = cvGet2D(fHsv, j, x+2);				

						if (!(b.val(0) == 0)) {
							cvSet2D(cHsv, j, x-1, interpolateHSV(b, f, 0.25));
							cvSet2D(cHsv, j, x, interpolateHSV(b, f, 0.5));
						}
						if (!(f.val(0) == 0)) {
							cvSet2D(cHsv, j, x+1, interpolateHSV(b, f, 0.75));
						}
						
						// Interpolate along right hand edge.
						b = cvGet2D(bHsv, j, x + width + 2);
						f = cvGet2D(fHsv, j, x + width - 2);

						if (!(b.val(0) == 0)) {
							cvSet2D(cHsv, j, x + width, interpolateHSV(b, f, 0.5));
							cvSet2D(cHsv, j, x + width + 1, interpolateHSV(b, f, 0.25));
						}
						if (!(f.val(0) == 0)) {
							cvSet2D(cHsv, j, x + width - 1, interpolateHSV(b, f, 0.75));
						}
					}
				}	
			
				// Interpolate along top and bottom edges.
				for (int i = x; i < x + width; i++) {
					// Handle boundary conditions
					if ((0 <= i && i <= imgWidth) && (2 <= y && y <= imgHeight - 2)) {
						// Interpolate along top edge.
						b = cvGet2D(bHsv, y - 2, i);
						f = cvGet2D(fHsv, y + 2, i);
						
						if (!(b.val(0) == 0)) {
							cvSet2D(cHsv, y - 1, i, interpolateHSV(b, f, 0.25));
							cvSet2D(cHsv, y, i, interpolateHSV(b, f, 0.5));
						}
						if (!(f.val(0) == 0)) {
							cvSet2D(cHsv, y + 1, i, interpolateHSV(b, f, 0.75));
						}	
						
						// Interpolate along bottom edge.
						b = cvGet2D(bHsv, y + height + 2, i);
						f = cvGet2D(fHsv, y + height - 2, i);
						if (!(b.val(0) == 0)) {
							cvSet2D(cHsv, y + height + 1, i, interpolateHSV(b, f, 0.25));
							cvSet2D(cHsv, y + height, i, interpolateHSV(b, f, 0.5));
						}
						if (!(f.val(0) == 0)) {
							cvSet2D(cHsv, y + height - 1, i, interpolateHSV(b, f, 0.75));
						}
					}
				}
			}
		// Convert back to RGB scale
		cvCvtColor(cHsv, bgrImg, CV_HSV2BGR);
		cvCvtColor(bgrImg, cImage, CV_BGR2RGB);
	}
}
