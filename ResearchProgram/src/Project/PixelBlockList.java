package Project;

import java.util.ArrayList;

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
