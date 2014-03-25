package Project;

import java.io.File;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class launcher {

	public static void main(String[] args) {
		File outb = new File("C:/outb.avi");
		File outf = new File("C:/outf.avi");
		Transmitter transmitter = new Transmitter();
		Analyzer analyzer;
		try {
			
			FrameGrabber frameGrabber = transmitter.receiveStream();
			analyzer = new Analyzer();
			IplImage origImage;
			origImage = frameGrabber.grab();
			transmitter.initializeRecorders(outb, outf, origImage);
			IplImage backImage = origImage.clone();
			IplImage faceImage = origImage.clone();
			UI gui = new UI();
			while(true){
				origImage = frameGrabber.grab();
				backImage = origImage.clone();
				analyzer.separateStreams(origImage, backImage, faceImage);
				gui.putFrame(origImage, backImage, faceImage);
				transmitter.videoBuilder(backImage, faceImage);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} catch (ClassiferLoadFailure e) {
			e.printStackTrace();
		} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
			e.printStackTrace();
		}

	}


}
