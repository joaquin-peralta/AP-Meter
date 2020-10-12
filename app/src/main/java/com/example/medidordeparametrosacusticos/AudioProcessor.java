package com.example.medidordeparametrosacusticos;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.view.View;

import com.example.medidordeparametrosacusticos.activities.MainActivity;
import com.example.medidordeparametrosacusticos.databinding.FragmentRecordBinding;
import com.example.medidordeparametrosacusticos.fragments.FileViewerFragment;
import com.example.medidordeparametrosacusticos.storage.StorageManager;
import com.example.medidordeparametrosacusticos.util.MyMaths;

public class AudioProcessor {
    private OctaveBandFilter octaveBandFilter = new OctaveBandFilter();
    private Thread processingThread = null;
    private double[][] reverbTimes;
    private StorageManager storageManager;

    // UI
    private Handler processorThreadHandler = new Handler(Looper.getMainLooper());
    private FragmentRecordBinding binding;

    public AudioProcessor(FragmentRecordBinding binding) {
        this.binding = binding;
    }

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
               }
           }
       });
        processingThread.start();
    }
}
