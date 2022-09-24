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

    public void record(){
        recordings.writeAudio(mediaRecorder,page,currentTrack);
    }
    public void switchTracks(int newTrack){
        currentTrack = newTrack;
    }

    public String getAudioFilePath(){
        return recordings.getFilePath(page,currentTrack,true);
    }

    public void playAll(){
        /*
        mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(recordingsManager.getAudioFilePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
         */
    }

    //Recording1

    //Recording2

    //Recording3

    //Recording4

    //Total Length

}