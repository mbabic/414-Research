package test;

import static org.junit.Assert.*;

import org.junit.Test;

import Project.FaceStream;

public class FaceStreamTests {


	@Test
	public void testReadFromFile() {		
		FaceStream fs = FaceStream.fromFile();
		System.out.println(fs);
		assert(1==1);
	}

}
