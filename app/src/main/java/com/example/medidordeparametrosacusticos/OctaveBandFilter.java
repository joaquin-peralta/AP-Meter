package com.example.medidordeparametrosacusticos;

import uk.me.berndporr.iirj.Butterworth;

import static java.lang.StrictMath.sqrt;

public class OctaveBandFilter {
    private int ORDER = 3;
    private double SAMPLE_RATE = 44100d;
    private double MAX_BIT_VALUE = 32767d;
    private double[] centerFrequency = {32d, 63d, 125d, 250d, 500d, 1000d, 2000d, 4000d, 8000d};
    private Butterworth butterworth = new Butterworth();

    public double[][] applyTo(double[] impulse) {
        double[][] filteredImpulses = new double[centerFrequency.length][impulse.length];
        for (int i=0; i < centerFrequency.length; i++) {
            butterworth.bandPass(ORDER, SAMPLE_RATE, centerFrequency[i], widthFrequency(centerFrequency[i]));
            for (int j=0; j < impulse.length; j++) {
                impulse[j] = normalize(impulse[j]);
                filteredImpulses[i][j] = butterworth.filter(impulse[j]);
            }
        }
        return filteredImpulses;
    }

    private double widthFrequency(double centerFrequency) {
        return centerFrequency * (1/sqrt(2));
    }

    public double normalize(double sample) {
        return sample / MAX_BIT_VALUE;
    }
}
