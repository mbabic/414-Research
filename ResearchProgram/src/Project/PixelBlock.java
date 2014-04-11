package Project;

import java.util.ArrayList;

import org.libjpegturbo.turbojpeg.TJCompressor;
import org.libjpegturbo.turbojpeg.TJDecompressor;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_core.CvScalar;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class PixelBlock implements java.io.Serializable {

	/**
	 * Auto-generated serialization uid.
	 */
	private static final long serialVersionUID = -7335428971230485355L;
	
	/** 
	 * Byte information associated with pixels arranged in 1-dim array. 
	 * TODO: determine if needs to be declared transient
	 */
	private byte[] _flatBytes;
	/** Byte information associated with pixel arranged in 2-dim array. */
	private byte[] _blockBytes;
	/** 
	 * Pixels associated with this pixel block. 
	 * Declared transient such that this array list will not be serialized
	 * with the rest of the object.
	 */
	public transient ArrayList<CvScalar> _pixels;
	
	
	public PixelBlock() {
		_pixels = new ArrayList<CvScalar>();
	}
	
	public PixelBlock(ArrayList<CvScalar> pixels) {
		_pixels = new ArrayList<CvScalar>(pixels);
	}
	
	public PixelBlock(IplImage img, ArrayList<CvRect> rects) {
		
		CvScalar p;
		int x, y, height, width;
		for (CvRect rect: rects) {
			x = rect.x();
			y = rect.y();
			height = rect.height();
			width = rect.width();
			
			// We get pixel values in separate iterations and in order to keep
			// the byte data in a predicatble format at deserialization.
			// Get values of pixels along left-hand edge, top to bottom.
			for (int j = y; j < y + width; j ++) {
				_pixels.add(cvGet2D(img, j, x));				
			}
			
			// Get values of pixels along bottom edge, left to right.
			for (int i = x; i < x + width; i++) {
				_pixels.add(cvGet2D(img, y + height, i));
			}
			
			// Get values of pixels along right edge, bottom to top.
			for (int j = y + height - 1; j >= y; j--) {
				_pixels.add(cvGet2D(img, j, x + width));
			}
			
			// Get values of pixels along top edge, right to left.
			for (int i = x + width - 1; i >= x; i--) {
				_pixels.add(cvGet2D(img, y, i));
			}
				
			
		}
		
	}
	
	/**
	 * Flatten byte array to one-dimensional representation
	 */
	public void flatten() {
		
	}
	
	/**
	 * Create 2-dimensional out of pixel data.
	 */
	public void createBlock() {
		
	}
	
	/**
	 * Compress byte array using JPEG algorithm.
	 * @param compressor
	 */
	public void compress(TJCompressor compressor) {
		
	}
	
	/**
	 * Decompress byte array which was compressed using JPEG algorithm.
	 * @param decompressor
	 */
	public void decompress(TJDecompressor decompressor) {
		
	}
	
	/**
	 * Destroy reference to pixel array such that it is not serialized with
	 * rest of object.
	 */
	public void prepareForSerialization() {
		
	}
	
	public void pixelsToBytes() {
		
	}
}
