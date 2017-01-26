package org.gosparx.team1126.robot.util;
import java.util.ArrayList;
import org.opencv.imgcodecs.*;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ForTheRoboRio {
	static final int H_MIN = 70; //Hue min
	static final int H_MAX = 90; //Hue max
	static final int S_MIN = 150; //Sat min and max
	static final int S_MAX = 255;
	static final int V_MIN = 30; //value min and max
	static final int V_MAX = 130;
	static final byte MAX_KERNEL_LENGTH = 4; //For blur
	static final int threshold = 10; //for in range 
	static Point topLeft; //top left of rectangle
	static Point bottomRight; //bottom right of rectangle
	static final int NUM_CAPTURES = 10;
	static final int SMALLEST_CONTOUR=10; //smallest contour size allowed
	static final int LARGEST_CONTOUR=300; //largest contour size allowed
	static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
	static double center;
	static double width;
	static double widthTotal = 0; 
	
	public static void main(String[] args) {
		int count = 0;  //amount of errors
		WebCam camera = new WebCam();
		Mat image=new Mat();
		
		long startTime = System.currentTimeMillis();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		
		for (int ii = 0; ii < NUM_CAPTURES; ++ii)  //change to how to video stream 
		{
			image = camera.capture();
			if(image!=null&&!image.empty()){
				//Blurs image with Max kernal length 
				blur(image);
				//Convert to hsv
				Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV); 
				// Isolate HSV values in range.
				Core.inRange(image, new Scalar(H_MIN, S_MIN, V_MIN), new Scalar(H_MAX, S_MAX, V_MAX), image);
				//Canny conversion
				Imgproc.Canny(image, image, 1, 3);
				//Finds contours		
				Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

				if (!contours.isEmpty()){ //2 countours for every rectangle
					//Finds the Bounding Box
					Rect boundingRect = Imgproc.boundingRect(contours.get(0));
					//Rect boundingRect2 = Imgproc.boundingRect(contours.get(2));
					//Finds the top left corner and right corner of bounding box's 
					topLeft = boundingRect.tl();
					//Point topLeft2 = boundingRect2.tl(); //For the 2nd target
					bottomRight = boundingRect.br();
					//Point bottomRight2 = boundingRect2.br(); //For the 2nd target
					width = bottomRight.x-topLeft.x;
					findDistanceFromGoal();
					//Point = new Point((topLeft.x+bottomRight.x)/2.0,(topLeft.y+bottomRight.y)/2.0);
					widthTotal += findDistanceFromGoal();
					center=((topLeft.x+bottomRight.x)/2);
					Imgproc.rectangle(image, bottomRight, topLeft, new Scalar(30,30,255));
					Imgcodecs.imwrite("/home/admin/test.jpg",image);
				}
				else{
					count++;
				} //add to the amount crashed 
			}
		}

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime+" mili");
		System.out.println(count+" errors");
		camera.close();
	}
	
	/**
	 * This method uses a look up table of values to find distance away
	 * @param width of the rectangle seen in perspective 
	 * @return the distance in feet away from target
	 */
	public static double findDistanceFromGoal(){
		return (.001116531569*(width*width)-.311488390152*width+28.7931405153)*12;
	}
	
	public static double degreesOffCenter(){
		return ((320-center)*.07984375);
	}
	
		/**
		 * Finds the largest contour in the list
		 * @param contour the list of contours
		 * @return the largest contour
		 */
		public MatOfPoint findLargestContour(ArrayList<MatOfPoint> contour){
			Point[] largest=contour.get(0).toArray();
			MatOfPoint greatest = null;
			int newSize;
			for (int i = 1; i < contour.size(); i++) {
				newSize=contour.get(i).toArray().length;
				if(newSize>SMALLEST_CONTOUR && newSize<LARGEST_CONTOUR && contour.get(i).toArray().length>largest.length){
					greatest=contour.get(i);
					largest=contour.get(i).toArray();
				}
			}
			return greatest;
			
		} 
		private static void blur(Mat image){
			//Blurs image with Max kernal length 
			for(int i=1; i<MAX_KERNEL_LENGTH;i+=2)
				Imgproc.blur(image, image, new Size(i,i),new Point(-1,-1)); 
		}
}


//TALK TO MIKE OF HOW HES GOING TO GET OUR VARIABLES
//DO LIVE STREAMING
//HANDLE MORE THAN ONE REFLECTION