#include "MasterControl.h"

/*
#include <stdio.h>
#include <execinfo.h>
#include <signal.h>
#include <stdlib.h>
#include <unistd.h>
*/

// g++ -I ./include -pthread -std=c++11 ./*.cpp ./src/*.cpp -o TargetFinder -lopencv_imgproc -lopencv_highgui -lopencv_core
// -lopencv_core -lopencv_imgproc -lopencv_highgui -lopencv_calib3d -lopencv_contrib -lopencv_features2d -lopencv_flann -lopencv_gpu -lopencv_legacy -lopencv_ml -lopencv_objdetect -lopencv_photo -lopencv_stitching -lopencv_superres -lopencv_video -lopencv_videostab
// g++ -I ./include -pthread -std=c++11 ./*.cpp ./src/*.cpp -o TargetFinder -lopencv_core -lopencv_imgproc -lopencv_highgui -lopencv_calib3d -lopencv_contrib -lopencv_features2d -lopencv_flann -lopencv_gpu -lopencv_legacy -lopencv_ml -lopencv_objdetect -lopencv_photo -lopencv_stitching -lopencv_superres -lopencv_video -lopencv_videostab

/*
void Handler(int sig)
{
	void* array[10];
	size_t size;

	size = backtrace(array, 10);

	fprintf(stderr, "Error: signal %d:\n", sig);
	backtrace_symbols_fd(array, size, STDERR_FILENO);
	exit(1);
}
*/

int main()
{
/*
	signal(SIGSEGV, Handler);
*/
	MasterControl mc;
	mc.Initialize();
	mc.Run();
	return 0;
}
