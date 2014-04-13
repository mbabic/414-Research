package test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import Project.ObjectTracker;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class AnalyzerTests {

	@Test
	public void test() {

		BufferedImage img;
		try {
			img = ImageIO.read(new File("tests/test.jpg"));
			IplImage test = IplImage.createFrom(img);
			CvRect cvr = new CvRect(100, 100, 100, 100);
			ObjectTracker t1 = new ObjectTracker();
			ObjectTracker t2 = new ObjectTracker();
			t1.trackNewObject(test, cvr);
			t2.trackNewObject(test, cvr);
			
			System.out.println(t1.equals(t2));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
	}

}
