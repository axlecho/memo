LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := ndkDrawer
LOCAL_SRC_FILES := \
	E:\github\memo\app\src\main\jni\Android.mk \
	E:\github\memo\app\src\main\jni\Makefile \
	E:\github\memo\app\src\main\jni\ndkDrawer.c \

LOCAL_C_INCLUDES += E:\github\memo\app\src\main\jni
LOCAL_C_INCLUDES += E:\github\memo\app\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
