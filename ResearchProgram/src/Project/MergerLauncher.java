package Project;

import java.io.File;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class MergerLauncher {

	public static void main(String[] args) {
		UI gui = new UI();
		File inb = new File("out/outb.avi");
		File inf = new File("out/outf.avi");
		Transmitter transmitter = new Transmitter();
		Analyzer analyzer = null;
		FrameGrabber backFrameGrabber = null, faceFrameGrabber = null;
		IplImage mergImage, backImage, faceImage;
		
		try {
			backFrameGrabber = transmitter.receiveStream(inb);
			faceFrameGrabber = transmitter.receiveStream(inf);
		} catch (Exception e) {
			System.err.println("Failed to load the FrameGrabbers");
			e.printStackTrace();
			gui.destroy();
			System.exit(-1);
		}
		try {
			analyzer = new Analyzer();
		} catch (ClassiferLoadFailure e) {
			System.err.println("Failed to create analyzer");
			e.printStackTrace();
			gui.destroy();
			System.exit(-1);
		}
		try {
			while (gui.isVisible()) {
				backImage = backFrameGrabber.grab();
				faceImage = faceFrameGrabber.grab();
				if (backImage == null || faceImage == null) {
					backFrameGrabber.restart();
					faceFrameGrabber.release();
					backFrameGrabber.start();
					faceFrameGrabber.start();
					backImage = backFrameGrabber.grab();
					faceImage = faceFrameGrabber.grab();

				}
				mergImage = backImage.clone();
				//TODO Figure out how to get the recs
//				analyzer.recombineVideo(mergImage, backImage, faceImage, null);
				gui.putFrame(mergImage, backImage, faceImage);
			}
			transmitter.close();
			gui.destroy();
			System.exit(0);
			
		} catch (Exception e) {
		
			e.printStackTrace();
		} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
			e.printStackTrace();
		}		
	}

}
