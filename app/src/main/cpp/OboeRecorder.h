//
// Created by chees on 9/18/2022.
//
#include <oboe/Oboe.h>
#ifndef MULTITRACKRECORDER_OBOERECORDER_H
#define MULTITRACKRECORDER_OBOERECORDER_H
class OboeRecorder: public oboe::AudioStreamCallback{
protected:
    oboe::ManagedStream outStream;
    oboe::AudioStream *stream{};
    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) override;
    static OboeRecorder *singleton;
    explicit OboeRecorder();
public:
    static OboeRecorder* get();
    bool isRecording = true;
    void StopAudioRecorder();
    void StartAudioRecorder(const char *path, const int freq);

};

#endif //MULTITRACKRECORDER_OBOERECORDER_H
