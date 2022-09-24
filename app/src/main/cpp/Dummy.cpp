#include <jni.h>
#include <string>
#include <oboe/Oboe.h>
#include <fstream>
#include <android/log.h>
#include "OboeRecorder.h"

//Yoinked from https://github.com/reuniware/OboeRecorder

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"AUDIO-APP", __VA_ARGS__)

namespace little_endian_io
{
    template <typename Word>
    std::ostream& write_word( std::ostream& outs, Word value, unsigned size = sizeof( Word ) )
    {
        for (; size; --size, value >>= 8)
            outs.put( static_cast <char> (value & 0xFF) );
        return outs;
    }
}
using namespace little_endian_io;

class OboeRecorder: public oboe::AudioStreamCallback{
protected:
    oboe::ManagedStream outStream;
    oboe::AudioStream *stream{};

    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) override {
        LOGE("onAudioReady");
        if(this->isRecording){
            int *output = (int *)audioData;
            return oboe::DataCallbackResult::Continue;
        }
        return oboe::DataCallbackResult::Stop;
    }


    static OboeRecorder* singleton;
    explicit OboeRecorder() = default;

public:
    static OboeRecorder *get() {
        if (singleton == nullptr)
            singleton = new OboeRecorder();
        return singleton;
    }

    bool isRecording = true;

    void StopAudioRecorder() {
        this->isRecording = false;
    }
    void StartAudioRecorder(const char *path, const int freq)
    {
        oboe::AudioStreamBuilder builder;
        builder.setDeviceId(0);
        builder.setDirection(oboe::Direction::Input);
        builder.setPerformanceMode(oboe::PerformanceMode::None);
        builder.setSampleRate(freq);
        builder.setSharingMode(oboe::SharingMode::Shared);
        builder.setCallback(this);

        int sampleRate = freq;
        int bitsPerSample = 16;
        int numChannels = 1;// 2= stereo

        std::ofstream f;

        f.open(path, std::ios::binary);
        // Write the file headers
        f << "RIFF----WAVEfmt ";     // (chunk size to be filled in later)
        write_word( f,     16, 4 );  // no extension data
        write_word( f,      1, 2 );  // PCM - integer samples
        write_word( f,      numChannels, 2 );  // one channel (mono) or two channels (stereo file)
        write_word( f,  freq, 4 );  // samples per second (Hz)
        //write_word( f, 176400, 4 );  // (Sample Rate * BitsPerSample * Channels) / 8
        write_word( f,(freq * bitsPerSample * numChannels) / 8, 4 );  // (Sample Rate * BitsPerSample * Channels) / 8
        write_word( f,      4, 2 );  // data block size (size of two integer samples, one for each channel, in bytes)
        write_word( f,     bitsPerSample, 2 );  // number of bits per sample (use a multiple of 8)

        // Write the data chunk header
        size_t data_chunk_pos = f.tellp();
        f << "data----";  // (chunk size to be filled in later)

        oboe::AudioStream *stream;
        oboe::Result r = builder.openStream(&stream);

        if (r != oboe::Result::OK) {
            return;
        }

        r = stream->requestStart();
        if (r != oboe::Result::OK) {
            return;
        }
        auto a = stream->getState();
        if (a == oboe::StreamState::Started) {

            constexpr int kMillisecondsToRecord = 2;
            auto requestedFrames = (int32_t) (kMillisecondsToRecord *
                                              (stream->getSampleRate() / oboe::kMillisPerSecond));
            // __android_log_print(ANDROID_LOG_INFO, "OboeRecorder", "requestedFrames = %d", requestedFrames);

            int16_t mybuffer[requestedFrames];
            constexpr int64_t kTimeoutValue = 3 * oboe::kNanosPerMillisecond;

            int framesRead = 0;
            do {
                auto result = stream->read(mybuffer, requestedFrames, 0);
                if (result != oboe::Result::OK) {
                    break;
                }
                framesRead = result.value();
                //__android_log_print(ANDROID_LOG_INFO, "OboeRecorder", "framesRead = %d", framesRead);
                if (framesRead > 0) {
                    break;
                }
            } while (framesRead != 0);

            while (isRecording) {
                auto result = stream->read(mybuffer, requestedFrames, kTimeoutValue * 1000);
                if (result == oboe::Result::OK) {
                    auto nbFramesRead = result.value();
                    //__android_log_print(ANDROID_LOG_INFO, "OboeRecorder", "nbFramesRead = %d", nbFramesRead);
                    for (int i = 0; i <
                                    nbFramesRead; i++) {// __android_log_print(ANDROID_LOG_INFO, "OboeRecorder","nbFramesRead[%d] = %d", i, mybuffer[i]);
                        write_word(f, (int) (mybuffer[i]), 2);
                        //write_word( f, (int)(mybuffer[i]), 2 ); // If stereo recording, add this line and write mybuffer[i+1] ?
                    }
                } else {
                    auto error = convertToText(result.error());
                    //__android_log_print(ANDROID_LOG_INFO, "OboeRecorder", "error = %s", error);
                }
            }

            stream->requestStop();
            stream->close();

            // (We'll need the final file size to fix the chunk sizes above)
            size_t file_length = f.tellp();

            // Fix the data chunk header to contain the data size
            f.seekp(data_chunk_pos + 4);
            write_word(f, file_length - data_chunk_pos + 8);

            // Fix the file header to contain the proper RIFF chunk size, which is (file size - 8) bytes
            f.seekp(0 + 4);
            write_word(f, file_length - 8, 4);
            f.close();
        }
    }
};

OboeRecorder *OboeRecorder::singleton = nullptr;

