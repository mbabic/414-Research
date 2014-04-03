package Project;

import java.io.File;
import java.util.ArrayList;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
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
		FaceStream stream = FaceStream.fromFile();
		ArrayList<CvRect> rects;
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
					faceFrameGrabber.restart();
					stream.restart();
					backFrameGrabber.start();
					faceFrameGrabber.start();
					backImage = backFrameGrabber.grab();
					faceImage = faceFrameGrabber.grab();
				}
				mergImage = backImage.clone();
				rects = stream.getNextRectList();
				analyzer.recombineVideo(mergImage, backImage, faceImage, rects);
				gui.putFrame(mergImage, backImage, faceImage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			gui.destroy();
			System.exit(0);
		}
		
	}

}
