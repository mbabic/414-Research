package test;

import static org.junit.Assert.*;

import org.junit.Test;

import Project.FaceStream;
import Project.FaceStreamElement;

public class FaceStreamTests {


	@Test
	public void testReadFromFile() {		
		FaceStream fs = FaceStream.fromFile();
		System.out.println(fs);
		FaceStreamElement fse = fs.getNextElement();
//		assert(1==1);
	}

}
