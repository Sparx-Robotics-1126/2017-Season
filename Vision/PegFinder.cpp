#include "PegFinder.h"

#include "RectangleFinderIF.h"
#include "RectangleFinder.h"
#include "ConfigReader.h"

#include <fstream>

// Config File
static const auto CONFIG_FILE = "/home/ubuntu/Development/Vision/config/PegFinderConfig.txt";
static const auto CAMERA_FILE = "/home/ubuntu/Development/Vision/config/PegCamera.txt";

// Defaults
static const auto DEBUG = false;
static const auto DEBUG_DIR = "/home/ubuntu/Development/Vision/logs/PegFinder";

static const auto USE_CAMERA = true;
static const auto CAMERA_ID = 1u;

static const auto HUE_MIN = 40u;
static const auto HUE_MAX = 85u;
static const auto SATURATION_MIN = 200u;
static const auto SATURATION_MAX = 255u;
static const auto VALUE_MIN = 37u;
static const auto VALUE_MAX = 255u;

static const auto BLUR_FACTOR = 8u;

PegFinder::PegFinder() :
  rectangleFinder(nullptr)
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

	rectangleFinder.reset(new RectangleFinder(config));
}

void PegFinder::Open()
{
	rectangleFinder->Open();
}

TargetFinderIF::TargetData PegFinder::GetTargetData()
{
	TargetFinderIF::TargetData data;

	const auto rectangles = rectangleFinder->GetRectangles();

	const cv::Rect* leftRectangleToUse = nullptr;
	const cv::Rect* rightRectangleToUse = nullptr;

	for (const auto& rectangle : rectangles)
	{
		if (rectangle.height >= 28u && rectangle.height <= 90u &&
		    rectangle.tl().x >= 2u && rectangle.br().x <= (X_RESOLUTION - 2u) &&
		    rectangle.tl().y >= 2u && rectangle.br().y <= (Y_RESOLUTION -2u))
		{
			if (!leftRectangleToUse || rectangle.tl().x < leftRectangleToUse->tl().x)
			{
				leftRectangleToUse = &rectangle;
			}

			if (!rightRectangleToUse || rectangle.br().x > rightRectangleToUse->br().x)
			{
				rightRectangleToUse = &rectangle;
			}
		}
	}

	if (leftRectangleToUse && rightRectangleToUse && (leftRectangleToUse != rightRectangleToUse))
	{
		data.angle = ((static_cast<double>(leftRectangleToUse->tl().x + rightRectangleToUse->br().x) / 2u) -
			      (static_cast<double>(X_RESOLUTION) / 2u)) * DEGREES_PER_PIXEL;

		data.distance = 1.0;
	}
	else
	{
		data.angle = -180.0;
		data.distance = -1.0;
	}

	return data;
}

void PegFinder::Release()
{
	rectangleFinder->Release();
}

