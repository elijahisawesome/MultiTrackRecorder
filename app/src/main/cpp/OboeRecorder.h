//
// Created by chees on 9/18/2022.
//


#ifndef MULTITRACKRECORDER_OBOERECORDER_H
#define MULTITRACKRECORDER_OBOERECORDER_H
#include <oboe/Oboe.h>
#include "sndfile.h"
class OboeRecorder: public oboe::AudioStreamCallback{
protected:
    oboe::ManagedStream outStream;
    oboe::AudioStream *stream{};
    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) override;
    static OboeRecorder *singleton;
    explicit OboeRecorder();
    SNDFILE *infile, *outfile;
public:
    static OboeRecorder* get();
    bool isRecording = true;
    void StopAudioRecorder();
    void StartAudioRecorder(const char *path, const int freq);

};

#endif //MULTITRACKRECORDER_OBOERECORDER_H
