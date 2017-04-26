#include "HighGoalFinder.h"

#include "RectangleFinderIF.h"
#include "RectangleFinder.h"
#include "ConfigReader.h"

#include <cmath>
#include <fstream>

// Config File
static const auto CONFIG_FILE = "/home/ubuntu/Development/Vision/config/HighGoalFinderConfig.txt";
static const auto CAMERA_FILE = "/home/ubuntu/Development/Vision/config/HighGoalCamera.txt";

// Defaults
static const auto DEBUG = false;
static const auto DEBUG_DIR = "/home/ubuntu/Development/Vision/logs/HighGoalFinder";

static const auto USE_CAMERA = true;
static const auto CAMERA_ID = 0u;

static const auto HUE_MIN = 40u;
static const auto HUE_MAX = 85u;
static const auto SATURATION_MIN = 200u;
static const auto SATURATION_MAX = 255u;
static const auto VALUE_MIN = 37u;
static const auto VALUE_MAX = 255u;

static const auto BLUR_FACTOR = 8u;

static const auto USE_QUADRATIC = true;

HighGoalFinder::HighGoalFinder() :
  rectangleFinder(nullptr)
, useQuadratic(USE_QUADRATIC)
{
	unsigned int cameraID = CAMERA_ID;
	std::ifstream f(CAMERA_FILE);
	if (!(f >> cameraID))
	{
		cameraID = CAMERA_ID;
	}

	ConfigReader reader(CONFIG_FILE);

	RectangleFinderConfig config;
	config.debug = reader.GetBool("DEBUG", DEBUG);
	config.debugDirectory = reader.GetString("DEBUG_DIR", DEBUG_DIR);
	config.useCamera = reader.GetBool("USE_CAMERA", USE_CAMERA);
	config.cameraID = cameraID;
	config.hueMin = reader.GetUInt("HUE_MIN", HUE_MIN);
	config.hueMax = reader.GetUInt("HUE_MAX", HUE_MAX);
	config.saturationMin = reader.GetUInt("SATURATION_MIN", SATURATION_MIN);
	config.saturationMax = reader.GetUInt("SATURATION_MAX", SATURATION_MAX);
	config.valueMin = reader.GetUInt("VALUE_MIN", VALUE_MIN);
	config.valueMax = reader.GetUInt("VALUE_MAX", VALUE_MIN);
	config.blurFactor = reader.GetUInt("BLUR_FACTOR", BLUR_FACTOR);

	useQuadratic = reader.GetBool("USE_QUADRATIC", useQuadratic);

	rectangleFinder.reset(new RectangleFinder(config));
}

void HighGoalFinder::Open()
{	
	rectangleFinder->Open();
}

double CalculateQuadraticDistance(const unsigned int maxY)
{
	return 0.000197138007 * std::pow(static_cast<double>(maxY), 2.0) + 0.084058888 * maxY + 32.021895;
}

double CalculateLookupDistance(const unsigned int maxY)
{
	if (maxY <= 106u)
	{
		return 41.5;
	}
	else if (maxY <= 127u)
	{
		return 44.5;
	}
	else if (maxY <= 149u)
	{
		return 47.5;
	}
	else if (maxY <= 169u)
	{
		return 50.5;
	}
	else if (maxY <= 188u)
	{
		return 53.5;
	}
	else if (maxY <= 206u)
	{
		return 56.5;
	}
	else if (maxY <= 224u)
	{
		return 59.5;
	}
	else if (maxY <= 241u)
	{
		return 62.5;
	}
	else if (maxY <= 258u)
	{
		return 65.5;
	}
	else if (maxY <= 275u)
	{
		return 68.5;
	}
	else if (maxY <= 292u)
	{
		return 71.5;
	}
	else if (maxY <= 306u)
	{
		return 74.5;
	}
	else if (maxY <= 320u)
	{
		return 77.5;
	}
	else if (maxY <= 335u)
	{
		return 80.5;
	}
	else if (maxY <= 349u)
	{
		return 83.5;
	}
	else if (maxY <= 362u)
	{
		return 86.5;
	}
	else if (maxY <= 374u)
	{
		return 89.5;
	}
	else if (maxY <= 384u)
	{
		return 92.5;
	}
	else if (maxY <= 395u)
	{
		return 95.5;
	}
	else if (maxY <= 408u)
	{
		return 98.5;
	}
	else if (maxY <= 424u)
	{
		return 101.5;
	}
	else if (maxY <= 437u)
	{
		return 104.5;
	}
	else if (maxY <= 443u)
	{
		return 107.5;
	}
	else
	{
		return -1.0;
	}
}

TargetFinderIF::TargetData HighGoalFinder::GetTargetData()
{
	TargetFinderIF::TargetData data;

	const auto rectangles = rectangleFinder->GetRectangles();

	const cv::Rect* topRectangleToUse = nullptr;

	for (const auto& rectangle : rectangles)
	{
		if (rectangle.width >= 50u && rectangle.width <= 100u &&
		    rectangle.height >= 12u && rectangle.height <= 45u &&
		    rectangle.tl().x >= 2u && rectangle.br().x <= (X_RESOLUTION - 2u) &&
		    rectangle.tl().y >= 2u && rectangle.br().y <= (Y_RESOLUTION -2u))
		{
			if (!topRectangleToUse || rectangle.y < topRectangleToUse->y)
			{
				topRectangleToUse = &rectangle;
			}
		}
	}

	if (topRectangleToUse)
	{
		data.angle = ((static_cast<double>(topRectangleToUse->br().x + topRectangleToUse->tl().x) / 2u) -
		              (static_cast<double>(X_RESOLUTION) / 2u)) * DEGREES_PER_PIXEL;

		const auto maxY = topRectangleToUse->y;

		if (maxY >= 90u && maxY <= 443u)
		{
			data.distance = useQuadratic ? CalculateQuadraticDistance(maxY) :
			                               CalculateLookupDistance(maxY);
		}
		else
		{
			// Target is either too close or too far away to accurately determine distance.
			data.distance = -1.0;
		}
	}
	else
	{
		data.angle = -180.0;
		data.distance = -1.0;
	}

	return data;
}

void HighGoalFinder::Release()
{
	rectangleFinder->Release();
}

