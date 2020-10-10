package com.example.medidordeparametrosacusticos.util;

import com.example.medidordeparametrosacusticos.AudioProcessor;
import com.example.medidordeparametrosacusticos.OctaveBandFilter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static com.example.medidordeparametrosacusticos.util.OctaveBandFilterTest.generateImpulse;

public class MyMathsTest {
    private double[] impulse;

    @Before
    public void setUp() throws Exception {
        impulse = generateImpulse(50000);
        OctaveBandFilter octaveBandFilter = new OctaveBandFilter();
        octaveBandFilter.applyTo(impulse);
    }

    @Test
    public void cumtrapzTest() {
        double[] buffer = new double[]{1, 4, 9, 16, 25};
        double[] cumulation = MyMaths.cumtrapz(buffer);
        Assert.assertTrue("impulse[impulse.length] debería ser igual a cero.",cumulation[0] == 0);
        for (int i = 1; i < cumulation.length-1; i++) {
            Assert.assertTrue("Los valores cumulation[i] no crecen como deberían.", cumulation[i] >= cumulation[i-1]);
        }
        for (double d:cumulation) {
            System.out.println(d);
        }
    }

    @Test
    public void schroederIntegrationTest() {
        double[] decayCurve = MyMaths.schroederIntegration(impulse);
        Assert.assertTrue("impulse[impulse.length] debería ser igual a cero.",decayCurve[decayCurve.length-1] == 0);
        for (int i = 0; i < decayCurve.length-1; i++) {
            Assert.assertTrue("Los valores decayCurve[i] no decrecen como deberían.", decayCurve[i] >= decayCurve[i+1]);
        }
    }

    @Test
    public void calculateRMSTest() {
        double[] buffer = new double[]{1,2,3,4,5};
        double rms = MyMaths.calculateRMS(buffer);
        Assert.assertTrue(Math.round(rms) == 3);
    }

    @Test
    public void calculateRTTest() {
        double RT = MyMaths.calculateRT(-10.50);
        System.out.println(RT);
    }

    @Test
    public void maxValueTest() {
        double[] array = generateImpulse(10);
        double max = MyTools.maxValue(array);
        for (double d : array) {
            System.out.println(d);
            Assert.assertTrue("Error: max debería ser mayor que los demás números.", max >= d);
        }
        System.out.println("\nMax value: " + max);
    }

    @Test
    public void minValueTest() {
        double[] array = generateImpulse(10);
        double min = MyTools.minValue(array);
        for (double d : array) {
            System.out.println(d);
            Assert.assertTrue("Error: min debería ser menor que los demás números.", min <= d);
        }
        System.out.println("\nMin value: " + min);
    }

    @Test
    public void convertToDecibelTest() {
        double[] curve = generateImpulse(10);
        double[] curveInDecibel = MyMaths.convertToDecibel(curve);
        for (int i = 0; i < curve.length; i++) {
            System.out.println("Double: " + curve[i] + "  dB: " + curveInDecibel[i]);
        }
    }

    @Test
    public void reverseTest() {
        double[] array = new double[]{1, 2, 3, 4, 5};
        MyTools.reverse(array);
        for (double d:array) {
            System.out.println(d);
        }
    }

    @Test
    public void generateMatrixTest() {
        double[] mImpulse = new double[]{0.0, -3.0, -5.0, -6.3, -7.0, -3.2, -9.0, -11.0};
        ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
        double[] curve = MyMaths.convertToDecibel(MyMaths.schroederIntegration(mImpulse));
        matrix = MyMaths.generateMatrix(curve, "EDT");

        int x = MyMaths.getVectorX(matrix).length;
        int y = MyMaths.getVectorY(matrix).length;

    }

    @Test
    public void polyfitTest() {
        double[] mImpulse = new double[]{0.0, -3.0, -5.0, -6.3, -7.0, -3.2, -9.0, -11.0};
        ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
        double[] curve = MyMaths.convertToDecibel(MyMaths.schroederIntegration(mImpulse));
        matrix = MyMaths.generateMatrix(curve, "EDT");

        MyMaths.polyfit(MyMaths.getVectorX(matrix), MyMaths.getVectorY(matrix));
    }

    @Test
    public void polyfitTest2() {
        double[] x = new double[]{0, 1, 2, 3, 4, 5};
        double[] y = new double[]{0, 1, 2, 3, 4, 5};

        MyMaths.polyfit(x, y);

    }
}