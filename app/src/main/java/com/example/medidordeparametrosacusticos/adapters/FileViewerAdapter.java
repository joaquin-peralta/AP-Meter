package com.example.medidordeparametrosacusticos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medidordeparametrosacusticos.R;

import java.util.ArrayList;

public class FileViewerAdapter extends RecyclerView.Adapter<FileViewerAdapter.FileViewerViewHolder> {
    ArrayList<String> mDataset;

    public FileViewerAdapter(ArrayList<String> dataset) {
        this.mDataset = dataset;
    }

    @NonNull
    @Override
    public FileViewerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_view, parent, false);
        return new FileViewerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewerViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class FileViewerViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView deleteImage;

        public FileViewerViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.file_name_text);
            deleteImage = itemView.findViewById(R.id.image_delete);}
    }
}
