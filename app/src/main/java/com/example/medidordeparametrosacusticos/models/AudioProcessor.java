package com.example.medidordeparametrosacusticos.models;

import android.os.Process;

import androidx.lifecycle.MutableLiveData;

import com.example.medidordeparametrosacusticos.util.MyMaths;
import com.example.medidordeparametrosacusticos.util.MyTools;

import java.util.ArrayList;

public class AudioProcessor {
    private static AudioProcessor instance;
    private OctaveBandFilter octaveBandFilter = new OctaveBandFilter();
    private double[][] reverbTimes;
    private double[] coeff = new double[2];
    ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>(2);
    private Thread processorThread;
    private MutableLiveData<ArrayList<ArrayList<Double>>> mutableReverbs = new MutableLiveData<>();


    private AudioProcessor() {
    }

    public static AudioProcessor getInstance() {
        if (instance == null) {
            instance = new AudioProcessor();
        }
        return instance;
    }

    public void process(final ArrayList<Short> shorts) {
        processorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

                double[] impulse = new double[shorts.size()];
                for (int i = 0; i < shorts.size(); i++) {
                    impulse[i] = shorts.get(i);
                }

                reverbTimes = new double[9][4];
                ArrayList<ArrayList<Double>> tempMatrix;

                double[][] filteredImpulses = octaveBandFilter.applyTo(impulse);
                for (int i = 0; i < filteredImpulses.length; i++) {
                    double[] decayCurve = MyMaths.schroederIntegration(filteredImpulses[i]);
                    double[] normalizeCurve = MyMaths.convertToDecibel(decayCurve);

                    matrix = MyMaths.generateMatrix(normalizeCurve, "EDT");
                    coeff = MyMaths.polyfit(MyMaths.getVectorX(matrix), MyMaths.getVectorY(matrix));
                    reverbTimes[i][0] = MyMaths.calculateRT(coeff[1]);
                    matrix.clear();

                    matrix = MyMaths.generateMatrix(normalizeCurve, "10dB");
                    coeff = MyMaths.polyfit(MyMaths.getVectorX(matrix), MyMaths.getVectorY(matrix));
                    reverbTimes[i][1] = MyMaths.calculateRT(coeff[1]);
                    matrix.clear();

                    matrix = MyMaths.generateMatrix(normalizeCurve, "20dB");
                    coeff = MyMaths.polyfit(MyMaths.getVectorX(matrix), MyMaths.getVectorY(matrix));
                    reverbTimes[i][2] = MyMaths.calculateRT(coeff[1]);
                    matrix.clear();

                    matrix = MyMaths.generateMatrix(normalizeCurve, "30dB");
                    coeff = MyMaths.polyfit(MyMaths.getVectorX(matrix), MyMaths.getVectorY(matrix));
                    reverbTimes[i][3] = MyMaths.calculateRT(coeff[1]);
                    matrix.clear();
                }
                tempMatrix = MyTools.asListOfArrays(reverbTimes);
                mutableReverbs.postValue(tempMatrix);
            }
        });
        processorThread.start();
    }

    public MutableLiveData<ArrayList<ArrayList<Double>>> getReverbTimes() {
        return mutableReverbs;
    }
}
