package com.example.medidordeparametrosacusticos.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.medidordeparametrosacusticos.R;
import com.example.medidordeparametrosacusticos.databinding.FragmentRecordBinding;
import com.example.medidordeparametrosacusticos.viewmodels.SharedViewModel;

import java.util.ArrayList;

public class RecordFragment extends Fragment {
    private FragmentRecordBinding binding;
    private boolean isEnabled = false;
    private SharedViewModel sharedViewModel;
    private Handler handler = new Handler((Looper.getMainLooper()));

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
        sharedViewModel.init();
        binding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        final Observer<Boolean> stateObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.btnRecord.setEnabled(false);
                    binding.btnRecord.setBackgroundTintList(
                            ColorStateList.valueOf(Color.parseColor("#808080"))); // color gris
                }
            }
        };

        final Observer<ArrayList<Short>> audioDataObserver = new Observer<ArrayList<Short>>() {
            @Override
            public void onChanged(final ArrayList<Short> shorts) {
                if (!shorts.isEmpty()) {
                    binding.progressCircularBar.setVisibility(View.VISIBLE);
                    sharedViewModel.processData(shorts);
                }
            }
        };

        final Observer<ArrayList<ArrayList<Double>>> reverbObserver = new Observer<ArrayList<ArrayList<Double>>>() {
            @Override
            public void onChanged(ArrayList<ArrayList<Double>> reverbTimes) {
                sharedViewModel.saveData(reverbTimes);
                binding.btnRecord.setBackgroundTintList(
                        ColorStateList.valueOf(Color.parseColor("#49B675"))); // color verde
                binding.btnRecord.setImageResource(R.drawable.ic_mic_white);
                Toast.makeText(getContext(), "Medición guardada", Toast.LENGTH_SHORT).show();
            }
        };

        sharedViewModel.getIsRecording().observe(getViewLifecycleOwner(), stateObserver);
        sharedViewModel.getAudioData().observe(getViewLifecycleOwner(), audioDataObserver);
        sharedViewModel.getReverbTimes().observe(getViewLifecycleOwner(), reverbObserver);
    }

    private void toggle() {
        isEnabled = !isEnabled;
        executeTask();
    }

    private void executeTask() {
        if (isEnabled) {
            binding.btnRecord.setBackgroundTintList(ColorStateList.valueOf(
                    Color.parseColor("#E71837"))); // color rojo
            binding.btnRecord.setImageResource(R.drawable.ic_mic_off);
            Toast.makeText(getContext(), "Esperando impulso...", Toast.LENGTH_SHORT).show();
            sharedViewModel.initRecorder();


        } else {
            binding.btnRecord.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#49B675"))); // color verde
            binding.btnRecord.setImageResource(R.drawable.ic_mic_white);
            Toast.makeText(getContext(), "Medición cancelada", Toast.LENGTH_SHORT).show();
            sharedViewModel.finishRecorder();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
