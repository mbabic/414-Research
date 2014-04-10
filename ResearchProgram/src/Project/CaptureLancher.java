package Project;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class CaptureLancher {
	public static void main(String[] args) {
		// TODO: setup mechanism to specify input file and encryption password.
		UI gui = new UI();
		File outb = new File(Settings.OUTB);
		File outf = new File(Settings.OUTF);
		Transmitter transmitter = new Transmitter();
		Analyzer analyzer = null;
		FrameGrabber frameGrabber = null;
		IplImage origImage, backImage, faceImage;
		FaceStream stream = new FaceStream();
		
		try {
			File file = grabMediaFile();
			if (file == null) {
				frameGrabber = transmitter.receiveStream();
			} else {
				frameGrabber = transmitter.receiveStream(file);
			}
			
		} catch (Exception e1) {
			System.err.println("Failed to load FrameGrabber");
			e1.printStackTrace();
			gui.destroy();
			System.exit(-1);
		}
		
		try {
			analyzer = new Analyzer();
		} catch (ClassiferLoadFailure e1) {
			System.err.println("Failed to create analyzer");
			e1.printStackTrace();
			gui.destroy();
			System.exit(-1);
		}
		try {

			origImage = frameGrabber.grab();
			transmitter.initializeRecorders(outb, outf, origImage);
			backImage = origImage.clone();
			faceImage = origImage.clone();
			analyzer.separateStreams(origImage, backImage, faceImage, stream);
			gui.putFrame(origImage, backImage, faceImage);
			transmitter.videoBuilder(backImage, faceImage);
			while (gui.isVisible()) {
				origImage = frameGrabber.grab();
				backImage = origImage.clone();
				analyzer.separateStreams(origImage, backImage, faceImage,
						stream);
				gui.putFrame(origImage, backImage, faceImage);
				transmitter.videoBuilder(backImage, faceImage);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
			e.printStackTrace();
		} finally {
			
			try {
				transmitter.close();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
				e.printStackTrace();
			}
			
			try {
				frameGrabber.stop();
				frameGrabber.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stream.toFile();
			gui.destroy();
			transmitter.encodeHECV();
			System.exit(0);

		}

	}
	
	private static File grabMediaFile() {
		File file = null;
		JFileChooser chooser;
		int option;
		
		chooser = new JFileChooser(System.getProperty("user.dir"));

		chooser.setFileFilter(new FileFilter() {
			
			private FileNameExtensionFilter exFilter = new FileNameExtensionFilter("mp4", "avi");
			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean accept(File f) {
				
				if (f.isDirectory()) return true;
				return exFilter.accept(f);
			}
		});
		chooser.setAcceptAllFileFilterUsed(false);		
		option = chooser.showOpenDialog(chooser);
		
		if (option == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
		}
		return file;
	}
}
