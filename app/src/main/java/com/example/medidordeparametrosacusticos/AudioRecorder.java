package com.example.medidordeparametrosacusticos;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.medidordeparametrosacusticos.databinding.FragmentRecordBinding;
import com.example.medidordeparametrosacusticos.fragments.RecordFragment;
import com.example.medidordeparametrosacusticos.util.MyMaths;

import java.util.ArrayList;

public class AudioRecorder {
    private AudioRecord recorder = null;
    private int SAMPLE_RATE = 44100;
    private int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private int AUDIO_SOURCE = MediaRecorder.AudioSource.UNPROCESSED;
    private int bufferSize = 0;

    private boolean isRecording = false;
    private boolean isLoudEnough = true;
    private boolean isChecked = false;
    private double noiseFloor = 10.0;

    private ArrayList<Short> audioDataList = new ArrayList<Short>();

    public void startRecording() {
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        recorder = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);
        recorder.startRecording();
        int bytesCounter = 0;
        short[] audioData = new short[bufferSize/2];
        isRecording = true;

        while (isRecording) {
            int readSize = recorder.read(audioData, 0, bufferSize/2, AudioRecord.READ_BLOCKING);

            if (readSize > 0) {
                checkLevel(audioData);
                if (isLoudEnough) {
                    bytesCounter += readSize;
                    for (int i = 0; i < audioData.length; i++) {
                        audioDataList.add(audioData[i]);
                    }
                    if (bytesCounter > 141000) { // duración del proceso: 3 segundos ~
                        recorder.stop();
                        recorder.release();
                        recorder = null;
                        isRecording = false;
                    }
                }

            } else if (readSize == AudioRecord.ERROR_INVALID_OPERATION) {
                Log.e("Recording", "Invalid operation error");
                break;

            } else if (readSize == AudioRecord.ERROR_BAD_VALUE) {
                Log.e("Recording", "Bad value error");
                break;

            } else if (readSize == AudioRecord.ERROR) {
                Log.e("Recording", "Unknown error");
                break;
            }
        }
    }

    public void stopRecording() {
        if (isRecording) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private void checkLevel(short[] audioData) {
        if (isChecked) {return;}
        double[] doubleData = new double[audioData.length];
        for (int i = 0; i < audioData.length ; i++) {
            doubleData[i] = audioData[i];
        }
        double rms = MyMaths.calculateRMS(doubleData);
        if (rms > noiseFloor) {
            isLoudEnough = true;
            isChecked = true;
        }
    }

    public double[] getAudioData() {
        double[] audioData = new double[audioDataList.size()];
        for (int i = 0; i < audioDataList.size(); i++) {
            audioData[i] = audioDataList.get(i);
        }
        return audioData;
    }

    public boolean isRecording() {
        return isRecording;
    }
}