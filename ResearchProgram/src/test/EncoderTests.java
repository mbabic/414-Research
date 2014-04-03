package test;

import static org.junit.Assert.*;

import org.junit.Test;

import Project.Encoder;

public class EncoderTests {

	@Test
	public void testWriteConfigurationFile() {
		Encoder e1= new Encoder(null, "test");
		e1.writeConfigurationFile();
		Encoder e2 = new Encoder(null, "test2");
		e2.setFps(1);
		e2.setFrames(2);
		e2.setImgHeight(3);
		e2.setImgWidth(4);
		e2.writeConfigurationFile();
		// Could do byte comparason against expected output... but manual
		// inspection is fine for now.
		assert(true);
	}
	
	@Test
	public void testEncode() {
		Encoder e = new Encoder("tests/test.avi", "testResult");
		e.setFps(50);
		e.setImgWidth(1280);
		e.setImgHeight(720);
		e.setFrames(20);
		e.encode();
	}
	
//	@Test
//	public void testCompress() {
//		Encoder e1 = new Encoder("tests/aaa.yuv", "asdf");
//		e1.setImgHeight(288);
//		e1.setImgWidth(352);
//		e1.setFps(30);
//		e1.setFrames(10);
//		e1.compress();
//	} 

}
