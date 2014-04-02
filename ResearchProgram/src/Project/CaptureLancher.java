package Project;

import java.io.File;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class CaptureLancher {
	public static void main(String[] args) {
		UI gui = new UI();
		File outb = new File("out/outb.avi");
		File outf = new File("out/outf.avi");
		Transmitter transmitter = new Transmitter();
		Analyzer analyzer = null;
		FrameGrabber frameGrabber = null;
		IplImage origImage, backImage, faceImage;
		FaceStream stream = new FaceStream();
		File inf = new File("test/test.avi");
		try {
			frameGrabber = transmitter.receiveStream(inf);
		} catch (Exception e1) {
			System.err.println("Faile to load FrameGrabber");
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
			stream.toFile();
			gui.destroy();
			System.exit(0);

		}

	}

}
