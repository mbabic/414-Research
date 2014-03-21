package Examples;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

class My_Panel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;

	public My_Panel() {
		super();
	}

	public boolean MatToBufferedImage(Mat matBGR) {
//		long startTime = System.nanoTime();
		int width = matBGR.width(), height = matBGR.height(), channels = matBGR
				.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		matBGR.get(0, 0, sourcePixels);
		// create new image and get reference to backing data
		image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster()
				.getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
//		long endTime = System.nanoTime();
//		System.out.println(String.format("Elapsed time: %.2f ms",
//				(float) (endTime - startTime) / 1000000));
		return true;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.image == null)
			return;
		g.drawImage(this.image, 10, 10, this.image.getWidth(),
				this.image.getHeight(), null);
		// g.drawString("This is my custom Panel!",10,20);
	}
}

class processor {
	private CascadeClassifier face_cascade0;
	private CascadeClassifier face_cascade1;
	private CascadeClassifier face_cascade2;
	// Create a constructor method

	public processor() {
		face_cascade0 = new CascadeClassifier(
				"C:/OpenCV/opencv/sources/data/haarcascades/haarcascade_frontalface_default.xml");
		face_cascade1 = new CascadeClassifier(
				"C:/OpenCV/opencv/sources/data/haarcascades/haarcascade_frontalface_alt.xml");
		face_cascade2 = new CascadeClassifier(
				"C:/OpenCV/opencv/sources/data/haarcascades/haarcascade_profileface.xml");
		if (face_cascade0.empty() || face_cascade1.empty() || face_cascade2.empty()) {
			System.out.println("--(!)Error loading A\n");
			return;
		} else {
			System.out.println("Face classifier loooaaaaaded up");
		}
	}

	public Mat detect(Mat inputframe) {
		Mat mRgba = new Mat();
		Mat mGrey = new Mat();
		MatOfRect faces0 = new MatOfRect();
		MatOfRect faces1 = new MatOfRect();
		MatOfRect faces2 = new MatOfRect();
		inputframe.copyTo(mRgba);
		inputframe.copyTo(mGrey);
		Imgproc.cvtColor(mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(mGrey, mGrey);
		face_cascade0.detectMultiScale(mGrey, faces0);
		face_cascade1.detectMultiScale(mGrey, faces1);
		face_cascade2.detectMultiScale(mGrey, faces2);
//		System.out.println(String.format("Detected %s faces",
//				(faces0.toArray().length + faces1.toArray().length + faces2.toArray().length)));
		for (Rect rect : faces0.toArray()) {
			Core.rectangle(mRgba, new Point(rect.x, rect.y
					- rect.height * 0.25), new Point(rect.x + rect.width,
					rect.y + rect.height), new Scalar(255, 0, 0),
					2/*Core.FILLED*/, 8, 0);

		}
		for (Rect rect : faces1.toArray()) {
			Core.rectangle(mRgba, new Point(rect.x, rect.y
					- rect.height * 0.25), new Point(rect.x + rect.width,
					rect.y + rect.height), new Scalar(0, 255, 0),
					2/*Core.FILLED*/, 8, 0);

		}
		for (Rect rect : faces2.toArray()) {
			Core.rectangle(mRgba, new Point(rect.x, rect.y
					- rect.height * 0.25), new Point(rect.x + rect.width,
					rect.y + rect.height), new Scalar(0, 0, 255),
					2/*Core.FILLED*/, 8, 0);

		}
		return mRgba;
	}
}

public class window {
	public static void main(String arg[]) throws InterruptedException {
		System.loadLibrary("opencv_java248");
		String window_name = "Capture - Face detection";
		JFrame frame = new JFrame(window_name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);

		processor my_processor = new processor();
		My_Panel my_panel = new My_Panel();
		frame.setContentPane(my_panel);
		frame.setVisible(true);

		Mat webcam_image = new Mat();
		VideoCapture capture = new VideoCapture(0);

		if (capture.isOpened()) {
			Thread.sleep(900);
			while (true) {
				capture.read(webcam_image);
				if (webcam_image.empty()) {

					System.out.println(" --(!) No captured frame -- Break!");
					break;
				} else {
					frame.setSize(webcam_image.width() + 40,
							webcam_image.height() + 60);
					
					webcam_image = my_processor.detect(webcam_image);
					
					my_panel.MatToBufferedImage(webcam_image);
					my_panel.repaint();
				}
			}
		}
		return;
	}
}