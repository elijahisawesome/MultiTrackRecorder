//
// Created by chees on 9/25/2022.
//

#ifndef MULTITRACKRECORDER_OBOEPLAYBACK_H
#define MULTITRACKRECORDER_OBOEPLAYBACK_H
#include <sndfile.h>


class OboePlayback:oboe::AudioStreamCallback {
protected:
    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) override;
    bool keepPlaying = false;
    SNDFILE *infile, *outfile;
    static OboePlayback* singleton;
    oboe::AudioStream *stream;
public:
    bool startPlayback(const char *path, int freq);
    bool stopPlayback();
    static OboePlayback* get();
};


#endif //MULTITRACKRECORDER_OBOEPLAYBACK_H
