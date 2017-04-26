#ifndef HIGH_GOAL_FINDER_H
#define HIGH_GOAL_FINDER_H

#include "TargetFinderIF.h"

#include <memory>

class RectangleFinderIF;

class HighGoalFinder : public TargetFinderIF
{
public:
	HighGoalFinder();
private:
	virtual void Open() override;
	virtual TargetData GetTargetData() override;
	virtual void Release() override;

	std::unique_ptr<RectangleFinderIF> rectangleFinder;

	bool useQuadratic;
};

#endif
