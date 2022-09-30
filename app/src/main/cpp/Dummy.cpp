//
// Created by chees on 9/25/2022.
//
#include <jni.h>
#include <string>
#include <oboe/Oboe.h>
#include <fstream>
#include <android/log.h>
#include "OboePlayback.h"
#include "sndfile.h"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"AUDIO-APP", __VA_ARGS__)



bool OboePlayback::startPlayback(const char *path, int freq) {
    keepPlaying = true;

    oboe::AudioStreamBuilder builder;
    builder.setDeviceId(0);
    builder.setDirection(oboe::Direction::Output);
    builder.setPerformanceMode(oboe::PerformanceMode::None);
    builder.setFormat(oboe::AudioFormat::I16);
    builder.setChannelCount(oboe::ChannelCount::Mono);
    builder.setSampleRate(freq);
    builder.setSharingMode(oboe::SharingMode::Shared);
    builder.setAudioApi(oboe::AudioApi::OpenSLES);
    builder.setCallback(this);


    oboe::Result result  = builder.openStream(&stream);

    if(result != oboe::Result::OK) { return false; }

    result = stream->requestStart();
    auto a = stream->getState();

    SF_INFO sfinfo;
    sfinfo.channels=1;
    sfinfo.samplerate=48000;
    sfinfo.format = SF_FORMAT_WAV;
    int readcount;
    const char *infilename = path;

    //memset(&sfinfo, 0, sizeof(sfinfo));


    if(!(infile = sf_open (infilename, SFM_READ, &sfinfo))){
        LOGE("Error Opening File");
        LOGE("%s", infilename);
    }

    //float data [stream->getBufferCapacityInFrames()];
    /*
    if (a == oboe::StreamState::Started) {
        while(keepPlaying){
            sf_read_float(infile,data,1024);
            result = stream->write(data,);
        }
    }*/


    return true;
}
OboePlayback* OboePlayback::singleton;
OboePlayback* OboePlayback::get(){
    if (singleton == nullptr){
        singleton = new OboePlayback();
    }
    return singleton;
}
bool OboePlayback::stopPlayback(){
    stream->requestStop();
    stream->close();
    sf_close(infile);
    singleton = nullptr;
    keepPlaying = false;
    return true;
}

oboe::DataCallbackResult OboePlayback::onAudioReady(oboe::AudioStream *oboeStream, void *audioData,int32_t numFrames) {
    //__android_log_print(ANDROID_LOG_INFO, "OboePlayback", "numFrames = %d", numFrames);
    /*for(int i = 0; i < numFrames; i++){
        reinterpret_cast<int *>(audioData)[i]*=50000;
    }
    float *dataPtr = reinterpret_cast<float *>(audioData);
    oboe::convertPcm16ToFloat(reinterpret_cast<int16_t *> (audioData),dataPtr,numFrames);
     */
    sf_read_int(infile, reinterpret_cast<int *>(audioData), numFrames);


    return oboe::DataCallbackResult::Continue;



}