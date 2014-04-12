package Project;

import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/** 
 * A particular element in a FaceStream.  Aggregates an array of CvRects and
 * an associated array of PixelBlocks.
 * @author Marko Babic, Marcus Karpoff
 */
public class FaceStreamElement implements java.io.Serializable {

	/**
	 * Auto-generated uid.
	 */
	private static final long serialVersionUID = -6526438513032989593L;

	private SerializableRectList _rects;
	private PixelBlockList _pixelBlocks;
	
	public FaceStreamElement(IplImage img, CvSeq rects) {
		_rects = new SerializableRectList(rects);	
		if (Settings.SAVE_PIXELS == true) {
			_pixelBlocks = new PixelBlockList(img, rects);
		} else {
			_pixelBlocks = null;
		}

	}
	
	/**
	 * @deprecated
	 * @param pbl
	 * @param srl
	 */
	public FaceStreamElement(PixelBlockList pbl, SerializableRectList srl) {
		_rects = srl;
		_pixelBlocks = pbl;
	}
	
	public SerializableRectList getRectangles() {
		return _rects;
	}
	
	public PixelBlockList getPixelBlocks() {
		return _pixelBlocks;
	}
	
}
