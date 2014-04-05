package test;

import static org.junit.Assert.*;

import org.junit.Test;

import Project.Decoder;

public class DecoderTests {

	@Test
	public void testDecode() {
		Decoder d = new Decoder("out/zzztest.hevc", "zzztestOut", 640, 360);
		d.decode();
	}

}
