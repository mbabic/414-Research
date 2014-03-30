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

import com.googlecode.javacv.cpp.opencv_core.CvRect;

/**
 * Stream of SerializableRectLists.
 * @author Marko Babic, Marcus Karpoff
 *
 */
public class FaceStream implements java.io.Serializable {

	/**
	 * Auto-generated UID.
	 */
	private static final long serialVersionUID = -2647220701334355386L;

	public ArrayList<SerializableRectList> _stream;
	int _frame;
	
	public FaceStream() {
		_stream = new ArrayList<SerializableRectList>();
		_frame = 0;
	}
	
	public void add(SerializableRectList faces) {
		_stream.add(faces);
	}
	
	public ArrayList<CvRect> getNextRectList() {
		return _stream.get(_frame++).toCvRectList();
	}
	
	public void writeObject(ObjectOutputStream oos) throws IOException{
		oos.defaultWriteObject();
	}
	
	public void readObject(ObjectInputStream ois) 
		throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
	}
	
	public void toFile() {
		try {
			OutputStream file 	= new FileOutputStream(Settings.FACESTREAM_OUT);
			OutputStream buf 	= new BufferedOutputStream(file);
			ObjectOutput out 	= new ObjectOutputStream(buf);
			out.writeObject(this);
		} catch (IOException ioe) {
			System.err.println(ioe.toString());
			System.exit(1);
		}
	}
	
	public static FaceStream fromFile() {
		try {
			InputStream file 	= new FileInputStream(Settings.FACESTREAM_OUT);
			InputStream buf 	= new BufferedInputStream(file);
			ObjectInput in 		= new ObjectInputStream(buf);
			return (FaceStream) in.readObject();
		} catch (ClassNotFoundException cnfe) {
			System.err.println(cnfe.toString());
			System.exit(1);
		} catch (IOException ioe) {
			System.err.println(ioe.toString());
			System.exit(1);
		}
		// Not reached.
		return null;
	}
	
}
