package Project;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.cvAbsDiff;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.googlecode.javacpp.Loader;
import static com.googlecode.javacv.cpp.opencv_core.*;
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

	// TODO: use better mechanism than flag.
	// Probably it will be the case that each instance of objectTracker will
	// have its own flag.
	public static int flag = 0;
	
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
	 * 
	 * @param face
	 * @return
	 */
	private int findMinDistTrackerIndex(CvRect face) {
		
		int minIndex = -1;
		float min = Float.MAX_VALUE;
		for (int i = 0; i < _trackers.size(); i ++) {
		}
		
		return -1;
	}
	
	private ArrayList<CvRect> cvSeqToList(CvSeq seq) {
		ArrayList<CvRect> rects = new ArrayList<CvRect>();
		for (int i = 0; i < seq.total(); i++) {
			CvRect cvr = new CvRect(cvGetSeqElem(seq, 0));
			rects.add(cvr);
		}
		return rects;
	}
	
	/**
	 * 
	 * @param trackers
	 * @param rect
	 * @return
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
	
	private Multimap<CvRect, ObjectTracker> getFaceTrackerPairs(IplImage img, CvSeq facesDetected) {
		Multimap<CvRect, ObjectTracker> pairs = HashMultimap.create();
		_faces = cvSeqToList(facesDetected);
		ArrayList<ObjectTracker> trackers = new ArrayList<ObjectTracker>();
		ObjectTracker nearestTracker = null;
		CvRect rect = null, nearestRect = null;
		
		for (ObjectTracker ot: _trackers) trackers.add(ot);
		System.out.println("Rects: " + _faces);
		System.out.println("Trackers: " + trackers);
		
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
		CvSeq faces = cvCreateSeq(
			0, 
			Loader.sizeof(CvSeq.class), 
			Loader.sizeof(CvRect.class), 
			storage
		);

		Multimap<CvRect, ObjectTracker> pairs = getFaceTrackerPairs(img, facesDetected);
		System.out.println(pairs.get(null));
		System.out.println(_trackers);

		Iterator<ObjectTracker> iter = pairs.get(null).iterator();
		while (iter.hasNext()) {
			ObjectTracker tracker = iter.next();
			if (tracker.hasLostObject()) {
				_trackers.remove(tracker);
			} else {
				cvSeqPush(faces, tracker.track(img));
			}
		}
		
		for (int i = 0; i < _faces.size(); i++) {
			ObjectTracker tracker = pairs.get(_faces.get(i)).iterator().next();
			tracker._obj._pRect = new CvRect(
				_faces.get(i).x(),
				_faces.get(i).y(),
				_faces.get(i).width(),
				_faces.get(i).height()
			);
			cvSeqPush(faces, tracker.track(img));
		}
		
//		if ((rects.total() > 0) && (flag == 0)) {
//			CvRect cvr = new CvRect(cvGetSeqElem(rects, 0));
//			_tracker.trackNewObject(input, cvr);
//			System.out.println("setting flag.  cvr = " + cvr);
//			flag = 1;
//		} else if (flag == 1) {
//			CvRect cvr = new CvRect(cvGetSeqElem(rects, 0));
//			System.out.println(cvr);
//			if (cvr.isNull() != true) {
//				_tracker._obj._pRect = cvr;
//			}
//			CvRect newCvr = _tracker.track(input);
//			if (newCvr.width() == 0) {
//				// Tracker has lost object, reset flag.
//				flag = 0;
//			}
//			CvSeq newRects = cvCreateSeq(0, Loader.sizeof(CvSeq.class), Loader.sizeof(CvRect.class), storage);
//			cvSeqPush(newRects, newCvr);
//			return newRects;
//		}
//		cvClearMemStorage(storage);
//		return rects;
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
	 */
	public void separateStreams(IplImage orig, IplImage back, IplImage face) {
		blackOutFaces(back, getFaces(orig));
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
					cvPoint(r.x(), r.y() - (int) (r.height() * .25)),
					cvPoint(r.width() + r.x(), r.height() + r.y()),
					CvScalar.BLACK, CV_FILLED, CV_AA, 0);
		}
	}

	/**
	 * This function takes in the two split video streams and recombines them in
	 * to one video stream.
	 */
	public void recombineVideo(IplImage cImage, IplImage bImage, IplImage fImage) {
		cvAbsDiff(bImage, fImage, cImage);
	}
	
	/**
	 * TODO: better name
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean isWithinThreshold(CvRect a, CvRect b) {
		return _minDist < Math.sqrt(
			(a.x() - b.x())^2 + (a.y() - b.y())
		);
	}


}
