package Project;

import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;

import java.util.ArrayList;

import org.libjpegturbo.turbojpeg.TJ;
import org.libjpegturbo.turbojpeg.TJCompressor;
import org.libjpegturbo.turbojpeg.TJDecompressor;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Serializable data structure used to save pixel information on boundary of
 * rectangles defining face areas. Assumes RGB pixel format.
 * 
 * @author Marko Babic, Marcus Karpoff
 * 
 */
public class PixelBlock implements java.io.Serializable {

	/**
	 * Auto-generated serialization uid.
	 */
	private static final long serialVersionUID = -7335428971230485355L;

	/**
	 * Pixel depth. Assumes RGB.
	 */
	private static final int PIXEL_CHANNELS = 3;

	/**
	 * Byte information associated with pixels arranged in 1-dim array.
	 */
	public transient byte[] _bytes;

	/**
	 * The compressed bytes associated with the pixels.
	 */
	public byte[] _compressed;

	/**
	 * Decompressed bytes associated with compressed iamge data.
	 */
	public transient byte[] _decompressed;

	/** Byte information associated with pixel arranged in 2-dim array. */
	private int _compressedSize;
	/**
	 * Pixels associated with this pixel block. Declared transient such that
	 * this array list will not be serialized with the rest of the object.
	 */
	public transient ArrayList<CvScalar> _pixels;

	/**
	 * Width of rectangle which defines area of image from which instance
	 * retrieves pixel values.
	 */
	public int _width;
	/**
	 * Height of rectangle which defines area of image from which instance
	 * retrieves pixel values.
	 */
	public int _height;

	/**
	 * Empty constructor.
	 */
	public PixelBlock() {
		_pixels = new ArrayList<CvScalar>();
	}

	/**
	 * @param img
	 *            The image from which we wish to extract pixels.
	 * @param rect
	 *            The rectangle defining the lines of pixels which we wish to
	 *            extract.
	 */
	public PixelBlock(IplImage img, CvRect rect) {
		int x, y, height, width, imgHeight, imgWidth;
		_pixels = new ArrayList<CvScalar>();
		x = rect.x();
		y = rect.y();
		height = rect.height();
		width = rect.width();
		_width = width;
		_height = height;
		imgHeight = img.height();
		imgWidth = img.width();

		// We get pixel values in separate iterations and in order to keep
		// the byte data in a predictable format at de-serialization.
		// Get values of pixels along left-hand edge, top to bottom.
		for (int j = y; j < y + height; j++) {
			if ((0 <= j && j < imgHeight) && (2 < x && x < imgWidth - 2)) 
				_pixels.add(cvGet2D(img, j, x));
		}

		// Get values of pixels along bottom edge, left to right.
		for (int i = x; i < x + width; i++) {
			if ((0 <= i && i < imgWidth) && (2 < y + height && y + height < imgHeight - 2)) 
				_pixels.add(cvGet2D(img, y + height, i));
		}

		// Get values of pixels along right edge, bottom to top.
		for (int j = y + height; j > y; j--) {
			if (0 <= j && j < imgHeight && (2 < x + width && x + width < imgWidth - 2))
				_pixels.add(cvGet2D(img, j, x + width));
		}

		// Get values of pixels along top edge, right to left.
		for (int i = x + width; i > x; i--) {
			if (0 <= i && i < imgWidth && (2 < y && y < imgHeight - 2))
				_pixels.add(cvGet2D(img, y, i));
		}
		pixelsToBytes();
	}

	/**
	 * From instance _pixels value, populate _bytes array with appropriate byte
	 * values.
	 */
	private void pixelsToBytes() {
		CvScalar p;
		_bytes = new byte[_pixels.size() * PIXEL_CHANNELS * 4];
		for (int i = 0; i < _pixels.size() * (PIXEL_CHANNELS); i += PIXEL_CHANNELS) {
			p = _pixels.get(i / (PIXEL_CHANNELS));
			for (int j = 0; j < PIXEL_CHANNELS; j++) {
				_bytes[i + j] = (byte) ((int) p.val(j) & 0xFF);
			}
		}
	}

	/**
	 * Compress byte array using JPEG algorithm. TODO: clear _bytes from memory.
	 * 
	 * @param compressor
	 *            The instance of TJCompressor used to perform the compression.
	 */
	public void compress(TJCompressor compressor) {
		try {
			compressor.setSourceImage(_bytes, // src buf
					0, // x offset
					0, // y offset
					2, // width
					0, // pitch
					(_width + _height), // height
					TJ.PF_RGB);
			compressor.setSubsamp(TJ.SAMP_420);
			compressor.setJPEGQuality(100);
			_compressed = compressor.compress(0);
			_compressedSize = compressor.getCompressedSize();
			compressor.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Decompress byte array which was compressed using JPEG algorithm.
	 * 
	 * @param decompressor
	 *            The instance of TJDecompressor to be used to perfrom the
	 *            decompression.
	 */
	public void decompress(TJDecompressor tjd) {
		try {
			if (_compressed == null || _compressed.length == 0)
				return;

			_decompressed = new byte[2 * (_height + _width) * PIXEL_CHANNELS
					* 100];
			tjd.setJPEGImage(_compressed, _compressedSize);

			tjd.decompress(_decompressed, 0, 0, 2, 0, _height + _width,
					TJ.PF_RGB, 0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * @return Use _decompressed to reconstruct an array list of pixels.
	 */
	public ArrayList<CvScalar> reconstructPixels() {

		CvScalar pixel;
		int r, g, b;
		_pixels = new ArrayList<CvScalar>();

		if (_decompressed == null || _decompressed.length == 0) {
			System.exit(1);
		}

		for (int i = 0; i < 2 * (_width + _height) * PIXEL_CHANNELS; i += PIXEL_CHANNELS) {
			r = _decompressed[i] & 0xFF;
			g = _decompressed[i + 1] & 0xFF;
			b = _decompressed[i + 2] & 0xFF;
			pixel = new CvScalar(r, g, b, 0f);
			_pixels.add(pixel);
		}
		return _pixels;
	}

}
