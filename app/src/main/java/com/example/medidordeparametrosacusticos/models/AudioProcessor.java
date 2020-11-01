package com.example.medidordeparametrosacusticos.models;

import com.example.medidordeparametrosacusticos.util.MyMaths;

import java.util.ArrayList;

public class AudioProcessor {
    private OctaveBandFilter octaveBandFilter = new OctaveBandFilter();
    private double[][] reverbTimes;
    private double[] coeff = new double[2];
    ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>(2);

    public void process(final double[] impulse) {

        reverbTimes = new double[9][4];
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
    }

    public double[][] getReverbTimes() {
        return reverbTimes;
    }
}
