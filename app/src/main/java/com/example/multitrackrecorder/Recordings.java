package com.example.multitrackrecorder;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaRecorder;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import com.example.multitrackrecorder.Recording;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class Recordings {
    //context management
    private Context context;

    private static final int NUM_OF_STEMS = 4;
    private static final int SUB_STEM_FIELD_COUNT = 3;
    private static final int RECORDING_FREQUENCY = 44100;

    //Array of Recordings, broken up into blocks of 4.
    Recording[] recordingArray = new Recording[4];
    int pagecount;
    int currentPage;
    File[] externalStorageVolumes;
    File primaryExternalStorage;

    private static final Recordings instance = new Recordings();
    public static Recordings getInstance(){
        return instance;
    }
    public void setContext(Context pContext){
        context = pContext;
        externalStorageVolumes = ContextCompat.getExternalFilesDirs(context, null);
        primaryExternalStorage = externalStorageVolumes[0];
    }


    public Recording[] getRecordingArray(int page){

        Recording[] x = {recordingArray[page],recordingArray[page+1],recordingArray[page+2],recordingArray[page+3]};
        return x;
    }

    public Recording[] switchPage(int page)
    {
        Recording[] recordings = new Recording[NUM_OF_STEMS];
        //load new Recording[]
        recordings[0] = readFile(page, 0);
        recordings[1] = readFile(page, 1);;
        recordings[2] = readFile(page, 2);;
        recordings[3] = readFile(page, 3);;
        //return new Recording[]
        recordingArray = recordings;
        return recordings;
    }

    private Recording readFile(int page, int stemNum){
        Recording x = new Recording();
        String[] y = new String[SUB_STEM_FIELD_COUNT];

        File curAudio = new File(primaryExternalStorage.getPath()+"/"+page+"/"+stemNum+"/audio.mp3");
        File curData = new File(primaryExternalStorage.getPath()+"/"+page+"/"+stemNum+"/data.txt");


        try
        {
            int i = 0;
            Scanner dataScanner = new Scanner(curData);
            while (dataScanner.hasNextLine()) {
                y[i] =  dataScanner.nextLine();
                i++;
            }

            x = new Recording(curAudio, Integer.parseInt(y[0]),Boolean.parseBoolean(y[1]),Boolean.parseBoolean(y[2]));
        }
        catch(Exception e){
            e.printStackTrace();
            x = new Recording();
        }

        return x;
    }
    public void PlayAll(){
        startPlayback(primaryExternalStorage.getPath()+"/Music/0/0/audio.wav", RECORDING_FREQUENCY);
    }
    public void StopRecording(){stopRecording();}
    public void StopAll(){
        stopPlayback();
    }
    public void writeAudio(MediaRecorder mr, int page, int stemNum){
        mr.setAudioSource(MediaRecorder.AudioSource.MIC);
        mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mr.setOutputFile(getFilePath(page, stemNum, true));
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            System.out.println(getFilePath(page, stemNum, true));
            mr.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mr.start();
    }
    public void writeData(int page, int stemNum, int loopCount, boolean isMaster){
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        try {
            FileWriter fw = new FileWriter(new File (musicDirectory, (page+"/"+stemNum+"/data.txt") ).getPath());
            String contents = String.valueOf(loopCount) + "\n" +String.valueOf(isMaster) +"\n" +String.valueOf(true);
            fw.write(contents);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getFilePath(int page, int stemNum, boolean audio)
    {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        new File((musicDirectory), (page+"/"+stemNum)).mkdirs();
        if(audio){
            return new File (musicDirectory, (page+"/"+stemNum+"/audio.wav")).getPath();
        }
        else{
            return new File (musicDirectory, (page+"/"+stemNum+"/data.txt") ).getPath();
        }


    }
    public boolean startRecord(int page,int track, int freq){
        return startRecording(getFilePath(page,track, true), freq);
    }
    public void stopRecord(){
        stopRecording();
    }
    private native boolean startRecording(String path,int freq);
    private native boolean stopRecording();
    private native boolean startPlayback(String path, int freq);
    private native boolean stopPlayback();
    /*
    private String getFilePath(){
        ContextWrapper contextWrapper = new ContextWrapper();
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File (musicDirectory,"testRecordingFile.mp3" );
        return file.getPath();
    }
    */
}