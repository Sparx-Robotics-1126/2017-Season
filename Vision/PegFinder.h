#ifndef PEG_FINDER_H
#define PEG_FINDER_H

#include "TargetFinderIF.h"

#include <memory>

class RectangleFinderIF;

class PegFinder : public TargetFinderIF
{
public:
	PegFinder();
private:
	virtual void Open() override;
	virtual TargetData GetTargetData() override;
	virtual void Release() override;

	std::unique_ptr<RectangleFinderIF> rectangleFinder;
};

#endif
