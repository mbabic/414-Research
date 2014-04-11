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
	 * TODO: determine if needs to be declared transient
	 */
	public byte[] _flatBytes;
	public byte[] _compressed;
	public byte[] _decompressed;
	/** Byte information associated with pixel arranged in 2-dim array. */
	private byte[] _blockBytes;
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
	 * 
	 */
	public PixelBlock() {
		_pixels = new ArrayList<CvScalar>();
	}
	
	/**
	 * 
	 * @param pixels
	 */
	public PixelBlock(ArrayList<CvScalar> pixels) {
		_pixels = new ArrayList<CvScalar>(pixels);
	}
	
	/**
	 * 
	 * @param img
	 * @param rect
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
	 * 
	 */
	private void createByteBuffer() {
		CvScalar p;
		_flatBytes = new byte[2 * (_width + _height) * PIXEL_CHANNELS * 2];
		for (int i = 0; i < 2 * (_width + _height) * PIXEL_CHANNELS; i += PIXEL_CHANNELS) {
			p = _pixels.get(i / PIXEL_CHANNELS);
			for (int j = 0; j < PIXEL_CHANNELS; j++) {
				_flatBytes[i + j] = (byte) ((int)p.val(0) & 0xFF);
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
		try {
			System.out.println(2 * (_width + _height) * PIXEL_CHANNELS);
			compressor.setSourceImage(
				_flatBytes,								// src buf
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Decompress byte array which was compressed using JPEG algorithm.
	 * @param decompressor
	 */
	public void decompress(TJDecompressor decompressor) {
	try{
		decompressor.setJPEGImage(_compressed, _compressedSize);
		_decompressed = decompressor.decompressToYUV(0);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
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
