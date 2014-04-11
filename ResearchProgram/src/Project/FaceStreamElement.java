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

	private ArrayList<CvRect> _rects;
	private ArrayList<PixelBlock> _pixelBlocks;
	
	public FaceStreamElement(ArrayList<CvRect> rects, ArrayList<PixelBlock> pbs) {
		_rects = new ArrayList<CvRect>(rects);
		_pixelBlocks = new ArrayList<PixelBlock>(pbs);
	}
	
}
