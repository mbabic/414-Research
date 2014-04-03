package test;

import static org.junit.Assert.*;

import org.junit.Test;

import Project.Encoder;

public class EncoderTests {

	@Test
	public void testWriteConfigurationFile() {
		Encoder e1= new Encoder("test");
		e1.writeConfigurationFile();
		Encoder e2 = new Encoder("test", 1, 2, 3, 4);
		e2.writeConfigurationFile();
		assert(true);
	}

}
