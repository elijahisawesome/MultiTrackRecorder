package com.example.multitrackrecorder;

import java.io.File;

public class Recording {
    public Recording(File location, int numOfLoops, boolean mLoop, boolean real){
        audio = location;
        loopCount = numOfLoops;
        masterLoop= mLoop;
        present = real;
    }
    public Recording()
    {
        present = false;
    }
    //Path to Audio
    public File audio;
    //Loop count
    public int loopCount;
    //Master Loop?
    public boolean masterLoop;
    //Present?
    public boolean present;
}
