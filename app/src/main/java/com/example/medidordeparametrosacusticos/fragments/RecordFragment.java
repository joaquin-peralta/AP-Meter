package com.example.medidordeparametrosacusticos.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.medidordeparametrosacusticos.AudioProcessor;
import com.example.medidordeparametrosacusticos.AudioRecorder;
import com.example.medidordeparametrosacusticos.R;
import com.example.medidordeparametrosacusticos.databinding.FragmentRecordBinding;

import java.lang.ref.WeakReference;

public class RecordFragment extends Fragment {
    private FragmentRecordBinding binding;
    private RecordAndProcessTask mTask;
    private boolean mStatus = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecordBinding.inflate(inflater, container, false);
        View recordView = binding.getRoot();
        return recordView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event();
            }
        });
    }

    private void event() {
        executeTask();
        mStatus = !mStatus;
    }

    private void executeTask() {
        if (mStatus == true) {
            mTask = new RecordAndProcessTask(getContext(), binding);
            mTask.execute();

        } else {
            mTask.cancel(false);
        }
    }

    private static class RecordAndProcessTask extends AsyncTask<Void, Void, String> {
        private WeakReference<Context> contextWeakReference;
        private WeakReference<FragmentRecordBinding> bindingWeakReference;
        private AudioRecorder mRecorder = new AudioRecorder();

        RecordAndProcessTask(Context context, FragmentRecordBinding binding) {
            contextWeakReference = new WeakReference<Context>(context);
            bindingWeakReference = new WeakReference<FragmentRecordBinding>(binding);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Context context = contextWeakReference.get();
            if (context == null) {
                return;
            }

            FragmentRecordBinding binding = bindingWeakReference.get();
            if (binding == null) {
                return;
            }

            binding.btnRecord.setBackgroundTintList(ColorStateList.valueOf(
                    Color.parseColor("#E71837"))); // color rojo
            binding.btnRecord.setImageResource(R.drawable.ic_mic_off);
//            binding.progressCircularBar.setVisibility(View.VISIBLE);
            Toast.makeText(context, "Esperando impulso...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            Context context = contextWeakReference.get();

            mRecorder = new AudioRecorder();
            mRecorder.startRecording();
            while (mRecorder.isRecording()) {
                if (isCancelled())
                    break;
            }
            new AudioProcessor(context).process(mRecorder.getAudioData());
            return "Medición guardada";
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            FragmentRecordBinding binding = bindingWeakReference.get();
            binding.btnRecord.setBackgroundTintList(ColorStateList.valueOf(
                    Color.parseColor("#AAAAAA")));
            binding.btnRecord.setEnabled(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Context context = contextWeakReference.get();
            if (context == null) {
                return;
            }

            FragmentRecordBinding binding = bindingWeakReference.get();
            if (binding == null) {
                return;
            }

//            binding.progressCircularBar.setVisibility(View.INVISIBLE);
            binding.btnRecord.setEnabled(true);
            binding.btnRecord.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#49B675"))); // color verde
            binding.btnRecord.setImageResource(R.drawable.ic_mic_white);
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            Context context = contextWeakReference.get();
            if (context == null) {
                return;
            }

            FragmentRecordBinding binding = bindingWeakReference.get();
            if (binding == null) {
                return;
            }

            mRecorder.stopRecording();

            binding.btnRecord.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#49B675"))); // color verde
            binding.btnRecord.setImageResource(R.drawable.ic_mic_white);
//            binding.progressCircularBar.setVisibility(View.INVISIBLE);
            Toast.makeText(context, "Medición cancelada", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
