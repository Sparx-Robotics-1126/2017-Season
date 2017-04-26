#!/bin/bash

sleep 15

ls -l /dev/v4l/by-path/platform-tegra-ehci.0-usb-0:1:1.0-video-index0 | tail -c 2 > /home/ubuntu/Development/Vision/config/HighGoalCamera.txt

ls -l /dev/v4l/by-path/platform-tegra-xhci-usb-0:3.1:1.0-video-index0 | tail -c 2 > /home/ubuntu/Development/Vision/config/PegCamera.txt

v4l2-ctl -d 0 --set-ctrl=exposure_auto=1 
v4l2-ctl -d 0 --set-ctrl=exposure_absolute=0 
v4l2-ctl -d 0 --set-ctrl=brightness=0 
v4l2-ctl -d 0 --set-ctrl=contrast=10 
v4l2-ctl -d 0 --set-ctrl=sharpness=0 
v4l2-ctl -d 0 --set-ctrl=saturation=200 
v4l2-ctl -d 0 --set-ctrl=backlight_compensation=0 
v4l2-ctl -d 0 --set-ctrl=pan_absolute=0 
v4l2-ctl -d 0 --set-ctrl=tilt_absolute=0 
v4l2-ctl -d 0 --set-ctrl=zoom_absolute=0
v4l2-ctl -d 0 --set-ctrl=exposure_auto=1      
v4l2-ctl -d 0 --set-ctrl=exposure_absolute=0      
v4l2-ctl -d 0 --set-ctrl=brightness=0      
v4l2-ctl -d 0 --set-ctrl=contrast=10      
v4l2-ctl -d 0 --set-ctrl=sharpness=0      
v4l2-ctl -d 0 --set-ctrl=saturation=200      
v4l2-ctl -d 0 --set-ctrl=backlight_compensation=0      
v4l2-ctl -d 0 --set-ctrl=pan_absolute=0      
v4l2-ctl -d 0 --set-ctrl=tilt_absolute=0       
v4l2-ctl -d 0 --set-ctrl=zoom_absolute=0

v4l2-ctl -d 1 --set-ctrl=exposure_auto=1 
v4l2-ctl -d 1 --set-ctrl=exposure_absolute=0 
v4l2-ctl -d 1 --set-ctrl=brightness=0 
v4l2-ctl -d 1 --set-ctrl=contrast=10 
v4l2-ctl -d 1 --set-ctrl=sharpness=0 
v4l2-ctl -d 1 --set-ctrl=saturation=200 
v4l2-ctl -d 1 --set-ctrl=backlight_compensation=0 
v4l2-ctl -d 1 --set-ctrl=pan_absolute=0 
v4l2-ctl -d 1 --set-ctrl=tilt_absolute=0 
v4l2-ctl -d 1 --set-ctrl=zoom_absolute=0
v4l2-ctl -d 1 --set-ctrl=exposure_auto=1      
v4l2-ctl -d 1 --set-ctrl=exposure_absolute=0      
v4l2-ctl -d 1 --set-ctrl=brightness=0      
v4l2-ctl -d 1 --set-ctrl=contrast=10      
v4l2-ctl -d 1 --set-ctrl=sharpness=0      
v4l2-ctl -d 1 --set-ctrl=saturation=200      
v4l2-ctl -d 1 --set-ctrl=backlight_compensation=0      
v4l2-ctl -d 1 --set-ctrl=pan_absolute=0      
v4l2-ctl -d 1 --set-ctrl=tilt_absolute=0       
v4l2-ctl -d 1 --set-ctrl=zoom_absolute=0

/home/ubuntu/Development/Vision/TargetFinder&

