package Project;

import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;

import java.util.ArrayList;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;

/**
 * Serializable list of SerializableRects.  Used at stream recombination to
 * retain position of original CvRects.
 * @author Marko Babic, Marcus Karpoff
 *
 */
public class SerializableRectList implements java.io.Serializable {
	/**
	 * Auto-generated UID.
	 */
	private static final long serialVersionUID = -7216084600440027767L;
		
	/** List of rectangles this instance aggregates. */
	private ArrayList<SerializableRect> _rects;
	
	/**
	 * Empty constructor.
	 */
	public SerializableRectList() {
		_rects = new ArrayList<SerializableRect>();
	}
	
	/**
	 * @param rects
	 * 		List of rects from which to initialize instance attribute _rects.
	 */
	public SerializableRectList(ArrayList<CvRect> rects) {
		_rects = new ArrayList<SerializableRect>();
		for (int i = 0; i < rects.size(); i++) {
			_rects.add(new SerializableRect(rects.get(i)));
		}		
	}
	
	/**
	 * @param seq
	 * 		CvSequence of rects from which to initailize instance attribute
	 * 		_rects.
	 */
	public SerializableRectList(CvSeq seq) {
		_rects = new ArrayList<SerializableRect>();
		for (int i = 0; i < seq.total(); i++) {
			CvRect cvr = new CvRect(cvGetSeqElem(seq, i));
			_rects.add(new SerializableRect(cvr));
		}	
	}
	
	/**
	 * @param r
	 * 		The rectangle to be added to the list of aggregated rectangles.
	 */
	public void add(SerializableRect r) {
		_rects.add(r);
	}

	/**
	 * @return
	 * 		List of rectangles this instance aggregates as list of CvRects.
	 */
	public ArrayList<CvRect> toCvRectList() {
		ArrayList<CvRect> ret = new ArrayList<CvRect>();
		for (int i = 0; i < _rects.size(); i++) {
			ret.add(_rects.get(i).toCvRect());
		}
		return ret;
	}

}
