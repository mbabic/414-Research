package Project;

import java.io.File;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;

public class launcher {

	public static void main(String[] args) {
		File outb = new File("C:/outb.a");
		File outf = new File("C:/outf.a");
		Transmitter t = new Transmitter(outb, outf);
		try {
			FrameGrabber f = t.receiveStream();

			UI gui = new UI();
			while(true){
				gui.putFrame(f.grab());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
