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
import org.opencv.imgcodecs.Imgcodecs;

public class HighGoalVision implements TargetFinder{
	static final int H_MIN = 70; //Hue min
	static final int H_MAX = 90; //Hue max
	static final int S_MIN = 150; //Sat min and max
	static final int S_MAX = 255;
	static final int V_MIN = 30; //value min and max
	static final int V_MAX = 160;
	static final byte MAX_KERNEL_LENGTH = 4; //For blur
	static final int threshold = 10; //for in range 
	static Point topLeft; //top left of rectangle
	static Point bottomRight; //bottom right of rectangle
	static final int SMALLEST_CONTOUR=15; //smallest contour size allowed
	static final int LARGEST_CONTOUR=600; //largest contour size allowed
	static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
	static double centerX = 0;
	static double height = 0;
	static int count =0;

	public void run() {
		WebCam camera = new WebCam();
		Mat image=new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		MatOfPoint largestContour;
	
		while (true)  
		{
			image = camera.capture();
			Imgcodecs.imwrite("/home/admin/hello3.jpg", image);
			//Blurs image with Max kernal length  
			for(int i=1; i<MAX_KERNEL_LENGTH;i+=2)
				Imgproc.blur(image, image, new Size(i,i),new Point(-1,-1)); 
			//Convert to hsv
			Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV); 
			// Isolate HSV values in range.
			Core.inRange(image, new Scalar(H_MIN, S_MIN, V_MIN), new Scalar(H_MAX, S_MAX, V_MAX), image);
			Imgcodecs.imwrite("/home/admin/hello4.jpg", image);
			//Canny conversion
			Imgproc.Canny(image, image, 1, 3);
			//Finds contours		
			Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

			if(!contours.isEmpty()){
				largestContour=findLargestContour(contours);
				
			}
			else{
				largestContour=null;
			}
			if (largestContour != null && !largestContour.empty()){ //2 countours for every rectangle
				//Finds the Bounding Box
				Rect boundingRect = Imgproc.boundingRect(largestContour);
				
				//Finds the top left corner and right corner of bounding box
				topLeft = boundingRect.tl();
				bottomRight = boundingRect.br();
				height = bottomRight.y-topLeft.y;
				centerX=((topLeft.x+bottomRight.x)/2);
				Imgproc.rectangle(image, bottomRight, topLeft, new Scalar(30,20,100));
				Shooter.visionUpdate(degreesOffCenter(), distanceFromGoal());
			}
			else 
			{
				System.out.println("Error");
			}
			contours=new ArrayList<MatOfPoint>();
		}
	}

	/**
	 * Determines how far away the camera is from the target in inches
	 */
	public double distanceFromGoal(){  //TODO change this once we can take measurements
		double distanceAway = (.001116531569*(height*height)-.311488390152*height+28.7931405153)*12;
		System.out.println(distanceAway);
		return distanceAway;
	}

	/**
	 * Measures how many degrees off center the target is from the camera. 
	 * If the target is off the the right of the camera a negative number is returned
	 * If target is to the left a positive angle is returned 
	 */
	public double degreesOffCenter(){
		double degreesOff = ((320-centerX)*.07984375);
		System.out.println(degreesOff);
		return degreesOff;
	}

	/**
	 * Finds the largest contour in the list
	 * @param contour the list of contours
	 * @return the largest contour
	 */
	public static MatOfPoint findLargestContour(List<MatOfPoint> contour){
		Point[] largest=contour.get(0).toArray();
		MatOfPoint greatest = null;
		int newSize;
		for (int i = 1; i < contour.size(); i++) {
			newSize=contour.get(i).toArray().length;
			if(newSize>SMALLEST_CONTOUR && newSize<LARGEST_CONTOUR && contour.get(i).toArray().length>largest.length){
				greatest=contour.get(i);
			}
		}
		return greatest;
	} 
}