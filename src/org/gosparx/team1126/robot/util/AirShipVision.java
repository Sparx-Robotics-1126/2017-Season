package org.gosparx.team1126.robot.util;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class AirShipVision {
	static final int H_MIN = 70; //Hue min
	static final int H_MAX = 90; //Hue max
	static final int S_MIN = 150; //Sat min and max
	static final int S_MAX = 255;
	static final int V_MIN = 30; //value min and max
	static final int V_MAX = 160;
	static final byte MAX_KERNEL_LENGTH = 4; //For blur
	static final int threshold = 10; //for in range 
	static Point topLeftRect1; //top left of first rectangle
	static Point bottomRightRect2; //bottom right of second rectangle
	static final int SMALLEST_CONTOUR=15; //smallest contour size allowed
	static final int LARGEST_CONTOUR=600; //largest contour size allowed
	static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
	static double centerX = 0;
	static int count =0;
	
	public static void main(String[] args) {
		WebCam camera = new WebCam();
		Mat image=new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		MatOfPoint firstRectContour;
		MatOfPoint secondRectContour;
		while (true)  
		{
			image = camera.capture();
			//Blurs image with Max kernal length  
			for(int i=1; i<MAX_KERNEL_LENGTH;i+=2)
				Imgproc.blur(image, image, new Size(i,i),new Point(-1,-1)); 
			//Convert to hsv
			Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV); 
			// Isolate HSV values in range.
			Core.inRange(image, new Scalar(H_MIN, S_MIN, V_MIN), new Scalar(H_MAX, S_MAX, V_MAX), image);
			//Canny conversion
			Imgproc.Canny(image, image, 1, 3);
			//Finds contours		
			Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
			 
			// Contours of rect 1 and rect 2 are found below  
			if(!contours.isEmpty()&&contours.size()>=3){
				firstRectContour=findLargestContour(contours);
				findLargestContour(contours); //removes second contour for rect 1
				secondRectContour=findLargestContour(contours);
			}
			else{
				firstRectContour=null;
				secondRectContour=null;
			}
			//Makes sure rects were found
			if (firstRectContour != null && !firstRectContour.empty()&&secondRectContour != null && !secondRectContour.empty()){
				//Finds the Bounding Box
				Rect boundingRect = Imgproc.boundingRect(firstRectContour);
				Rect boundingRect2 = Imgproc.boundingRect(secondRectContour);
				//Finds the top left corner and right corner of bounding box's 
				topLeftRect1 = boundingRect.tl();
				bottomRightRect2 = boundingRect2.br(); //For the 2nd target
				//Imgproc.rectangle(image,topLeft,bottomRight, new Scalar(255,255,255));			
				centerX=((topLeftRect1.x+bottomRightRect2.x)/2);
				degreesOffCenter();
			}
			else 
			{
				System.out.println("Error");
			}
			//resets contours
			contours=new ArrayList<MatOfPoint>();
		}
	}

	/**
	 * Measures how many degrees off center the target is from the camera. 
	 * If the target is off the the right of the camera a negative number is returned
	 * If target is to the left a positive angle is returned 
	 */
	public static double degreesOffCenter(){
		double degreesOff = ((320-centerX)*.07984375);
		System.out.println(degreesOff);
		return degreesOff;
	}

	/**
	 * Finds the largest contour in the list and removes it from list
	 * @param contour the list of contours
	 * @return the largest contour
	 */
	public static MatOfPoint findLargestContour(List<MatOfPoint> contour){
		Point[] largest=contour.get(0).toArray();
		MatOfPoint greatest = null;
		int newSize;
		int index=0;
		for (int i = 1; i < contour.size(); i++) {
			newSize=contour.get(i).toArray().length;
			if(newSize>SMALLEST_CONTOUR && newSize<LARGEST_CONTOUR && contour.get(i).toArray().length>largest.length){
				greatest=contour.get(i);
				index=i;
			}
		}
		contour.remove(index);
		return greatest;
	} 
}
