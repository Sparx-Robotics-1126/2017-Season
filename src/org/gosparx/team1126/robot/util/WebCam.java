package org.gosparx.team1126.robot.util;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;




public class WebCam {
	Mat frame = new Mat();
	VideoCapture camera;
	
	
	public WebCam(){
		camera = new VideoCapture(0);
	}
	
	public Mat capture (){	

	if(!camera.isOpened()){
		System.out.println("Camera Error");
		return null;
	}
	
//	try
//	{
//		Process p = Runtime.getRuntime().exec("v4l2-ctl -d /dev/video0 -c exposure_auto=1 -c exposure_absolute=0");
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	finally
//	{
//	}

	//f
	//System.out.println("Frame Grabbed");
	//camera.retrieve(frame);
	//System.out.println("Frame Decoded");

	camera.read(frame);
	//System.out.println("Frame Obtained");

	/* No difference
    camera.release();
	 */

	//System.out.println("Captured Frame Width " + frame.width());
	return frame;
	}
	public void close(){
		camera.release();
	}
}
