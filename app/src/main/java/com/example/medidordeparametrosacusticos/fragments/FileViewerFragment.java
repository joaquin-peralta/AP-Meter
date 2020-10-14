package com.example.medidordeparametrosacusticos.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.medidordeparametrosacusticos.storage.StorageManager;

public class FileViewerFragment extends Fragment implements FileViewerAdapter.OnItemListener {
    private FragmentFileViewerBinding binding;
    private FileViewerAdapter mAdapter;
    private StorageManager storageManager = new StorageManager(getContext());
    private int itemsInserted = 0;

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
        mAdapter = new FileViewerAdapter(storageManager.getMeasuresList(), this);
        binding.recyclerView.setAdapter(mAdapter);
        checkIfNewItemsWereInserted();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(int position) {
        String measure = storageManager.getResults().get(position);
        Intent intent = new Intent(getContext(), Results.class);
        intent.putExtra("Reverb times", measure);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("¿Eliminar medición?");
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                storageManager.delete(position);
                mAdapter.notifyItemRemoved(position);
                itemsInserted = storageManager.getMeasuresList().size();
                Toast.makeText(getContext(), "Medición eliminada", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ...
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkIfNewItemsWereInserted() {
        if (itemsInserted == storageManager.getMeasuresList().size()) {
            return;
        }
        mAdapter.notifyItemInserted(storageManager.getMeasuresList().size() - 1);
    }
}