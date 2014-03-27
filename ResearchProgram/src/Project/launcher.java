package Project;

import java.io.File;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class launcher {
	public static IplImage stupid;
	public static void main(String[] args) {
		System.loadLibrary("opencv_java248");
		UI gui = new UI();
		File outb = new File("out/outb.yuv");
		File outf = new File("out/outf.yuv");
		Transmitter transmitter = new Transmitter();
		Analyzer analyzer;
		FrameGrabber frameGrabber;
		FaceTracker faceTracker;
		IplImage origImage, backImage, faceImage ;
		
		/////////////////////////////////
		CanvasFrame f = new CanvasFrame("merged");
		IplImage mergImage;
		/////////////////////////////////
		
		
		try {
			
			frameGrabber = transmitter.receiveStream();
			analyzer = new Analyzer();
			faceTracker = new FaceTracker();
			origImage = frameGrabber.grab();
			transmitter.initializeRecorders(outb, outf, origImage);
			backImage = origImage.clone();
			faceImage = origImage.clone();
			
			/////////////////////
			mergImage = origImage.clone();
			/////////////////////
			

			while(gui.isVisible()){
				origImage = frameGrabber.grab();
				backImage = origImage.clone();
				analyzer.separateStreams(origImage, backImage, faceImage);
				gui.putFrame(origImage, backImage, faceImage);
				transmitter.videoBuilder(backImage, faceImage);
				
				//////////////////////////////////////////////
				analyzer.recombineVideo(mergImage, backImage, faceImage);
				if (stupid == null || stupid.isNull()) {
					f.showImage(faceImage);
				} else {
					f.showImage(stupid);
				}
					//////////////////////////////////////////////
			}
	
			gui.destroy();
			transmitter.close();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			gui.destroy();
		} catch (ClassiferLoadFailure e) {
			e.printStackTrace();
			gui.destroy();
		} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
			e.printStackTrace();
			gui.destroy();
		}

	}


}
