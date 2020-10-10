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
import com.example.medidordeparametrosacusticos.storage.InternalStorageManager;
import com.example.medidordeparametrosacusticos.util.MyMaths;

public class AudioProcessor {
    private OctaveBandFilter octaveBandFilter = new OctaveBandFilter();
    private Thread processorThread = null;
    private double[][] reverbTimes;
    private InternalStorageManager mStorage;

    // UI
    private Handler processorThreadHandler = new Handler(Looper.getMainLooper());
    private FragmentRecordBinding binding;

    public AudioProcessor(FragmentRecordBinding binding) {
        this.binding = binding;
    }

    public void process(final double[] impulse) {
       processorThread = new Thread(new Runnable() {
           @Override
           public void run() {
               android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
               showProgressBar();
               reverbTimes = new double[9][4];
               double[][] filteredImpulses = octaveBandFilter.applyTo(impulse);
               for (int i = 0; i < filteredImpulses.length; i++) {
                   double[] decayCurve = MyMaths.schroederIntegration(filteredImpulses[i]);
                   double[] normalizeCurve = MyMaths.convertToDecibel(decayCurve);

               }

               mStorage = FileViewerFragment.getCurrentStorage();
               mStorage.save(reverbTimes, MainActivity.getAppContext());

               restartUI();
           }
       });
        processorThread.start();
    }

    private void showProgressBar() {
        processorThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                binding.progressCircularBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void restartUI() {
        processorThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                binding.progressCircularBar.setVisibility(View.INVISIBLE);
                binding.btnRecord.setEnabled(true);
                binding.btnRecord.setBackgroundTintList(
                        ColorStateList.valueOf(Color.parseColor("#49B675"))); // color verde
                binding.btnRecord.setImageResource(R.drawable.ic_mic_white);
            }
        });
    }
}
