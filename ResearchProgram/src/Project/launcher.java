package Project;

import java.io.File;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class launcher {

	public static void main(String[] args) {
		File outb = new File("C:/outb.a");
		File outf = new File("C:/outf.a");
		Transmitter t = new Transmitter(outb, outf);
		Analyzer a;
		try {
			FrameGrabber f = t.receiveStream();
			a = new Analyzer();
			IplImage m;
			IplImage n;
			IplImage c;
			CvSeq r;
			UI gui = new UI();
			while(true){
				m = f.grab();
				n = m.clone();
				r = a.detectFaces(m);
				a.blackOutFaces(n, r);
				c = a.extractForeground(m, n);
				gui.putFrame(m, n, c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (ClassiferLoadFailure e) {
			e.printStackTrace();
		}

	}

}
