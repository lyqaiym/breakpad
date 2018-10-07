LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_CFLAGS := -Wno-sign-compare
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
LOCAL_SRC_FILES := tracepath.c
LOCAL_MODULE := tracepath
LOCAL_MODULE_TAGS := debug
#include $(BUILD_EXECUTABLE)
include $(BUILD_SHARED_LIBRARY)


