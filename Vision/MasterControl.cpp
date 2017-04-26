#include "MasterControl.h"

#include "TargetFinderIF.h"
#include "HighGoalFinder.h"
#include "PegFinder.h"
#include "ConfigReader.h"

#include "llvm/StringRef.h"
#include "networktables/NetworkTable.h"

#include <array>
#include <string>

#include <iostream>

// Config File
static const auto CONFIG_FILE = "/home/ubuntu/Development/Vision/config/MasterControlConfig.txt";

// Defaults
static const auto DEBUG = false;
static const std::string IP = "10.11.26.2";

// Constants
static const std::string TARGET_DATA_TABLE_NAME = "targetData";
static const std::string HIGH_GOAL_KEY = "highGoal";
static const std::string PEG_KEY = "peg";
static const std::string MODE_KEY = "mode";
static const unsigned int DATA_ARRAY_SIZE = 3u;
static const double MAX_REFRESH_DOUBLE = 10000.0;

MasterControl::MasterControl() :
  pegRefresh(0.0)
, highGoalRefresh(0.0)
, lookingForPeg(false)
, highGoalFinder(new HighGoalFinder())
, pegFinder(new PegFinder())
, debug(false)
, ip()
{
	ConfigReader reader(CONFIG_FILE);

	debug = reader.GetBool("DEBUG", DEBUG);
	ip = reader.GetString("IP", IP);
}

MasterControl::~MasterControl()
{
}

void MasterControl::Initialize()
{
	NetworkTable::SetClientMode();
	NetworkTable::SetIPAddress(llvm::StringRef(ip));
	targetDataTable = NetworkTable::GetTable(TARGET_DATA_TABLE_NAME);
	targetDataTable->AddTableListener(this, true);
}

void MasterControl::Run()
{
	while (true)
	{
		TargetFinderIF* finderToUse = nullptr;
		const std::string* key = nullptr;
		double* refreshToUse = nullptr;

		if (lookingForPeg)
		{
			finderToUse = pegFinder.get();
			key = &PEG_KEY;
			refreshToUse = &pegRefresh;
		}
		else
		{
			finderToUse = highGoalFinder.get();
			key = &HIGH_GOAL_KEY;
			refreshToUse = &highGoalRefresh;
		}

		if (finderToUse && key && refreshToUse)
		{
			std::array<double, DATA_ARRAY_SIZE> outputData;
			llvm::ArrayRef<double> aR(outputData.data(), outputData.size());

			finderToUse->Open();

			auto targetData = finderToUse->GetTargetData();
			outputData[0] = targetData.angle;
			outputData[1] = targetData.distance;
			outputData[2] = *refreshToUse;
			targetDataTable->PutNumberArray(*key, aR);

			if (debug)
			{
				std::cout << "start ----------" << std::endl
				          << "angle: " << targetData.angle << std::endl
				          << "distance: " << targetData.distance << std::endl
				          << "end ------------" << std::endl;
			}

			if (*refreshToUse >= MAX_REFRESH_DOUBLE)
			{
				*refreshToUse = 0.0;
			}
			else
			{
				*refreshToUse += 1.0;
			}
		}
	}
}

void MasterControl::ValueChanged(ITable*, llvm::StringRef key, std::shared_ptr<nt::Value> value, bool b)
{
	if (key.str() == MODE_KEY)
	{
		lookingForPeg = value->GetBoolean();

		if (debug)
		{
			std::cout << "Table value changed, lookingForPeg = " << lookingForPeg << "." << std::endl;
		}
	}	
}

