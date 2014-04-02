package Project;

import java.io.File;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class MergerLauncher {

	public static void main(String[] args) {
		UI gui = new UI();
		File inb = new File("out/outb.yuv");
		File inf = new File("out/outf.yuv");
		Transmitter transmitter = new Transmitter();
		Analyzer anlyzer;
		FrameGrabber backFrameGrabber, faceFrameGrabber;
		IplImage mergImage, backImage, faceImage;
		
//		try {
//			backFrameGrabber = transmitter.receiveStream(inb);
//			faceFrameGrabber = transmitter.receiveStream(inf);
//		}catch
		
	}

}
