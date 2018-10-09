// Copyright (c) 2012, Google Inc.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of Google Inc. nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

#include <stdio.h>

#include "client/linux/handler/exception_handler.h"
#include "client/linux/handler/minidump_descriptor.h"
#include "com_breakpad_nativecreash_NativeCrash.h"
#include <string.h>
#include <jni.h>
#include <android/log.h>

#define LOG_TAG "TESTCRASHTAG"
//#define LOG_PRINT(level,fmt,...) __android_log_print(level,LOG_TAG,"(%s:%u) %s: " fmt,__FILE__,__LINE__,__PRETTY_FUNCTION__,##__VA_ARGS__)
#define LOG_PRINT(level, fmt, ...) __android_log_print(level,LOG_TAG,fmt,##__VA_ARGS__)
#define LOGD(fmt, ...) LOG_PRINT(ANDROID_LOG_DEBUG,fmt ,##__VA_ARGS__)
JavaVM *gJvm = NULL;
jobject globalobj;

void callJava(JNIEnv *env, const char *path) {
    LOGD("callJava");
    jclass clazz = env->GetObjectClass(globalobj);
    LOGD("callJava:jclass");
    jstring jstring1 = env->NewStringUTF(path);
    jmethodID mid = env->GetStaticMethodID(clazz, "onCrash", "(Ljava/lang/String;)V");
    LOGD("callJava:jmethodID");
    env->CallStaticVoidMethod(clazz, mid, jstring1);
    LOGD("callJava:end");
}

namespace {

    bool DumpCallback(const google_breakpad::MinidumpDescriptor &descriptor,
                      void *context,
                      bool succeeded) {
        LOGD("DumpCallback:path=%s", descriptor.path());
        JNIEnv *env;
//        gJvm->GetEnv((void **) &env, JNI_VERSION_1_6);
        gJvm->AttachCurrentThread(&env, NULL);
        LOGD("DumpCallback:env=%p", env);
        callJava(env, descriptor.path());
        return succeeded;
    }
}  // namespace

void Crash() {
    volatile int *a = reinterpret_cast<volatile int *>(NULL);
    *a = 1;
}

void *CrashThread(void *v) {
    JNIEnv *env;
//        gJvm->GetEnv((void **) &env, JNI_VERSION_1_6);
    gJvm->AttachCurrentThread(&env, NULL);
    LOGD("CrashThread:env=%p", env);
    volatile int *a = reinterpret_cast<volatile int *>(NULL);
    *a = 1;
}

google_breakpad::ExceptionHandler *exceptionHandler;

int init(const char *path) {
    if (exceptionHandler != NULL) {
        return 1;
}
    google_breakpad::MinidumpDescriptor descriptor(path);
//    google_breakpad::ExceptionHandler eh(descriptor, NULL, DumpCallback,
//                                         NULL, true, -1);
//    exceptionHandler=&eh;
    exceptionHandler = new google_breakpad::ExceptionHandler(descriptor, NULL, DumpCallback,
                                                             NULL, true, -1);
    bool  installed=exceptionHandler->InitSuccess();
    LOGD("init:installed=%d",installed);
    if(!installed){
        delete exceptionHandler;
        exceptionHandler=NULL;
    }
    return installed;
}

//int mainTracePath(int argc, char **argv);
JNIEXPORT jint JNICALL
Java_com_breakpad_nativecreash_NativeCrash_init(JNIEnv *env, jobject obj,
                                                jstring path) {
    LOGD("NativeCrash_init");
    env->GetJavaVM(&gJvm);
    globalobj = env->NewGlobalRef(obj);
    const char *c_path = env->GetStringUTFChars(path, NULL);
    int installed=init(c_path);
    LOGD("NativeCrash_init:installed=%d",installed);
    return installed;
//    callJava(env);
}

JNIEXPORT void JNICALL
Java_com_breakpad_nativecreash_NativeCrash_crash(JNIEnv *env, jobject obj) {
    LOGD("NativeCrash_crash:exceptionHandler=%p", exceptionHandler);
    Crash();
}

JNIEXPORT void JNICALL
Java_com_breakpad_nativecreash_NativeCrash_crashThread(JNIEnv *env, jobject obj) {
    LOGD("NativeCrash_crashThread:exceptionHandler=%p", exceptionHandler);
    pthread_t star_location_1;
    pthread_create(&star_location_1, NULL, CrashThread, NULL);
}