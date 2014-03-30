package Project;

import java.util.ArrayList;

import com.googlecode.javacv.cpp.opencv_core.CvRect;

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
		
	private ArrayList<SerializableRect> _rects;
	
	public SerializableRectList() {
		_rects = new ArrayList<SerializableRect>();
	}
	
	public void add(SerializableRect r) {
		_rects.add(r);
	}

	public ArrayList<CvRect> toCvRectList() {
		ArrayList<CvRect> ret = new ArrayList<CvRect>();
		for (int i = 0; i < _rects.size(); i++) {
			ret.add(_rects.get(i).toCvRect());
		}
		return ret;
	}
	
}
