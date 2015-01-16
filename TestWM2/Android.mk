LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-subdir-java-files) \
	src/org/solarex/wm2service/IWm2SupportService.aidl \
	src/org/solarex/wm2service/IWm2Callback.aidl
LOCAL_PACKAGE_NAME := TestWm2
include $(BUILD_PACKAGE)