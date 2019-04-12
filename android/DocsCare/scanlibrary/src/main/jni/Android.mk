LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#opencv library
# different depend on user environment
# OPENCVROOT:= /Users/kim/Desktop/SimpleScanner/scanlibrary/src/main/jni/sdk
OPENCVROOT:= D:/android_workspace/DocsCare/android/DocsCare/scanlibrary/src/main/jni/sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}/native/jni/OpenCV.mk


LOCAL_MODULE    := Scanner
LOCAL_SRC_FILES := scan.cpp
LOCAL_LDLIBS    += -lm -llog -landroid
LOCAL_LDFLAGS += -ljnigraphics
include $(BUILD_SHARED_LIBRARY)