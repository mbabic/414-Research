package Project;

import java.io.File;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class launcher {

	public static void main(String[] args) {
		File outb = new File("C:/outb.a");
		File outf = new File("C:/outf.a");
		Transmitter transmitter = new Transmitter(outb, outf);
		Analyzer analyzer;
		try {
			FrameGrabber frameGrabber = transmitter.receiveStream();
			analyzer = new Analyzer();
			IplImage origImage;
			origImage = frameGrabber.grab();
			
			IplImage backImage = origImage.clone();
			IplImage faceImage = origImage.clone();
			Boolean windowOpen = true;
			UI gui = new UI(windowOpen);
			while(windowOpen){
				origImage = frameGrabber.grab();
				backImage = origImage.clone();
				analyzer.separateStreams(origImage, backImage, faceImage);
				gui.putFrame(origImage, backImage, faceImage);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} catch (ClassiferLoadFailure e) {
			e.printStackTrace();
		}

	}


}
