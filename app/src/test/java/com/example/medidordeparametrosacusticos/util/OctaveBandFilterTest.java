package com.example.medidordeparametrosacusticos.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OctaveBandFilterTest {
    private double[] impulse;
    OctaveBandFilter octaveBandFilter = new OctaveBandFilter();

    @Before
    public void setUp() throws Exception {
        impulse = generateImpulse(50000);
    }


    public static double getRandomDoubleBetweenRange(double min, double max){
        double x = Math.floor((Math.random()*((max-min)+1))+min);
        return x;
    }

    public static double[] generateImpulse(int length) {
        double[] impulse = new double[length];
        for (int i = 0; i < impulse.length; i++) {
            impulse[i] = getRandomDoubleBetweenRange(0, 32767);
        }
        return impulse;
    }

    @Test
    public void applyToTest() throws Exception {
        double[][] filteredImpulses = octaveBandFilter.applyTo(impulse);
        for (int i = 0; i < filteredImpulses.length; i++) {
            for (int j = 0; j < filteredImpulses[i].length; j++) {
                Assert.assertNotEquals(impulse[j],filteredImpulses[i][j]);
                Assert.assertTrue(Math.abs(filteredImpulses[i][j])!=Double.NaN);
            }
        }
    }
}