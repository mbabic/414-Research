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
	 * IDEA: cvAbsDiff works by taking abs diff between two images ...
	 * so may it is that at border both images have same value, so write your
	 * own custom absDiff that says if (diff == 0) return value of whichever
	 * pixel.  May not work in after encoding/decoding, but might produce
	 * good results for now.
	 */
	public void recombineVideo(IplImage cImage, IplImage bImage, IplImage fImage,
			ArrayList<CvRect> rects) {
		cvAbsDiff(bImage, fImage, cImage);
//		ByteBuffer buf = cImage.getByteBuffer();
		for (CvRect cvr: rects) {
			int index, x = cvr.x(), y = cvr.y(), widthStep = cImage.widthStep(),
			nChannels = cImage.nChannels(), height = cvr.height(), 
			width = cvr.width();
			// Interpolate along lefthand edge of rect
			for (int j = y; j < y + height; j++) {
				// For now, just grab pixel to left
				// TODO: worry about when pixel is outside frame!
				index = (j * widthStep) + (x * nChannels);
				CvScalar b1 = cvGet2D(bImage, j, x-3);
				CvScalar b2 = cvGet2D(bImage, j, x-2);
				CvScalar f1 = cvGet2D(fImage, j, x+2);
				CvScalar f2 = cvGet2D(fImage, j, x+3);
				cvSet2D(cImage, j, x-1, b1);
				cvSet2D(cImage, j, x, b2);
				cvSet2D(cImage, j, x+1, f1);
//				cvSet2D(cImage, j, x+2, f2);
				System.out.println("-------------");
				System.out.println(cvGet2D(cImage, j, x));
				System.out.println(b1);
				System.out.println(f1);
//				buf.put(index, 		(byte) ((int) fPixel.val(0) & 0xFF));
//				buf.put(index + 1, 	(byte) ((int) fPixel.val(1) & 0xFF));
//				buf.put(index + 2, 	(byte) ((int) fPixel.val(2) & 0xFF));
			}
		}
		
		// Interpolate left hand edge.
		
		
		// Attempt to remove black lines
//		int rows = cImage.height(), cols = cImage.width();
//		ByteBuffer buf = cImage.getByteBuffer();
//		for (int i = 0; i < rows; i++) {
//			for (int j = 0; j < cols; j++) {
//				CvScalar cPixel = cvGet2D(cImage, i, j);
//				if (cPixel.val(0) == 0 && cPixel.val(1) == 0 && cPixel.val(2) == 0) {
////					CvScalar fPixel = cvGet2D(fImage, i, j);
//					int index = (i * cImage.widthStep()) + (cImage.nChannels() * j);
//					buf.put(index, (byte) 255);
//					buf.put(index + 1, (byte)255);
//					buf.put(index + 2, (byte)255);
//				}
//				
//			}
//		}
	}
	
}
