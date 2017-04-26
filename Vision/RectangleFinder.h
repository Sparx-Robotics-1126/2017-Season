#ifndef RECTANGLE_FINDER_H
#define RECTANGLE_FINDER_H

#include "RectangleFinderIF.h"

#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include <string>

struct RectangleFinderConfig
{
	bool debug;
	std::string debugDirectory;
	bool useCamera;
	unsigned int cameraID;
	unsigned int hueMin;
	unsigned int hueMax;
	unsigned int saturationMin;
	unsigned int saturationMax;
	unsigned int valueMin;
	unsigned int valueMax;
	unsigned int blurFactor;
};

class RectangleFinder : public RectangleFinderIF
{
public:
	RectangleFinder(RectangleFinderConfig config);
private:
	virtual void Open() override;
	virtual std::vector<cv::Rect> GetRectangles() override;
	virtual void Release() override;

	void Capture();
	void Blur();
	void ConvertToHSV();
	void FilterRange();

	bool errorOpening;
	bool exceptionFound;

	cv::VideoCapture camera;
	cv::Mat image;

	RectangleFinderConfig config;
};

#endif
