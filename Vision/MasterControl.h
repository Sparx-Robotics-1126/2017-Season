#include "tables/ITableListener.h"

#include <memory>

class NetworkTable;
class TargetFinderIF;

class MasterControl : public ITableListener
{
public:
	MasterControl();
	virtual ~MasterControl();

	void Initialize();
	void Run();
private:
	virtual void ValueChanged(ITable* source, llvm::StringRef key, std::shared_ptr<nt::Value> value, bool isNew) override;

	double pegRefresh;
	double highGoalRefresh;

	// Config
	bool debug;
	std::string ip;

	// Current locating mode.
	bool lookingForPeg;

	// Table hosted on the Jetson that distributes target data.
	std::shared_ptr<NetworkTable> targetDataTable;

	// Target locating classes.
	std::unique_ptr<TargetFinderIF> highGoalFinder;
	std::unique_ptr<TargetFinderIF> pegFinder;
};

