package org.gosparx.team1126.robot.util;
import java.util.ArrayList;
import java.util.List;
//import org.opencv.core.CvType;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

//import org.opencv.videoio.VideoCapture;
//import org.opencv.videoio.Videoio;



public class ForTheRoboRio {
	static int count = 0;  //amount of errors
	static final int AmountOfRectanglesLookedFor = 1; //amount of targets eg two for boiler
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
	//static final double[] lookUpTable = {110.0, 108.0, 103.0, 101.0, 98.0, 95.0,92.0,91.0,88.0,87.0,84.0,82.0, 80.0};  
	static final int SMALLEST_CONTOUR=10; //smallest contour size allowed
	static final int LARGEST_CONTOUR=300; //largest contour size allowed

	static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
	
	
	public static void main(String[] args) {

		// get the jpg image from the internal resource folder
		WebCam camera = new WebCam();
		Mat image = camera.capture();
		double widthTotal = 0; 
		//		int nullImageCount = 0; //The amount of times the camera came up as null


		long startTime = System.currentTimeMillis();
		for (int ii = 0; ii < NUM_CAPTURES; ++ii)
		{
			image = camera.capture();
			if(image!=null&&!image.empty()){
				//			nullImageCount = 0;
				//			while (image.empty() && nullImageCount<1 ) {
				//				if(image.empty()){
				//					image = camera.capture();
				//				}
				//				nullImageCount+=1;
				//			}


				//Blurs image with Max kernal length 
//				for(int i=1; i<MAX_KERNEL_LENGTH;i+=2){
//					Imgproc.blur(image, image, new Size(i,i),new Point(-1,-1));}

				//Convert to hsv
				Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV); 

				// Isolate HSV values in range.
				Core.inRange(image, new Scalar(H_MIN, S_MIN, V_MIN), new Scalar(H_MAX, S_MAX, V_MAX), image);

				//		Imgcodecs.imwrite("resources/generated/-2blur.jpg", image);

				//Canny conversion
				Imgproc.Canny(image, image, 1, 3);

				//declaration for finding lines
				List<MatOfPoint> contours = new ArrayList<MatOfPoint>();


				//Finds contours		
				Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

				
				if (!contours.isEmpty() && contours.size()==AmountOfRectanglesLookedFor*2){ //2 countours for every rectangle
					//Finds the Bounding Box
					Rect boundingRect = Imgproc.boundingRect(contours.get(0));
					//Rect boundingRect2 = Imgproc.boundingRect(contours.get(2));


					//Finds the top left corner and right corner of bounding box's 
					topLeft = boundingRect.tl();
					//Point topLeft2 = boundingRect2.tl(); //For the 2nd target

					bottomRight = boundingRect.br();
					//Point bottomRight2 = boundingRect2.br(); //For the 2nd target
					//System.out.println(topLeft+" "+bottomRight);
					double width = bottomRight.x-topLeft.x;
					findDistanceFromGoal(width);
					Point center = new Point((topLeft.x+bottomRight.x)/2.0,(topLeft.y+bottomRight.y)/2.0);
					//double overAllX = (center.x-center2.x)/2;
					//System.out.println(overAllX);		
					//Imgcodecs.imwrite("resources/tester/rectangle.jpg", image);
					
					widthTotal += findDistanceFromGoal(width);
					double radius=findDistanceFromGoal(width);
					System.out.println(radius);
					System.out.println("Center: "+center);
					System.out.println("Degrees off center: "+(320-center.x)*.0975);
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
		System.out.println(widthTotal/(10-count));
		Imgcodecs.imwrite("reallyUnique.jpg",image);
		return;

	}
	/**
	 * This method uses a look up table of values to find distance away
	 * @param width of the rectangle seen in perspective 
	 * @return the distance in feet away from target
	 */
	public static double findDistanceFromGoal(double width){
		//for(int i=0; i<lookUpTable.length-1;i++){
			
			//if(width<=lookUpTable[i] &&width>lookUpTable[i+1])
				//return (i*.25)+8.0;
		//}	
		//return (18.82487-.099444*width)*12;
		return (.001116531569*(width*width)-.311488390152*width+28.7931405153)*12;
	
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
	
}


package org.gosparx.team1126.robot.util;
import java.util.ArrayList;
import java.util.List;
//import org.opencv.core.CvType;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

//import org.opencv.videoio.VideoCapture;
//import org.opencv.videoio.Videoio;



public class ForTheRoboRio {
	static int count = 0;  //amount of errors
	static final int AmountOfRectanglesLookedFor = 1; //amount of targets eg two for boiler
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
	//static final double[] lookUpTable = {110.0, 108.0, 103.0, 101.0, 98.0, 95.0,92.0,91.0,88.0,87.0,84.0,82.0, 80.0};  
	static final int SMALLEST_CONTOUR=10; //smallest contour size allowed
	static final int LARGEST_CONTOUR=300; //largest contour size allowed

	static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
	
	
	public static void main(String[] args) {

		// get the jpg image from the internal resource folder
		WebCam camera = new WebCam();
		Mat image = camera.capture();
		double widthTotal = 0; 
		//		int nullImageCount = 0; //The amount of times the camera came up as null


		long startTime = System.currentTimeMillis();
		for (int ii = 0; ii < NUM_CAPTURES; ++ii)
		{
			image = camera.capture();
			if(image!=null&&!image.empty()){
				//			nullImageCount = 0;
				//			while (image.empty() && nullImageCount<1 ) {
				//				if(image.empty()){
				//					image = camera.capture();
				//				}
				//				nullImageCount+=1;
				//			}


				//Blurs image with Max kernal length 
//				for(int i=1; i<MAX_KERNEL_LENGTH;i+=2){
//					Imgproc.blur(image, image, new Size(i,i),new Point(-1,-1));}

				//Convert to hsv
				Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV); 

				// Isolate HSV values in range.
				Core.inRange(image, new Scalar(H_MIN, S_MIN, V_MIN), new Scalar(H_MAX, S_MAX, V_MAX), image);

				//		Imgcodecs.imwrite("resources/generated/-2blur.jpg", image);

				//Canny conversion
				Imgproc.Canny(image, image, 1, 3);

				//declaration for finding lines
				List<MatOfPoint> contours = new ArrayList<MatOfPoint>();


				//Finds contours		
				Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

				
				if (!contours.isEmpty() && contours.size()==AmountOfRectanglesLookedFor*2){ //2 countours for every rectangle
					//Finds the Bounding Box
					Rect boundingRect = Imgproc.boundingRect(contours.get(0));
					//Rect boundingRect2 = Imgproc.boundingRect(contours.get(2));


					//Finds the top left corner and right corner of bounding box's 
					topLeft = boundingRect.tl();
					//Point topLeft2 = boundingRect2.tl(); //For the 2nd target

					bottomRight = boundingRect.br();
					//Point bottomRight2 = boundingRect2.br(); //For the 2nd target
					//System.out.println(topLeft+" "+bottomRight);
					double width = bottomRight.x-topLeft.x;
					findDistanceFromGoal(width);
					//Point center = new Point((topLeft.x+bottomRight.x)/2.0,(topLeft.y+bottomRight.y)/2.0);
					//Point center2 = new Point((topLeft2.x+bottomRight2.x)/2.0,(topLeft2.y+bottomRight2.y)/2.0); //For 2nd Target
					//double overAllX = (center.x-center2.x)/2;
					//System.out.println(overAllX);		
					//Imgcodecs.imwrite("resources/tester/rectangle.jpg", image);
					
					widthTotal += findDistanceFromGoal(width);

					System.out.println(findDistanceFromGoal(width));
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
		System.out.println(widthTotal/(10-count));
		Imgcodecs.imwrite("reallyUnique.jpg",image);
		return;

	}
	/**
	 * This method uses a look up table of values to find distance away
	 * @param width of the rectangle seen in perspective 
	 * @return the distance in feet away from target
	 */
	public static double findDistanceFromGoal(double width){
		//for(int i=0; i<lookUpTable.length-1;i++){
			
			//if(width<=lookUpTable[i] &&width>lookUpTable[i+1])
				//return (i*.25)+8.0;
		//}	
		//return (18.82487-.099444*width)*12;
		return (.001116531569*(width*width)-.311488390152*width+28.7931405153)*12;
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
}


