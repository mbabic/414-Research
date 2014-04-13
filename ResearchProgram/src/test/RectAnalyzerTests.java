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
	
	@Test
	public void midPointTests() {
		CvRect cvr1 = new CvRect(243, 0, 145, 228);
		CvRect cvr2 = new CvRect(260, 0, 99, 268);
		System.out.println(RectAnalyzer.dist(cvr1, cvr2));
		System.out.println(RectAnalyzer.topLeftDist(cvr1, cvr2));
	}
	
	@Test 
	public void testMinBoundingRect() {
		CvRect cvr1 = new CvRect(93, 0, 213, 293);
		CvRect cvr2 = new CvRect(84, 0, 235, 165);
		CvRect cvr3 = new CvRect(93, 131, 182, 182);
		System.out.println(RectAnalyzer.getMinBoundingRect(cvr1, cvr2));
		System.out.println(RectAnalyzer.getMinBoundingRect(cvr1, cvr3));
		System.out.println(RectAnalyzer.getMinBoundingRect(cvr3, cvr2));


	}

}
