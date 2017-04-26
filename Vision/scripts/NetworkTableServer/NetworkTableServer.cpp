#include "tables/ITableListener.h"
#include "networktables/NetworkTable.h"
#include "llvm/StringRef.h"

#include <iostream>
#include <string>

static const std::string TARGET_DATA_TABLE_NAME = "targetData";
static const std::string HIGH_GOAL_KEY = "highGoal";
static const std::string PEG_KEY = "peg";
static const std::string MODE_KEY = "mode";

class NetworkTableServer : public ITableListener
{
public:
	NetworkTableServer() :
	  mode(false)
	, distance(-1.0)
	, angle(-180.0)
	{
		NetworkTable::SetServerMode();
	}

	void Run()
	{
		auto targetDataTable = NetworkTable::GetTable(TARGET_DATA_TABLE_NAME);
		targetDataTable->AddTableListener(this, true);

		std::string input;

		while (true)
		{
			std::cout << "<V> to read values, <M> to switch mode, <Q> to quit." << std::endl;
			std::cin >> input;

			if (input == "q" || input == "Q")
			{
				break;
			}
			else if (input == "m" || input == "M")
			{
				mode = !mode;
				targetDataTable->PutBoolean(MODE_KEY, mode);
			}
			else if (input == "v" || input == "V")
			{
				std::cout << "distance: " << distance << std::endl;
				std::cout << "angle: " << angle << std::endl;
			}

			std::cout << "mode: " << (mode ? "peg" : "high goal") << std::endl;
		}
	}

	virtual void ValueChanged(ITable* source, llvm::StringRef key, std::shared_ptr<nt::Value> value, bool isNew) override
	{
		if (key.str() == HIGH_GOAL_KEY || key.str() == PEG_KEY)
		{
			auto val = value->GetDoubleArray();

			angle = val[0];
			distance = val[1];
		}
	}

private:
	bool mode;
	double distance;
	double angle;
};

int main()
{
	NetworkTableServer nts;
	nts.Run();

	return 0;
}
