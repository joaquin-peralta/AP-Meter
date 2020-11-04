package com.example.medidordeparametrosacusticos.util;

import java.util.ArrayList;

public class MyTools {

    public static void reverse(double[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            double temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
    }

    public static double maxValue(double[] array) {
        double max = array[0];
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static double minValue(double[] array) {
        double min = array[0];
        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static ArrayList<ArrayList<Double>> asListOfArrays(double[][] matrix) {
        ArrayList<ArrayList<Double>> arrayLists = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> temp = new ArrayList<>();

        for (int i = 0; i < matrix.length ; i++) {
            for (int j = 0; j < matrix[i].length ; j++) {
                temp.add(matrix[i][j]);
            }
            ArrayList<Double> cloned = (ArrayList<Double>) temp.clone();
            arrayLists.add(cloned);
            temp.clear();
        }
        return arrayLists;
    }
}
