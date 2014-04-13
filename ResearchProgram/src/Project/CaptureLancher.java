package Project;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class CaptureLancher {

	public static void main(String[] args) {
		UI gui = new UI();
		File outb = new File(Settings.OUTB);
		File outf = new File(Settings.OUTF);
		Transmitter transmitter = new Transmitter();
		Analyzer analyzer = null;
		FrameGrabber frameGrabber = null;
		IplImage origImage, backImage, faceImage;
		FaceStream stream = new FaceStream();
		
		LoadingPanel settingsPanel = new LoadingPanel();
		int mode = settingsPanel.getInputMode();
		String password = settingsPanel.getPassword();
		
		try {
			if (mode == LoadingPanel.FILE){
				File file = grabMediaFile();
				if (file == null) {
					System.exit(0);
				} else {
					frameGrabber = transmitter.receiveStream(file);
				}
			} else {
				frameGrabber = transmitter.receiveStream();
			}
		} catch (Exception e1) {
			System.err.println("Failed to load FrameGrabber");
			e1.printStackTrace();
			gui.destroy();
			System.exit(-1);
		}
		
		try {
			analyzer = new Analyzer();
		} catch (ClassiferLoadFailure e1) {
			System.err.println("Failed to create analyzer");
			e1.printStackTrace();
			gui.destroy();
			System.exit(-1);
		}
		try {

			origImage = frameGrabber.grab();
			transmitter.initializeRecorders(outb, outf, origImage);
			backImage = origImage.clone();
			faceImage = origImage.clone();
			analyzer.separateStreams(origImage, backImage, faceImage, stream);
			gui.putFrame(origImage, backImage, faceImage);
			transmitter.videoBuilder(backImage, faceImage);
			while (gui.isVisible()) {
				origImage = frameGrabber.grab();
				backImage = origImage.clone();
				analyzer.separateStreams(origImage, backImage, faceImage,
						stream);
				gui.putFrame(origImage, backImage, faceImage);
				transmitter.videoBuilder(backImage, faceImage);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
			e.printStackTrace();
			System.exit(2);
		} finally {
			
			try {
				transmitter.close();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
				e.printStackTrace();
			}
			
			try {
				frameGrabber.stop();
				frameGrabber.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stream.toFile();
			gui.destroy();
			transmitter.encodeHECV();
			outb.deleteOnExit();
			outf.deleteOnExit();
			System.exit(0);

		}

	}
	
	private static File grabMediaFile() {
		File file = null;
		JFileChooser chooser;
		int option;
		
		chooser = new JFileChooser(System.getProperty("user.dir"));

		chooser.setFileFilter(new FileFilter() {
			
			private FileNameExtensionFilter exFilter = new FileNameExtensionFilter("mp4", "avi");
			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) return true;
				return exFilter.accept(f);
			}
		});
		chooser.setAcceptAllFileFilterUsed(false);		
		option = chooser.showOpenDialog(chooser);
		
		if (option == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
		}
		return file;
	}
}
