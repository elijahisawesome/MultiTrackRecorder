#include <jni.h>
#include <string>
#include <iostream>
#include <oboe/Oboe.h>
#include <android/log.h>
#include <thread>
#include "OboeRecorder.h"
#include "OboePlayback.h"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"AUDIO-APP", __VA_ARGS__)

using namespace oboe;
extern "C" JNIEXPORT jint JNICALL
Java_com_example_multitrackrecorder_MainActivity_getInt(JNIEnv *env, jobject thiz){
    return 10;
}
/*
extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_multitrackrecorder_MainActivity_startRecording(
        JNIEnv * env,
        jobject MainActivity,
        jstring jStringPath,
        jint freq){

    const char *path = (*env).GetStringUTFChars(jStringPath, 0);
    static auto a = OboeRecorder::get();
    a->StartAudioRecorder(path, (int)(freq));
    return true;
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_example_multitrackrecorder_MainActivity_stopRecording(
        JNIEnv * env,
        jobject MainActivity
        ){
    static auto a = OboeRecorder::get();
    a->StopAudioRecorder();
    return true;
}
*/


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_multitrackrecorder_Recordings_startRecording(JNIEnv *env, jobject thiz,
                                                              jstring jStringPath, jint freq) {

        const char *path = (*env).GetStringUTFChars(jStringPath, 0);
        static auto a = OboeRecorder::get();
        a->StartAudioRecorder(path, (int)(freq));
        return true;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_multitrackrecorder_Recordings_stopRecording(JNIEnv *env, jobject thiz) {
    static auto a = OboeRecorder::get();
    a->StopAudioRecorder();
    return true;
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_multitrackrecorder_Recordings_startPlayback(JNIEnv *env, jobject thiz,
                                                             jstring path, jint freq) {
    static auto a = OboePlayback::get();
    const char *realPath = (*env).GetStringUTFChars(path, 0);
    a->startPlayback(realPath, freq);
    return true;
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_multitrackrecorder_Recordings_stopPlayback(JNIEnv *env, jobject thiz) {
    static auto a = OboePlayback::get();
    return a->stopPlayback();
}