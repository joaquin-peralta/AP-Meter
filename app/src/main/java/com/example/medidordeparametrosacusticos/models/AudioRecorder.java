package com.example.medidordeparametrosacusticos.models;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.medidordeparametrosacusticos.util.MyMaths;

import java.util.ArrayList;

public class AudioRecorder {
    private AudioRecord recorder = null;
    private int SAMPLE_RATE = 44100;
    private int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private int AUDIO_SOURCE = MediaRecorder.AudioSource.UNPROCESSED;
    private int bufferSize = 0;

    private Thread mRecordingThread;
    private boolean isRecording = false;
    private boolean isLoudEnough = false;
    private boolean isChecked = false;
    private double noiseFloor = 10.0;

    private ArrayList<Short> audioDataList = new ArrayList<Short>();
    private double[] mAudioData;

    public void startRecording() {
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        recorder = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);

        mRecordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                recorder.startRecording();
                int bytesCounter = 0;
                short[] audioData = new short[bufferSize/2];
                isRecording = true;

                while (isRecording) {
                    int readSize = recorder.read(audioData, 0, bufferSize/2, AudioRecord.READ_BLOCKING);

                    if (readSize > 0 && readSize < 564000) {
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
                                setAudioData();
                                new AudioProcessor().process(mAudioData);
                            }
                        }
                    } else if (readSize > 564000) {
                        recorder.stop();
                        recorder.release();
                        recorder = null;
                        isRecording = false;
                        mAudioData = null;

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
        });
        mRecordingThread.start();
    }

    public void stopRecording() {
        if (isRecording) {
            isRecording = false;
            if (mRecordingThread != null) {
                try {
                    mRecordingThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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

    private void setAudioData() {
        mAudioData = new double[audioDataList.size()];
        for (int i = 0; i < audioDataList.size(); i++) {
            mAudioData[i] = audioDataList.get(i);
        }
    }

    public MutableLiveData<Boolean> getStatus() {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        data.postValue(isLoudEnough);
        return data;
    }
}