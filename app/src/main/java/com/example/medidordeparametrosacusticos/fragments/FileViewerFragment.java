package com.example.medidordeparametrosacusticos.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.medidordeparametrosacusticos.activities.MainActivity;
import com.example.medidordeparametrosacusticos.activities.Results;
import com.example.medidordeparametrosacusticos.adapters.FileViewerAdapter;
import com.example.medidordeparametrosacusticos.databinding.FragmentFileViewerBinding;
import com.example.medidordeparametrosacusticos.storage.InternalStorage;

import java.util.ArrayList;

public class FileViewerFragment extends Fragment implements FileViewerAdapter.OnItemListener {
    private FragmentFileViewerBinding binding;
    private FileViewerAdapter mAdapter;
    private  ArrayList<String> mDataset = new ArrayList<>();
    private static InternalStorage mStorage;

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

        updateDataset();

        if (mDataset.size() == 0)  {
            Toast.makeText(getContext(), "No hay mediciones", Toast.LENGTH_SHORT).show();
        }
        mAdapter = new FileViewerAdapter(mDataset, this);
        binding.recyclerView.setAdapter(mAdapter);

        mStorage = new InternalStorage(mDataset, mAdapter);
    }

    public static InternalStorage getInternalStorage() {
        return mStorage;
    }

    public void updateDataset() {
        String[] fileList = getContext().fileList();
        if (fileList.length != 0) {
            for (int i = 0; i < fileList.length; i++) {
                mDataset.add(fileList[i]);
            }
        }

    }

    @Override
    public void onItemClick(int position) {
        String measure = MainActivity.getMeasures().get(position);
        Intent intent = new Intent(getContext(), Results.class);
        intent.putExtra("Reverb times", measure);
        startActivity(intent);
    }
}