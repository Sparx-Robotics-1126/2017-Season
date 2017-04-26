#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include <iostream>
#include <sstream>
#include <string>

int main(int argc, char** argv)
{
	cv::VideoCapture camera;
	cv::Mat image;	

	int cameraID;
	std::string pictureID;

	if (argc >= 2)
	{
		if (argv[1][0] == '1')
		{
			cameraID = 1;
		}
		else
		{
			cameraID = 0;
		}
	}

	if (argc >= 3)
	{
		pictureID = argv[2];
	}
	else
	{
		pictureID = "";
	}

	if (camera.open(cameraID))
	{
		camera >> image;

		std::ostringstream ss;
		ss << "/home/ubuntu/Development/Vision/scripts/Calibration/";
		ss << "calibration";
		ss << pictureID;
		ss << ".jpg";

		cv::imwrite(ss.str(), image);
	}
	else
	{
		std::cerr << "Couldn't open camera " << cameraID << "." << std::endl;
	}

	return 0;
}
