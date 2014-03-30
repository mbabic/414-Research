package Project;

import com.googlecode.javacv.cpp.opencv_core.CvRect;

/**
 * Serializable CvRect.  Used at stream recombination to retain original CvRect
 * position.
 * @author Marko Babic, Marcus Karpoff
 *
 */
public class SerializableRect implements java.io.Serializable {

	/**
	 * Auto-generated UID.
	 */
	private static final long serialVersionUID = -5983403586376286128L;
	private int _x, _y, _width, _height;
	
	/**
	 * Constructor.  Given a CvRect, construct an instance of SerializableRect
	 * with the same x, y, width and height values.
	 * @param cvr
	 * 		The CvRect to be converted.
	 */
	public SerializableRect(CvRect cvr) {
		_x = cvr.x();
		_y = cvr.y();
		_width = cvr.width();
		_height = cvr.height();
	}
	
	/**
	 * Convert the given instance of SerializableRect to a CvRect.
	 * @return
	 * 		CvRect with x, y, width, and height values set according to values
	 * 		if instance attributes.
	 */
	public CvRect toCvRect() {
		return new CvRect(_x, _y, _width, _height);
	}
	
	
}
