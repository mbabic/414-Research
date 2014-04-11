package test;

import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.libjpegturbo.turbojpeg.TJCompressor;

import Project.PixelBlock;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
public class PixelBlockTests {

	@Test
	public void testCompression() {
		BufferedImage img;
		try {
			img = ImageIO.read(new File("tests/test.jpg"));
			IplImage test = IplImage.createFrom(img);
			System.out.println(test);
			CvRect cvr = new CvRect(100, 100, 100, 100);
			ArrayList<CvRect> rects = new ArrayList<CvRect>();
			rects.add(cvr);
			
			PixelBlock pb = new PixelBlock(test, cvr);
			
			TJCompressor compressor = new TJCompressor();
			pb.compress(compressor);
			System.out.println(compressor.getCompressedSize());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
