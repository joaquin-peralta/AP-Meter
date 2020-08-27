package com.example.medidordeparametrosacusticos.util;

import java.util.ArrayList;

import static com.example.medidordeparametrosacusticos.util.MyTools.reverse;

public class MyMaths {
    private static double SAMPLE_RATE = 44100.0;

    public static double calculateRT(double slope) {
        return Math.floor((-60.0/slope)*1000) / 1000;
    }

    public static double polyfit(double[][] matrix) {
        double t_Mean = 0;
        double Ls_Mean = 0;
        for (int i = 0; i < matrix[0].length; i++) {
            t_Mean += matrix[0][i];
            Ls_Mean += matrix[1][i];
        }
        t_Mean = t_Mean / matrix[0].length;
        Ls_Mean = Ls_Mean / matrix[0].length;

        double tn = 0;
        double Ls = 0;
        double numerator = 0;
        double denominator = 0;
        for (int i = 0; i < matrix[0].length; i++) {
            tn = matrix[0][i];
            Ls = matrix[1][i];
            numerator += ((tn - t_Mean) * (Ls - Ls_Mean));
            denominator += ((tn - t_Mean) * (tn - t_Mean));
        }
        return numerator / denominator; // slope
    }

    public static double[] schroederIntegration(double[] impulse) {
        for (int i = 0; i < impulse.length; i++) {
            impulse[i] = impulse[i] * impulse[i];
        }
        reverse(impulse);
        double[] decayCurve = cumtrapz(impulse);
        reverse(decayCurve);
        return decayCurve;
    }

    public static double[] cumtrapz(double[] buffer) {
        double deltaX = (buffer.length / SAMPLE_RATE) / (buffer.length - 1); // deltaX = (b-a) / N-1
        double[] cumulation = new double[buffer.length];
        for (int i = 1; i < buffer.length; i++) {
            cumulation[i] = cumulation[i-1] + ((buffer[i] + buffer[i-1]) / 2) * deltaX;
        }
        return cumulation;
    }

    public static double calculateRMS(double[] buffer) {
        double rms = 0.0;
        for (int i = 0; i < buffer.length; i++) {
            rms += buffer[i] * buffer[i];
        }
        rms = rms / buffer.length;
        rms = Math.sqrt(rms);
        return rms;
    }

    public static double[] convertToDecibel(double[] curve) {
        double[] normalizeCurve = new double[curve.length];
        double max = MyTools.maxValue(curve);
        for (int i = 0; i < curve.length; i++) {
            normalizeCurve[i] = 10 * Math.log10(curve[i]/max);
        }
        return normalizeCurve;
    }

    public static double[][] getDecreaseRange(double[] normalizeCurve, String range) {
        double max = MyTools.maxValue(normalizeCurve);
        double min = MyTools.minValue(normalizeCurve);
        ArrayList<Double> levelList = new ArrayList<Double>();
        ArrayList<Integer> sampleList = new ArrayList<Integer>();

        if (range == "EDT") {
            if (max - min <= 10.0) {return null;}
            double startPoint = max;
            double endPoint = max - 10.0;
            for (int i = 0; i < normalizeCurve.length; i++) {
                if (normalizeCurve[i] <= startPoint && normalizeCurve[i] >= endPoint) {
                    levelList.add(normalizeCurve[i]);
                    sampleList.add(i);
                }
            }

        } else if (range == "10dB") {
            if (max - min <= 15.0) {return null;}
            double startPoint = max - 5.0;
            double endPoint = max - 15.0;
            for (int i = 0; i < normalizeCurve.length; i++) {
                if (normalizeCurve[i] <= startPoint && normalizeCurve[i] >= endPoint) {
                    levelList.add(normalizeCurve[i]);
                    sampleList.add(i);
                }
            }

        } else if (range == "20dB") {
            if (max - min <= 25.0) {return null;}
            double startPoint = max - 5.0;
            double endPoint = max - 25.0;
            for (int i = 0; i < normalizeCurve.length; i++) {
                if (normalizeCurve[i] <= startPoint && normalizeCurve[i] >= endPoint) {
                    levelList.add(normalizeCurve[i]);
                    sampleList.add(i);
                }
            }

        } else if (range == "30dB") {
            if (max - min <= 35.0) {return null;}
            double startPoint = max - 5.0;
            double endPoint = max - 35.0;
            for (int i = 0; i < normalizeCurve.length; i++) {
                if (normalizeCurve[i] <= startPoint && normalizeCurve[i] >= endPoint) {
                    levelList.add(normalizeCurve[i]);
                    sampleList.add(i);
                }
            }
        }

        double[][] matrix = new double[2][sampleList.size()];
        for (int i = 0; i < sampleList.size(); i++) {
            matrix[0][i] = sampleList.get(i) / 44100d;
            matrix[1][i] = levelList.get(i);
        }
        return matrix;
    }
}

