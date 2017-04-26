#ifndef RECTANGLE_FINDER_IF_H
#define RECTANGLE_FINDER_IF_H

#include <opencv2/core/core.hpp>

#include <vector>

class RectangleFinderIF
{
public:
	virtual ~RectangleFinderIF() = default;

	virtual void Open() = 0;
	virtual std::vector<cv::Rect> GetRectangles() = 0;
	virtual void Release() = 0;
};

#endif

