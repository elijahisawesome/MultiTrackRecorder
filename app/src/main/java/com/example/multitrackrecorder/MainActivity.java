package com.example.multitrackrecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    Button playPauseButton;
    ImageButton trackOneButton;
    ImageButton trackTwoButton;
    ImageButton trackThreeButton;
    ImageButton trackFourButton;
    TextView title;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    //Recordings recordings;
    RecordingsPackager recordingsManager;
    private static final int MICROPHONE_REQUEST_CODE = 100;
    // Default to Stereo (OPTIONS is zero-based array so index 1 = 2 channels)
    private static final int CHANNEL_COUNT_DEFAULT_OPTION_INDEX = 1;
    private static final int[] BUFFER_SIZE_OPTIONS = {0, 1, 2, 4, 8};
    private static final String[] AUDIO_API_OPTIONS = {"Unspecified", "OpenSL ES", "AAudio"};
    private static final int OBOE_API_OPENSL_ES = 1;
    private static final int RECORDING_FREQUENCY = 44100;
    private boolean playingBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playPauseButton = findViewById(R.id.playPauseButton);
        title = findViewById(R.id.title);
        trackOneButton=findViewById(R.id.imageButton);
        trackTwoButton=findViewById(R.id.imageButton2);
        trackThreeButton=findViewById(R.id.imageButton3);
        trackFourButton=findViewById(R.id.imageButton4);


        trackOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                changeTrack(1);
            }
        });
        trackTwoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                changeTrack(2);
            }
        });
        trackThreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                changeTrack(3);
            }
        });
        trackFourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                changeTrack(4);
            }
        });

        if (checkMic()){
            getMicPermission();
        }
        System.loadLibrary("native-lib");
    }
    @Override
    protected void onStart() {
        recordingsManager = new RecordingsPackager();
        recordingsManager.init(getApplicationContext());

        title.setText("Title" + getInt());



        super.onStart();
    }


    public void clickPlay(View view){
        if(playingBack){
            playingBack = false;
            stopBkgThread();
            return;
        }
        if(!playingBack) {
            playingBack = true;
            playBkgThread();
            return;
        }
    }
    private void playBkgThread(){
        Runnable myRunnable = new Runnable(){
            @Override
            public void run(){
                try{
                    recordingsManager.playAll();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Thread begin = new Thread(myRunnable);
        begin.start();
    }
    private void stopBkgThread(){
        Runnable myRunnable = new Runnable(){
            @Override
            public void run(){
                try{
                    recordingsManager.stopPlayback();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Thread begin = new Thread(myRunnable);
        begin.start();
    }
    public void clickRecord(View view){
        Runnable myRunnable = new Runnable(){
            @Override
            public void run(){
                try{
                    //System.out.println(getFilePath());
                    //recordingsManager.record(getFilePath(),RECORDING_FREQUENCY);
                    //Toast.makeText(this, "Recording...", Toast.LENGTH_LONG).show();
                    recordingsManager.record(RECORDING_FREQUENCY);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Thread begin = new Thread (myRunnable);
        begin.start();


    }

    public void clickStopRecording(View view){
        /*
        if(mediaRecorder != null){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            //recordings.writeData(1,1,1,true);
            title.setText("Stopped Recording");

            Toast.makeText(this, "Stopped Recording", Toast.LENGTH_LONG).show();
        }*/

        Runnable myRunnable = new Runnable(){
            @Override
            public void run(){
                try{
                    recordingsManager.stopRecording();
                    //recordingsManager.record();

                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Thread begin = new Thread (myRunnable);
        begin.start();
    }
    private boolean checkMic(){
        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return true;
        }
        return false;
    }
    private void getMicPermission(){
        System.out.println("Getting Mic Permission");
        if(ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MICROPHONE_REQUEST_CODE);
        }
    }
    private String getFilePath(){

        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File (musicDirectory,"testRecordingFile.wav" );
        //new File(musicDirectory,(1+"/"+1)).mkdirs();
        return file.getPath();
    }

    public void changeTrack(int x){
        recordingsManager.switchTracks(x);
    }

    //private native boolean startRecording(String path,int freq);
    //private native boolean stopRecording();
    private native int getInt();

}
