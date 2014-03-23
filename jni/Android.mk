LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := ndkDrawer 
LOCAL_SRC_FILES := ndkDrawer.c
LOCAL_LDLIBS    := -ljnigraphics -llog -lm
include $(BUILD_SHARED_LIBRARY)



