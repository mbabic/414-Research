package Project;

import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;

import java.util.ArrayList;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Serializable array of pixel boxes.
 * @author Marko Babic, Marcus Karpoff
 */
public class PixelBlockList implements java.io.Serializable {

	/**
	 * Auto-generated uid.
	 */
	private static final long serialVersionUID = 4473855717545417735L;

	private ArrayList<PixelBlock> _pixelBlocks;
	
	/**
	 * Empty constructor.
	 */
	public PixelBlockList() {
		_pixelBlocks = new ArrayList<PixelBlock>();
	}
	
	/**
	 * Given an image and an ArrayList of rectangles, construct a list of 
	 * PixelBlocks.
	 * @param img
	 * 		The source image.
	 * @param rects
	 * 		The list of rectangles from which the PixelBlock instances are
	 * 		to be constructed.
	 */
	public PixelBlockList(IplImage img, ArrayList<CvRect> rects) {
		_pixelBlocks = new ArrayList<PixelBlock>();
		for (int i = 0; i < rects.size(); i++) {
			_pixelBlocks.add(new PixelBlock(img, rects.get(i)));
		}
	}
	
	/**
	 * Given an image and a CvSeq of rectangles, construct a list of 
	 * PixelBlocks.
	 * @param img
	 * 		The source image.
	 * @param rects
	 * 		The sequence of rectangles from which the PixelBlokck instances are
	 * 		to be constructed.
	 */
	public PixelBlockList(IplImage img, CvSeq rects) {
		_pixelBlocks = new ArrayList<PixelBlock>();
		for (int i = 0; i < rects.total(); i++) {
			CvRect cvr = new CvRect(cvGetSeqElem(rects, i));
			_pixelBlocks.add(new PixelBlock(img, cvr));
		}
	}
	
	public PixelBlockList(ArrayList<PixelBlock> pbs) {
		_pixelBlocks = new ArrayList<PixelBlock>(pbs);
	}
	
	/**
	 * @param i
	 * 		The index of the element to be returned.
	 * @return
	 * 		The element at the given index.
	 */
	public PixelBlock get(int i) {
		return _pixelBlocks.get(i);
	}
	
	/**
	 * @param pb
	 * 		PixelBlock instance to be added to list.
	 */
	public void add(PixelBlock pb) {
		_pixelBlocks.add(pb);
	}
	
}
