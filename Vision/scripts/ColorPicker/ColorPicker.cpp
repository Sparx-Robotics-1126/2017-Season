#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <iostream>
#include <opencv2/imgproc/imgproc.hpp>

//static const int H_MAX = 130;
//static const int H_MIN = 65;
//static const int S_MAX = 255;
//static const int S_MIN = 175; //170
//static const int V_MAX = 120;
//static const int V_MIN = 29;

void trackBar(int value, void*){}

int main()
{
	cv::vector<std::vector<cv::Point> > contours;
	cv::namedWindow("Display window", cv::WINDOW_AUTOSIZE );// Create a window for display
	int* hueMin;
	int intHueMin = 37;
	hueMin = &intHueMin;

	int* hueMax;
	int intHueMax = 71;
	hueMax = &intHueMax;

	int* satMin;
	int intSatMin = 197;
	satMin = &intSatMin;

	int* satMax;
	int intSatMax = 255;
	satMax = &intSatMax;

	int* valMin;
	int intValMin = 42;
	valMin = &intValMin;

	int* valMax;
	int intValMax = 255;
	valMax = &intValMax;
	cv::VideoCapture hi;
	cv::Mat image;

	cv::namedWindow("Display window", cv::WINDOW_AUTOSIZE );// Create a window for display.
	while(true){
	image = cv::imread("/home/ubuntu/Development/Vision/logs/target.jpg");   // Read the file
//	hi.open(1);
//	hi>>image;
	cv::cvtColor(image,image,cv::COLOR_BGR2HSV);
    cv::inRange(image, cv::Scalar(*hueMin,*satMin,*valMin), cv::Scalar(*hueMax,*satMax,*valMax), image);

    //try{
    //cv::findContours(image, contours, cv::RETR_TREE, cv::CHAIN_APPROX_SIMPLE);
    //cv::Rect rectangle = cv::boundingRect(cv::Mat(contours[0]));
	//cv::rectangle(image,rectangle,cv::Scalar(100,100,100));
    //cv::imshow("Display window", image); // Show our image inside it.
    //}catch(std::exception e){std::cout<<e.what();}
    cv::imshow("Display window", image); // Show our image inside it.
    cv::createTrackbar("hueMin","Display window",hueMin,255,trackBar);//("hue","Display window",100,trackBar);
    cv::createTrackbar("hueMax","Display window",hueMax,255,trackBar);//("hue","Display window",100,trackBar);
    cv::createTrackbar("satMin","Display window",satMin,255,trackBar);//("hue","Display window",100,trackBar);
    cv::createTrackbar("satMax","Display window",satMax,255,trackBar);//("hue","Display window",100,trackBar);
    cv::createTrackbar("valMin","Display window",valMin,255,trackBar);//("hue","Display window",100,trackBar);
    cv::createTrackbar("valMax","Display window",valMax,255,trackBar);//("hue","Display window",100,trackBar);

    cv::waitKey(1);
	}
}


