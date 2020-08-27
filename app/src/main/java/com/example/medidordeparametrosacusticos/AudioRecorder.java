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

import com.example.medidordeparametrosacusticos.fragments.RecordFragment;
import com.example.medidordeparametrosacusticos.util.MyMaths;

import java.util.ArrayList;

public class AudioRecorder {
    private AudioRecord recorder = null;
    private int SAMPLE_RATE = 44100;
    private int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private int AUDIO_SOURCE = MediaRecorder.AudioSource.UNPROCESSED;
    private int bufferSize;

    private volatile boolean isInitialized = false;
    private volatile boolean isRecording = true;
    private boolean isLoud = false;
    private boolean isChecked = false;
    private double noiseFloor = 10.0;

    private Thread recordingThread = null;
    private ArrayList<Short> audioDataList = new ArrayList<Short>();
    private Handler recordingThreadHandler = new Handler((Looper.getMainLooper()));

    public void startRecording() {
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        recorder = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);
        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                recorder.startRecording();
                isInitialized = true;
                int bytesCounter = 0;
                short[] audioData = new short[bufferSize/2];

                while (isRecording) {
                    int readSize = recorder.read(audioData, 0, bufferSize/2, AudioRecord.READ_BLOCKING);

                    if (readSize > 0) {
                        checkLevel(audioData);
                        if (isLoud) {
                            bytesCounter += readSize;
                            for (int i = 0; i < audioData.length; i++) {
                                audioDataList.add(audioData[i]);
                            }
                            increaseProgressBar(bytesCounter);
                            if (bytesCounter > 141000) { // duración del proceso: 3 segundos ~
                                recorder.stop();
                                recorder.release();
                                recorder = null;
                                isRecording = false;
                                isInitialized = false;
                                recordingThreadHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        RecordFragment.binding.progressBar.setVisibility(View.INVISIBLE);
                                        RecordFragment.binding.btnRecord.setEnabled(true);
                                        RecordFragment.binding.btnRecord.setBackgroundTintList(
                                                ColorStateList.valueOf(Color.parseColor("#49B675"))); // color verde
                                        RecordFragment.binding.btnRecord.setImageResource(R.drawable.ic_mic_white);

                                    }
                                });
                                new AudioProcessor().process(getAudioData());
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
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    public void stopRecording() {
        if (isRecording) {
            isRecording = false;
            isInitialized = false;
            try {
                recordingThread.join();
            } catch (InterruptedException e) {
                Log.e("Stop recording", "Stop thread error");
            }
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkLevel(short[] audioData) {
        if (isChecked == true) {return;}
        double[] doubleData = new double[audioData.length];
        for (int i = 0; i < audioData.length ; i++) {
            doubleData[i] = audioData[i];
        }
        double rms = MyMaths.calculateRMS(doubleData);
        if (rms > noiseFloor * 50.0) {
            isLoud = true;
            isChecked = true;
            recordingThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    RecordFragment.binding.btnRecord.setEnabled(false);
                    RecordFragment.binding.btnRecord.setBackgroundTintList(
                            ColorStateList.valueOf(Color.parseColor("#808080")));
                    RecordFragment.binding.progressBar.setVisibility(View.VISIBLE);
                }
            });
            return;
        }
    }

    public double[] getAudioData() {
        double[] audioData = new double[audioDataList.size()];
        for (int i = 0; i < audioDataList.size(); i++) {
            audioData[i] = audioDataList.get(i);
        }
        return audioData;
    }

    public boolean hasInitialized() {
        return  isInitialized;
    }

    private void increaseProgressBar(final int progress) {
        Thread progressBarThread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
                    recordingThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            RecordFragment.binding.progressBar.setProgress(progress);
                        }
                    });
                }
            });
        progressBarThread.start();
    }
}