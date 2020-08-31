package com.example.medidordeparametrosacusticos.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.medidordeparametrosacusticos.AudioRecorder;
import com.example.medidordeparametrosacusticos.R;
import com.example.medidordeparametrosacusticos.databinding.FragmentRecordBinding;

public class RecordFragment extends Fragment {
    FragmentRecordBinding binding;
    private AudioRecorder mRecorder = null;


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
        mRecorder = new AudioRecorder(binding);
        binding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEvent();
            }
        });
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.progressBar.setMax(141000);
        binding.progressCircularBar.setVisibility(View.INVISIBLE);
    }

    public void startEvent() {
        if (!mRecorder.hasInitialized()) {
            binding.btnRecord.setBackgroundTintList(ColorStateList.valueOf(
                    Color.parseColor("#E71837"))); // color rojo
            binding.btnRecord.setImageResource(R.drawable.ic_mic_off);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Toast.makeText(getContext(), "Esperando impulso...", Toast.LENGTH_SHORT).show();
            mRecorder = new AudioRecorder(binding);
            mRecorder.startRecording();

        } else {
            binding.btnRecord.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#49B675"))); // color verde
            binding.btnRecord.setImageResource(R.drawable.ic_mic_white);
            Toast.makeText(getContext(), "Medición cancelada", Toast.LENGTH_SHORT).show();
            mRecorder.stopRecording();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
