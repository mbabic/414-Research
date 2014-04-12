package test;

import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.libjpegturbo.turbojpeg.TJCompressor;
import org.libjpegturbo.turbojpeg.TJDecompressor;

import Project.IOUtils;
import Project.PixelBlock;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
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
			ArrayList<CvScalar> pixels = new ArrayList<CvScalar>(pb._pixels);
			ArrayList<CvScalar> reconstructedPixels;
			TJCompressor compressor = new TJCompressor();
			TJDecompressor decompressor = new TJDecompressor();
			pb.compress(compressor);
			IOUtils.bytesToFile(pb._compressed, "asdfasdf.jpg");
			System.out.println(compressor.getCompressedSize());
			pb.decompress(decompressor);
			System.out.println(decompressor.getJPEGSize());
			System.out.println(pb._decompressed.length);
//			for (int i = 0; i < pb._decompressed.length; i++) {
//				if (Math.abs((pb._decompressed[i] & 0xFF) - (pb._bytes[i] & 0xFF)) > 10 ) {
//					System.out.println("i: " + i);
//					System.out.println((pb._decompressed[i] & 0xFF));
//					System.out.println((pb._bytes[i] & 0xFF));
//					fail("Pixel values differ by more than set threshold");
//				}
//			}
			
			reconstructedPixels = pb.reconstructPixels();
			for (int i = 0; i < reconstructedPixels.size(); i++) {
				if (reconstructedPixels.get(i).val(0) != pixels.get(i).val(0)) {
					System.out.println(reconstructedPixels.get(i));
					System.out.println(pixels.get(i));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
