/*
 * tracepath.c
 *
 *		This program is free software; you can redistribute it and/or
 *		modify it under the terms of the GNU General Public License
 *		as published by the Free Software Foundation; either version
 *		2 of the License, or (at your option) any later version.
 *
 * Authors:	Alexey Kuznetsov, <kuznet@ms2.inr.ac.ru>
 */

#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/socket.h>
#include <linux/types.h>
#include <linux/errqueue.h>
#include <errno.h>
#include <string.h>
#include <netdb.h>
#include <netinet/in.h>
#include <resolv.h>
#include <sys/time.h>
#include <sys/uio.h>
#include <arpa/inet.h>

#ifdef USE_IDN
#include <idna.h>
#include <locale.h>
#endif

#include <stdarg.h>
#include "com_netease_LDNetDiagnoService_LDNetTraceRoute.h"
#include <string.h>
#include <jni.h>
#include <android/log.h>

/**
 * java调用c中的tracePath方法，
 */
JavaVM *gJvm = NULL;
int isFirst;
//int mainTracePath(int argc, char **argv);
JNIEXPORT void JNICALL
Java_com_netease_LDNetDiagnoService_LDNetTraceRoute_startJNICTraceRoute(JNIEnv *env, jobject obj,
                                                                        jstring command) {
    __android_log_print(ANDROID_LOG_INFO, "JNIMsg", "===============begin=====================");
    (*env)->GetJavaVM(env, &gJvm);
    (*gJvm)->AttachCurrentThread(gJvm, &env, NULL);
    isFirst = 1;
    int *a = NULL;
    *a = 1;
}
