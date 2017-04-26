#ifndef TARGET_FINDER_IF_H
#define TARGET_FINDER_IF_H

static const auto DEGREES_PER_PIXEL = 0.07984375;
static const auto X_RESOLUTION = 640u;
static const auto Y_RESOLUTION = 460u;

class TargetFinderIF
{
public:
	struct TargetData
	{
		double angle = -1.0;
		double distance = -1.0;
	};

	virtual ~TargetFinderIF() = default;

	virtual void Open() = 0;
	virtual TargetData GetTargetData() = 0;
	virtual void Release() = 0;
};


#endif
