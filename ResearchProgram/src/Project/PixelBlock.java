package Project;

import java.util.ArrayList;

import org.libjpegturbo.turbojpeg.TJ;
import org.libjpegturbo.turbojpeg.TJCompressor;
import org.libjpegturbo.turbojpeg.TJDecompressor;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_core.CvScalar;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Assumes RGB pixel format.
 * @author Marko Babic, Marcus Karpoff
 *
 */
public class PixelBlock implements java.io.Serializable {

	/**
	 * Auto-generated serialization uid.
	 */
	private static final long serialVersionUID = -7335428971230485355L;
	
	private static final int PIXEL_CHANNELS = 3;
	
	
	/** 
	 * Byte information associated with pixels arranged in 1-dim array. 
	 */
	public transient byte[] _bytes;
	
	/**
	 * The compressed bytes asociated with the 
	 */
	public byte[] _compressed;
	
	public transient byte[] _decompressed;
	
	/** Byte information associated with pixel arranged in 2-dim array. */
	private int _compressedSize;
	/** 
	 * Pixels associated with this pixel block. 
	 * Declared transient such that this array list will not be serialized
	 * with the rest of the object.
	 */
	public transient ArrayList<CvScalar> _pixels;
	
	/** 
	 * Width of rectangle which defines area of image from which instance
	 * retrieves pixel values.
	 */	
	private int _width;
	/** 
	 * Height of rectangle which defines area of image from which instance
	 * retrieves pixel values.
	 */
	private int _height;
	
	/**
	 * Empty constructor.
	 */
	public PixelBlock() {
		_pixels = new ArrayList<CvScalar>();
	}
	
	/**
	 * @param img
	 * 		The image from which we wish to extract pixels.
	 * @param rect
	 * 		The rectangle defining the lines of pixels which we wish to
	 * 		extract.
	 */
	public PixelBlock(IplImage img, CvRect rect) {
		int x, y, height, width;
		_pixels = new ArrayList<CvScalar>();
		x = rect.x();
		y = rect.y();
		height = rect.height();
		width = rect.width();
		_width = width;
		_height = height;

		// We get pixel values in separate iterations and in order to keep
		// the byte data in a predicatble format at deserialization.
		// Get values of pixels along left-hand edge, top to bottom.
		for (int j = y; j < y + width; j++) {
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
		
		createByteBuffer();
	}
	
	/**
	 * From instance _pixels value, populate _bytes array with appropriate
	 * byte values.
	 */
	private void createByteBuffer() {
		CvScalar p;
		_bytes = new byte[_pixels.size() * PIXEL_CHANNELS * 2];
		for (int i = 0; i < _pixels.size() * PIXEL_CHANNELS; i += PIXEL_CHANNELS) {
			p = _pixels.get(i / PIXEL_CHANNELS);
			for (int j = 0; j < PIXEL_CHANNELS; j++) {
				_bytes[i + j] = (byte) ((int)p.val(0) & 0xFF);
			}
		}
		p = null;
	}
	
	/**
	 * Flatten byte array to one-dimensional representation
	 */
	public void flatten() {
		
	}
	
	/**
	 * Compress byte array using JPEG algorithm.
	 * TODO: clear _bytes from memory.
	 * @param compressor
	 * 		The instance of TJCompressor used to compress the pixels.
	 */
	public void compress(TJCompressor compressor) {
		try {
			System.out.println(2 * (_width + _height) * PIXEL_CHANNELS);
			compressor.setSourceImage(
					_bytes,								// src buf
				0,										// x offset
				0,										// y offset
				_width,									// width
				0,										// pitch
				(2 * (_width + _height)) / _width,		// height
				TJ.PF_BGR
			);
			compressor.setSubsamp(TJ.SAMP_420);
			compressor.setJPEGQuality(100);
			_compressed = compressor.compress(0);
			_compressedSize = compressor.getCompressedSize();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Decompress byte array which was compressed using JPEG algorithm.
	 * @param decompressor
	 */
	public void decompress(TJDecompressor decompressor) {
		try {
			_decompressed = new byte[2 * (_height + _width) * PIXEL_CHANNELS];
			decompressor.setJPEGImage(_compressed, _compressedSize);
			decompressor.decompress(
				_decompressed, 
				0,
				0, 
				_width, 
				0,
				(2 * (_height + _width)) / _width, 
				TJ.PF_BGR, 
				0
			);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}	
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
