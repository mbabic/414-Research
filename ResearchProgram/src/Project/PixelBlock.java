package Project;

import org.libjpegturbo.turbojpeg.TJCompressor;
import org.libjpegturbo.turbojpeg.TJDecompressor;

public class PixelBlock implements java.io.Serializable {

	/**
	 * Auto-generated serialization uid.
	 */
	private static final long serialVersionUID = -7335428971230485355L;
	
	/** Byte information associated with pixels. */
	byte[] _bytes;
	
	
	
	public PixelBlock() {
		
	}
	
	/**
	 * Flatten 
	 */
	public void flatten() {
		
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
}
