package com.example.medidordeparametrosacusticos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medidordeparametrosacusticos.R;

import java.util.ArrayList;

public class FileViewerAdapter extends RecyclerView.Adapter<FileViewerAdapter.FileViewerViewHolder> {
    ArrayList<String> mDataset;
    private OnItemListener mOnItemListener;

    public FileViewerAdapter(ArrayList<String> mDataset, OnItemListener onItemListener) {
        this.mDataset = mDataset;
        this.mOnItemListener = onItemListener;
    }

    @NonNull
    @Override
    public FileViewerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_view, parent, false);
        return new FileViewerViewHolder(itemView, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewerViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class FileViewerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        OnItemListener onItemListener;

        public FileViewerViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.file_name_text);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
