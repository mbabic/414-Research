package Project;

import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;

import java.util.ArrayList;

import org.libjpegturbo.turbojpeg.TJCompressor;
import org.libjpegturbo.turbojpeg.TJDecompressor;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Serializable array of PixelBlocks.
 * @author Marko Babic, Marcus Karpoff
 */
public class PixelBlockList implements java.io.Serializable {

	/**
	 * Auto-generated uid.
	 */
	private static final long serialVersionUID = 4473855717545417735L;

	/** List of PixelBlock instances this class aggregates. */
	private ArrayList<PixelBlock> _pixelBlocks;
	/** Instance of TJCompressor used in compression operation. */
	private transient TJCompressor _compressor;
	/** Instance of TJDecompressor used in decompression operations. */
	private transient TJDecompressor _decompressor;
	
	/**
	 * Empty constructor.
	 */
	public PixelBlockList() {
		_pixelBlocks = new ArrayList<PixelBlock>();
	}
	
	/**
	 * Given an image and an ArrayList of rectangles, construct a list of 
	 * PixelBlocks.
	 * @deprecated
	 * @param img
	 * 		The source image.
	 * @param rects
	 * 		The list of rectangles from which the PixelBlock instances are
	 * 		to be constructed.
	 */
	public PixelBlockList(IplImage img, ArrayList<CvRect> rects) {
		_pixelBlocks = new ArrayList<PixelBlock>();
		
		try {
			_compressor = new TJCompressor();
			for (int i = 0; i < rects.size(); i++) {
				PixelBlock pb = new PixelBlock(img, rects.get(i));
				pb.compress(_compressor);
				_pixelBlocks.add(pb);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
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
		try {
			_compressor = new TJCompressor();
			for (int i = 0; i < rects.total(); i++) {
				CvRect cvr = new CvRect(cvGetSeqElem(rects, i));
				PixelBlock pb = new PixelBlock(img, cvr);
				pb.compress(_compressor);
				_pixelBlocks.add(pb);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
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
		PixelBlock pb = _pixelBlocks.get(i);
		initDecompressor();
		pb.decompress(_decompressor);
		return pb;
	}
	
	/**
	 * Initialize _decompressor -- to be used after deserialization from
	 * file.
	 */
	public void initDecompressor() {
		if (_decompressor == null) {
			try {
				_decompressor = new TJDecompressor();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	/**
	 * @param pb
	 * 		PixelBlock instance to be added to list.
	 */
	public void add(PixelBlock pb) {
		_pixelBlocks.add(pb);
	}
	
	/**
	 * @return
	 * 		The number of elements in the pixel block list.
	 */
	public int size() {
		return _pixelBlocks.size();
	}
	
}
