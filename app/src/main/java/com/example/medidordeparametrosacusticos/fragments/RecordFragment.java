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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.medidordeparametrosacusticos.R;
import com.example.medidordeparametrosacusticos.databinding.FragmentRecordBinding;
import com.example.medidordeparametrosacusticos.models.AudioRecorder;
import com.example.medidordeparametrosacusticos.viewmodels.SharedViewModel;

public class RecordFragment extends Fragment {
    private FragmentRecordBinding binding;
    private boolean isActive = true;
    private SharedViewModel sharedViewModel;
    private AudioRecorder mRecorder;

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
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.loadCurrentMeasures();
        binding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    private void toggle() {
        executeTask();
        isActive = !isActive;
    }

    private void executeTask() {
        if (isActive) {
            binding.btnRecord.setBackgroundTintList(ColorStateList.valueOf(
                    Color.parseColor("#E71837"))); // color rojo
            binding.btnRecord.setImageResource(R.drawable.ic_mic_off);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Toast.makeText(getContext(), "Esperando impulso...", Toast.LENGTH_SHORT).show();
            mRecorder = new AudioRecorder();
            sharedViewModel.initRecorder(mRecorder);
            sharedViewModel.getIsRecording().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        binding.btnRecord.setEnabled(false);
                        binding.btnRecord.setBackgroundTintList(
                                ColorStateList.valueOf(Color.parseColor("#808080")));
                    } else {
                        binding.btnRecord.setEnabled(true);
                        binding.btnRecord.setBackgroundTintList(
                                ColorStateList.valueOf(Color.parseColor("#49B675"))); // color verde
                        binding.btnRecord.setImageResource(R.drawable.ic_mic_white);
                    }
                }
            });
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
