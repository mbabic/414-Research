package Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Implements functionality allowing for serialization between files 
 * and byte arrays.
 * @author Marko Babic, Marcus Karpoff
 */
public class IOUtils {

	/**
	 * Convert the file specified by the given filename to a one-dimensional
	 * array of bytes.
	 * @param filename
	 * 		The name of the file to be written to a byte array.
	 * @return
	 * 		Byte array representation of the file specified by the given 
	 * 		filename.
	 * @throws IOException
	 */
	public static byte[] fileToBytes(String filename) throws IOException {
		File file = new File(filename);
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		byte[] bytes;
		try {
			bytes = new byte[(int) raf.length()];
			raf.readFully(bytes);
			return bytes;
		} finally {
			raf.close();
		} 
	}
	
	/**
	 * Writes given byte array to a file.  The produced file is placed in the 
	 * Settings.OUT path.
	 * @param bytes
	 * 		The byte array to be written to a file.
	 * @param filename
	 * 		The name of the file to be produced.
	 */
	public static void bytesToFile(byte[] bytes, String filename) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(Settings.OUT + filename);
			fos.write(bytes);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			System.exit(1);
		} finally {
			fos.close();
		}
	}
	
}
