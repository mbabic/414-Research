package Project;

import java.util.ArrayList;

import com.googlecode.javacv.cpp.opencv_core.CvRect;

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
	
	public FaceStreamElement(PixelBlockList pbl, SerializableRectList srl) {
		_rects = srl;
		_pixelBlocks = pbl;
	}
	
}
