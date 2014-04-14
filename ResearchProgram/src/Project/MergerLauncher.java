package Project;

import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;

import java.io.File;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_video.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.cpp.avcodec;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class MergerLauncher {

	public static void main(String[] args) {
		// Gets the user to supply the password
		PasswordPanel panel_Password = new PasswordPanel();
		while (panel_Password.isVisible())
			Thread.yield();

		String password = panel_Password.getPassword();
		if (password != "")
			Settings.PASSWORD = password;
		panel_Password.dispose();

		UI gui = new UI();
		File inb = new File(Settings.OUT + Settings.DECODED_OUTB_NAME + ".avi");
		File inf = new File(Settings.OUT + Settings.DECODED_OUTF_NAME + ".avi");
//		inb.deleteOnExit();
//		inf.deleteOnExit();
		Transmitter transmitter = new Transmitter();

		FaceStream stream = FaceStream.fromFile();
		Settings.WIDTH = stream._imgWidth;
		Settings.HEIGHT = stream._imgHeight;
		transmitter.setUpDecoders(Settings.OUT + Settings.DECRYPTED_OUTF_NAME,
				Settings.OUT + Settings.ENCODED_OUTB_NAME, stream._imgWidth, stream._imgHeight);

		transmitter.decodeHEVC();

		Analyzer analyzer = null;
		FrameGrabber backFrameGrabber = null, faceFrameGrabber = null;
		IplImage mergImage, backImage, faceImage;
		FFmpegFrameRecorder recorder = null;		
		FaceStreamElement fse;
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
			boolean onceThrough = false;
			while (gui.isVisible()) {
				backImage = backFrameGrabber.grab();
				faceImage = faceFrameGrabber.grab();

				if (!onceThrough && backImage != null) {
					recorder = new FFmpegFrameRecorder(new File(
							"out/mergedOut.avi"), Settings.WIDTH,
							Settings.HEIGHT);

					recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
					// Indicate that we want the encoding to be lossless
					recorder.setVideoQuality(0);
					try {
						recorder.start();
						onceThrough = true;
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}

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
				fse = stream.getNextElement();
				analyzer.recombineVideo(mergImage, backImage, faceImage, fse);
				if (onceThrough) {
					try {
						recorder.record(mergImage);
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				gui.putFrame(mergImage, backImage, faceImage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				recorder.stop();
				recorder.release();
			} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
				e.printStackTrace();
			}
			gui.destroy();
			System.exit(0);
		}

	}

}
