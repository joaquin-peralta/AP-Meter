package com.example.medidordeparametrosacusticos.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.medidordeparametrosacusticos.adapters.FileViewerAdapter;
import com.example.medidordeparametrosacusticos.databinding.FragmentFileViewerBinding;
import com.example.medidordeparametrosacusticos.storage.StorageManager;

public class FileViewerFragment extends Fragment {
    private FragmentFileViewerBinding binding;
    private FileViewerAdapter mAdapter;
    private StorageManager storageManager = new StorageManager(getContext());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFileViewerBinding.inflate(inflater, container, false);
        View fileView = binding.getRoot();
        return fileView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(llm);
        mAdapter = storageManager.getAdapter();
        binding.recyclerView.setAdapter(storageManager.getAdapter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}