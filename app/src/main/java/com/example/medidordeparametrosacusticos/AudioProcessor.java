package com.example.medidordeparametrosacusticos;

import android.os.Process;

import com.example.medidordeparametrosacusticos.activities.MainActivity;
import com.example.medidordeparametrosacusticos.fragments.FileViewerFragment;
import com.example.medidordeparametrosacusticos.storage.InternalStorage;
import com.example.medidordeparametrosacusticos.fragments.RecordFragment;
import com.example.medidordeparametrosacusticos.util.MyMaths;
import com.example.medidordeparametrosacusticos.util.OctaveBandFilter;

public class AudioProcessor {
    private OctaveBandFilter octaveBandFilter = new OctaveBandFilter();
    private Thread processingThread = null;
    private double[][] reverbTimes;
    private InternalStorage storage;

    public void process(final double[] impulse) {
       processingThread = new Thread(new Runnable() {
           @Override
           public void run() {
               android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
               reverbTimes = new double[9][4];
               double[][] filteredImpulses = octaveBandFilter.applyTo(impulse);
               for (int i = 0; i < filteredImpulses.length; i++) {
                   double[] decayCurve = MyMaths.schroederIntegration(filteredImpulses[i]);
                   double[] normalizeCurve = MyMaths.convertToDecibel(decayCurve);

                   double[][] matrixEDT = MyMaths.getDecreaseRange(normalizeCurve, "EDT");
                   double[][] matrix10dB = MyMaths.getDecreaseRange(normalizeCurve, "10dB");
                   double[][] matrix20dB = MyMaths.getDecreaseRange(normalizeCurve, "20dB");
                   double[][] matrix30dB = MyMaths.getDecreaseRange(normalizeCurve, "30dB");

                   double slopeEDT = MyMaths.polyfit(matrixEDT != null ? matrixEDT : new double[0][0]);
                   double slope10dB = MyMaths.polyfit(matrix10dB != null ? matrix10dB : new double[0][0]);
                   double slope20dB = MyMaths.polyfit(matrix20dB != null ? matrix20dB : new double[0][0]);
                   double slope30dB = MyMaths.polyfit(matrix30dB != null ? matrix30dB : new double[0][0]);

                   reverbTimes[i][0] = MyMaths.calculateRT(slopeEDT);
                   reverbTimes[i][1] = MyMaths.calculateRT(slope10dB);
                   reverbTimes[i][2] = MyMaths.calculateRT(slope20dB);
                   reverbTimes[i][3] = MyMaths.calculateRT(slope30dB);
               }

               storage = FileViewerFragment.getInternalStorage();
               storage.save(reverbTimes, MainActivity.getAppContext());
           }
       });
       processingThread.start();
    }
}
