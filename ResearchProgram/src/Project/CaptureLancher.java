package Project;

import java.io.File;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class CaptureLancher {

	public static void main(String[] args) {

		LoadingPanel settingsPanel = new LoadingPanel();
		while (settingsPanel.isVisible())
			Thread.yield();

		Settings.SAVE_PIXELS = settingsPanel.getPixelSave();
		Settings.FPS = settingsPanel.getFPS();
		Settings.FRAMES = settingsPanel.getframes();

		int mode = settingsPanel.getInputMode();
		String password = settingsPanel.getPassword();
		if (password != "")
			Settings.PASSWORD = password;

		File outb = new File(Settings.OUTB);
		File outf = new File(Settings.OUTF);
		outb.deleteOnExit();
		outf.deleteOnExit();

		Transmitter transmitter = new Transmitter();
		FrameGrabber frameGrabber = null;
		IplImage origImage, backImage, faceImage;
		FaceStream stream = new FaceStream();

		try {
			if (mode == LoadingPanel.FILE) {
				File file = settingsPanel.getFile();
				frameGrabber = transmitter.receiveStream(file);
			} else {
				frameGrabber = transmitter.receiveStream();
			}
		} catch (Exception e1) {
			System.err.println("Failed to load FrameGrabber");
			e1.printStackTrace();
			System.exit(-1);
		}
		settingsPanel.destroy();
		UI gui = new UI();
		Analyzer analyzer = null;

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
			Settings.HEIGHT = origImage.height();
			Settings.WIDTH = origImage.width();
			stream = new FaceStream(origImage);
			transmitter.initializeRecorders(outb, outf);
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
			System.exit(1);
		} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
			e.printStackTrace();
			System.exit(2);
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

}
