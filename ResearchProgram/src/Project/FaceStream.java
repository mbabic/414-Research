package Project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Serializable stream of FaceStreamElements.
 * @author Marko Babic, Marcus Karpoff
 * 
 */
public class FaceStream implements java.io.Serializable {

	/**
	 * Auto-generated UID.
	 */
	private static final long serialVersionUID = -2647220701334355386L;
	
	/**
	 * Aggregation of FaceStreamElements.
	 */
	public ArrayList<FaceStreamElement> _stream;

	/** 
	 * Private state variable.  Keeps tracks of the next element to be 
	 * retrieved from the stream.
	 */
	private int _frame;


	/** 
	 * Width of the frame.
	 */
	public int _imgWidth;
	
	/**
	 * Height of the frame.
	 */
	public int _imgHeight;
	
	/**
	 * Empty constructor.
	 */
	public FaceStream() {
		_stream = new ArrayList<FaceStreamElement>();
		_frame = 0;
	}
	
	/**
	 * 
	 */
	public FaceStream(IplImage img) {
		_stream = new ArrayList<FaceStreamElement>();
		_frame = 0;
		_imgWidth = img.width();
		_imgHeight = img.height();
	}

	/**
	 * Reset instance attribute _frame to beginning such that next call to
	 * getNextElement() returns the information associated with the first
	 * frame in the stream.
	 */
	public void restart() {
		_frame = 0;
	}

	/**
	 * @param fse
	 * 		The face stream element to be added.
	 */
	public void add(FaceStreamElement fse) {
		_stream.add(fse);
	}
	

	/**
	 * @return
	 * 		The next FaceStreamElement in the stream.
	 */
	public FaceStreamElement getNextElement() {
		return _stream.get(_frame++);
	}

	public void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
	}

	public void readObject(ObjectInputStream ois)
			throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
	}

	/**
	 * Write the instance to a file.
	 */
	public void toFile() {
		OutputStream file = null;
		OutputStream buf = null;
		ObjectOutput out = null;
		try {
			file = new FileOutputStream(Settings.FACESTREAM_OUT);
			buf = new BufferedOutputStream(file);
			out = new ObjectOutputStream(buf);
			out.writeObject(this);
		} catch (IOException ioe) {
			System.err.println(ioe.toString());
			ioe.printStackTrace();
			System.exit(1);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				buf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return
	 * 		Deserialized FaceStream from file.
	 */
	public static FaceStream fromFile() {
		InputStream file = null;
		InputStream buf = null;
		ObjectInput in = null;
		try {
			file = new FileInputStream(Settings.FACESTREAM_OUT);
			buf = new BufferedInputStream(file);
			// TODO This needs a way to be closed
			in = new ObjectInputStream(buf);
			return (FaceStream) in.readObject();
		} catch (ClassNotFoundException cnfe) {
			System.err.println(cnfe.toString());
			cnfe.printStackTrace();
			System.exit(1);
		} catch (IOException ioe) {
			System.err.println(ioe.toString());
			ioe.printStackTrace();
			System.exit(1);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				buf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Not reached.
		return null;
	}
}
