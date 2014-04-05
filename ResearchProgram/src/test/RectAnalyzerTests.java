package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import Project.RectAnalyzer;

import com.googlecode.javacv.cpp.opencv_core.CvRect;

public class RectAnalyzerTests {

	@Test
	public void testGetBoundingRects() {
		ArrayList<CvRect> rects = new ArrayList<CvRect>();
		CvRect cvr1 = new CvRect(100, 100, 100, 100);
		CvRect cvr2 = new CvRect(50, 50, 50, 50);
		CvRect cvr3 = new CvRect(201, 200, 1, 1);
		rects.add(cvr1);
		rects.add(cvr2);
		rects.add(cvr3);
		System.out.println(RectAnalyzer.getBoundingRects(rects));
	}

}
