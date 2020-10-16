package com.example.medidordeparametrosacusticos.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.medidordeparametrosacusticos.adapters.FileViewerAdapter;
import com.example.medidordeparametrosacusticos.databinding.FragmentFileViewerBinding;

import java.util.ArrayList;

public class FileViewerFragment extends Fragment implements FileViewerAdapter.OnItemListener {
    private FragmentFileViewerBinding binding;
    private FileViewerAdapter mAdapter;
    private ArrayList<String> mMeasuresList;

    public interface FileViewerFragmentListener {
        void onInputSent(ArrayList<String> arrayList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
        getMeasuresList();
        mAdapter = new FileViewerAdapter(mMeasuresList, this);
        binding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(int position) {
        /*String measure = getResults().get(position);
        Intent intent = new Intent(getContext(), Results.class);
        intent.putExtra("Reverb times", measure);
        startActivity(intent);*/
    }

    @Override
    public void onDeleteClick(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("¿Eliminar medición?");
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = getContext().fileList()[position];
                getContext().deleteFile(fileName);
                mAdapter.notifyItemRemoved(position);
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

    private void getMeasuresList() {

    }
}