package com.example.medidordeparametrosacusticos.util;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.ArrayList;

import static com.example.medidordeparametrosacusticos.util.MyTools.reverse;

public class MyMaths {
    private static double SAMPLE_RATE = 44100.0;
    private static ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>(2);

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

    public static ArrayList<ArrayList<Double>> generateMatrix(double[] normalizeCurve, String range) {
        double max = MyTools.maxValue(normalizeCurve);
        double min = MyTools.minValue(normalizeCurve);
        ArrayList<Double> levelList = new ArrayList<Double>();
        ArrayList<Double> sampleList = new ArrayList<Double>();

        switch (range) {
            case "EDT": {
                if (max - min <= 10.0) {
                    return null;
                }
                double startPoint = max;
                double endPoint = max - 10.0;
                for (int i = 0; i < normalizeCurve.length; i++) {
                    if (normalizeCurve[i] <= startPoint && normalizeCurve[i] >= endPoint) {
                        levelList.add(normalizeCurve[i]);
                        sampleList.add((double) i);
                    }
                }

                break;
            }
            case "10dB": {
                if (max - min <= 15.0) {
                    return null;
                }
                double startPoint = max - 5.0;
                double endPoint = max - 15.0;
                for (int i = 0; i < normalizeCurve.length; i++) {
                    if (normalizeCurve[i] <= startPoint && normalizeCurve[i] >= endPoint) {
                        levelList.add(normalizeCurve[i]);
                        sampleList.add((double) i);
                    }
                }

                break;
            }
            case "20dB": {
                if (max - min <= 25.0) {
                    return null;
                }
                double startPoint = max - 5.0;
                double endPoint = max - 25.0;
                for (int i = 0; i < normalizeCurve.length; i++) {
                    if (normalizeCurve[i] <= startPoint && normalizeCurve[i] >= endPoint) {
                        levelList.add(normalizeCurve[i]);
                        sampleList.add((double) i);
                    }
                }

                break;
            }
            case "30dB": {
                if (max - min <= 35.0) {
                    return null;
                }
                double startPoint = max - 5.0;
                double endPoint = max - 35.0;
                for (int i = 0; i < normalizeCurve.length; i++) {
                    if (normalizeCurve[i] <= startPoint && normalizeCurve[i] >= endPoint) {
                        levelList.add(normalizeCurve[i]);
                        sampleList.add((double) i);
                    }
                }
                break;
            }
        }

        ArrayList<Double> vectorX = new ArrayList<Double>();
        ArrayList<Double> vectorY = new ArrayList<Double>();

        for (int i = 0; i < sampleList.size() ; i++) {
            vectorX.add(sampleList.get(i) / 44100d);
            vectorY.add(levelList.get(i));
        }
        matrix.add(vectorX);
        matrix.add(vectorY);

        return matrix;
    }

    public static double[] getVectorX(ArrayList<ArrayList<Double>> matrix) {
        double[] vectorX = new double[matrix.get(0).size()];
        for (int i = 0; i < matrix.get(0).size() ; i++) {
            vectorX[i] = matrix.get(0).get(i);
        }
        return vectorX;
    }

    public static double[] getVectorY(ArrayList<ArrayList<Double>> matrix) {
        double[] vectorY = new double[matrix.get(1).size()];
        for (int i = 0; i < matrix.get(1).size() ; i++) {
            vectorY[i] = matrix.get(1).get(i);
        }
        return vectorY;
    }

    public static double[] polyfit(double[] x, double[] y) {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < x.length; i++) {
            obs.add(x[i], y[i]);
        }

        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);
        final double[] coeff = fitter.fit(obs.toList());

        return coeff; /* coeff[0] = ordenada
                         coeff[1] = pendiente */
    }

    public static double calculateRT(double slope) {
        return Math.floor((-60.0/slope)*1000) / 1000;
    }

}

