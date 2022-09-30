package com.example.multitrackrecorder;


import android.content.Context;
import android.media.MediaRecorder;

public class RecordingsPackager {
    Recordings recordings;
    MediaRecorder mediaRecorder;
    int page;
    int currentTrack;

    public void init(Context context)
    {
        recordings = Recordings.getInstance();
        recordings.setContext(context);
    }

    public void record(int RECORDING_FREQUENCY){
        recordings.startRecord(page,currentTrack,RECORDING_FREQUENCY);
    }
    public void stopRecording(){recordings.StopRecording();}
    public void switchTracks(int newTrack){
        currentTrack = newTrack;
    }

    public String getAudioFilePath(){
        return recordings.getFilePath(page,currentTrack,true);
    }

    public void playAll(){recordings.PlayAll();}
    public void stopPlayback(){recordings.StopAll();}

    //Recording1

    //Recording2

    //Recording3

    //Recording4

    //Total Length

}
