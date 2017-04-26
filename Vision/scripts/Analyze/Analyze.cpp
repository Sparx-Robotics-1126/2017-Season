#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include <algorithm>
#include <exception>
#include <iostream>
#include <string>
#include <utility>
#include <vector>

static const auto HUE_MIN = 40u;
static const auto HUE_MAX = 85u;
static const auto SATURATION_MIN = 200u;
static const auto SATURATION_MAX = 255u;
static const auto VALUE_MIN = 37u;
static const auto VALUE_MAX = 255u;

static const auto BLUR_FACTOR = 8u;

static const auto MIN_WIDTH = 50u;
static const auto MAX_WIDTH = 100u;
static const auto MIN_HEIGHT = 12u;
static const auto MAX_HEIGHT = 45u;

/*
static const auto MIN_WIDTH = 0u;
static const auto MAX_WIDTH = 100u;
static const auto MIN_HEIGHT = 0u;
static const auto MAX_HEIGHT = 999u;
*/

static const std::vector<std::string> pictureNames =
{
	"41.5",
	"44.5",
	"47.5",
	"50.5",
	"53.5",
	"56.5",
	"59.5",
	"62.5",
	"65.5",
	"68.5",
	"71.5",
	"74.5",
	"77.5",
	"80.5",
	"83.5",
	"86.5",
	"89.5",
	"92.5",
	"95.5",
	"98.5",
	"101.5",
	"104.5",
	"107.5"
};

/*
static const std::vector<std::string> pictureNames =
{
	"peg1",
	"peg2"
};
*/

void Blur(cv::Mat& image)
{
	for (unsigned int i = 1u; i < BLUR_FACTOR; i += 2u)
	{
		cv::blur(image, image, cv::Size(i, i), cv::Point(-1, -1), cv::BORDER_CONSTANT);
	}
}

void ConvertToHSV(cv::Mat& image)
{
	cv::cvtColor(image, image, cv::COLOR_BGR2HSV);
}

void FilterRange(cv::Mat& image)
{
	cv::inRange(image, cv::Scalar(HUE_MIN, SATURATION_MIN, VALUE_MIN), cv::Scalar(HUE_MAX, SATURATION_MAX, VALUE_MAX), image);
}

int main()
{
	auto minWidth = MAX_WIDTH;
	auto maxWidth = MIN_WIDTH;
	auto minHeight = MAX_HEIGHT;
	auto maxHeight = MIN_HEIGHT;

	std::map<std::string, cv::Rect> finalRectangles;

	for (const auto& pictureName : pictureNames)
	{
		std::vector<cv::Rect> boundingRectangles;

		cv::Mat image;
		image = cv::imread(std::string("../Calibration/") + pictureName + std::string(".jpg"));	
		Blur(image);
		ConvertToHSV(image);
		FilterRange(image);
	
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

		std::cout << pictureName << ":" << std::endl;

		const cv::Rect* rectangleToUse = nullptr;
		for (const auto& rectangle : boundingRectangles)
		{
			if (rectangle.width >= MIN_WIDTH && rectangle.width <= MAX_WIDTH &&
			    rectangle.height >= MIN_HEIGHT && rectangle.height <= MAX_HEIGHT)
			{
				if (!rectangleToUse || rectangle.y < rectangleToUse->y)
				{
					rectangleToUse = &rectangle;
				}

				std::cout << "\ty: " << rectangle.y << std::endl
				          << "\twidth: " << rectangle.width << std::endl
				          << "\theight: " << rectangle.height << std::endl << std::endl;

				minWidth = std::min(minWidth, static_cast<unsigned int>(rectangle.width));
				maxWidth = std::max(maxWidth, static_cast<unsigned int>(rectangle.width));
				minHeight = std::min(minHeight, static_cast<unsigned int>(rectangle.height));
				maxHeight = std::max(maxHeight, static_cast<unsigned int>(rectangle.height));
			}
		}

		if (rectangleToUse)
		{
			finalRectangles.emplace(std::make_pair(pictureName, *rectangleToUse));
		}
	}
	
	std::cout << "Min Width: " << minWidth << std::endl
	          << "Max Width: " << maxWidth << std::endl
	          << "Min Height: " << minHeight << std::endl
	          << "Max Height: " << maxHeight << std::endl << std::endl;

	for (const auto& rectangle : finalRectangles)
	{
		std::cout << rectangle.first << ": " << rectangle.second.y << std::endl;
	}
}

