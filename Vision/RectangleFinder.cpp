#include "RectangleFinder.h"

#include <algorithm>
#include <exception>
#include <iostream>
#include <utility>

static const auto SOURCE_IMAGE = "target.jpg";
static const auto ORIGINAL_IMAGE = "0-original.jpg";
static const auto BLUR_IMAGE = "1-blur.jpg";
static const auto HSV_IMAGE = "2-hsv.jpg";
static const auto FILTER_IMAGE = "3-inrange.jpg";
static const auto RECTANGLE_IMAGE = "4-rectangle.jpg";

RectangleFinder::RectangleFinder(RectangleFinderConfig config) :
  config(std::move(config))
{
}

void RectangleFinder::Open()
{
	try
	{
		if (config.useCamera)
		{
			if (!camera.isOpened())
			{
				if (camera.open(config.cameraID))
				{
					errorOpening = false;
					std::cout << "Opened camera " << config.cameraID << "." << std::endl; 
				}
				else if (!errorOpening)
				{
					errorOpening = true;
					std::cerr << "Error opening camera " << config.cameraID << "." << std::endl;
				}
			}
		}

		exceptionFound = false;
	}
	catch (const std::exception& e)
	{
		if (!exceptionFound)
		{
			exceptionFound = true;
			std::cerr << "Exception found: " << e.what() << std::endl;
		}
	}
}

void RectangleFinder::Capture()
{
	if (config.useCamera)
	{
		camera >> image;
	}
	else
	{
		image = cv::imread(config.debugDirectory + std::string("/") + SOURCE_IMAGE);
	}

	if (config.debug)
	{
		cv::imwrite(config.debugDirectory + std::string("/") + ORIGINAL_IMAGE, image);
	}
}

void RectangleFinder::Blur()
{
	for (unsigned int i = 1; i < config.blurFactor; i += 2u)
	{
		cv::blur(image, image, cv::Size(i, i), cv::Point(-1, -1), cv::BORDER_CONSTANT);
	}

	if (config.debug)
	{
		cv::imwrite(config.debugDirectory + std::string("/") + BLUR_IMAGE, image);
	}
}

void RectangleFinder::ConvertToHSV()
{
	cv::cvtColor(image, image, cv::COLOR_BGR2HSV);

	if (config.debug)
	{
		cv::imwrite(config.debugDirectory + std::string("/") + HSV_IMAGE, image);
	}
}

void RectangleFinder::FilterRange()
{
	cv::inRange(image, cv::Scalar(config.hueMin, config.saturationMin, config.valueMin), cv::Scalar(config.hueMax, config.saturationMax, config.valueMax), image);

	if (config.debug)
	{
		cv::imwrite(config.debugDirectory + std::string("/") + FILTER_IMAGE, image);
	}
}

std::vector<cv::Rect> RectangleFinder::GetRectangles()
{
	std::vector<cv::Rect> boundingRectangles;

	try
	{
		if (camera.isOpened() || !config.useCamera)
		{
			Capture();
			Blur();
			ConvertToHSV();
			FilterRange();

			std::vector<std::vector<cv::Point> > contours;
			cv::findContours(image, contours, cv::RETR_TREE, cv::CHAIN_APPROX_SIMPLE);

			boundingRectangles.reserve(contours.size());

			for (const auto& contour : contours)
			{
				boundingRectangles.emplace_back(cv::boundingRect(cv::Mat(contour)));
			}

			std::sort(boundingRectangles.begin(), boundingRectangles.end(), [](const cv::Rect& lhs, const cv::Rect& rhs)
			{
				return lhs.area() > rhs.area();
			});

			if (config.debug)
			{
				std::cout << "Found " << boundingRectangles.size() << " rectangles." << std::endl;

				for (const auto& rectangle : boundingRectangles)
				{
					std::cout << "start ----------" << std::endl				         
						  << "x: " << rectangle.x << std::endl
						  << "y: " << rectangle.y << std::endl
						  << "width: " << rectangle.width << std::endl
						  << "height: " << rectangle.height << std::endl
					          << "end ------------" << std::endl;

					cv::rectangle(image, rectangle, cv::Scalar(255, 50, 50), 1);
				}

				cv::imwrite(config.debugDirectory + std::string("/") + RECTANGLE_IMAGE, image);
			}
		}

		exceptionFound = false;
	}
	catch (const std::exception& e)
	{
		if (!exceptionFound)
		{
			exceptionFound = true;
			std::cerr << "Exception found: " << e.what() << std::endl;
		}
	}

	return boundingRectangles;
}

void RectangleFinder::Release()
{
	try
	{
		if (config.useCamera)
		{
			if (camera.isOpened())
			{
				errorOpening = false;
				camera.release();
			}
		}

		exceptionFound = false;
	}
	catch (const std::exception& e)
	{
		if (!exceptionFound)
		{
			exceptionFound = true;
			std::cerr << "Exception found: " << e.what() << std::endl;
		}
	}
}

