package com.example.medidordeparametrosacusticos.storage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.medidordeparametrosacusticos.activities.MainActivity;
import com.example.medidordeparametrosacusticos.adapters.FileViewerAdapter;
import com.example.medidordeparametrosacusticos.fragments.FileViewerFragment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

public class InternalStorage {
    ArrayList<String> mDataset;
    FileViewerAdapter mAdapter;

    public InternalStorage(ArrayList<String> mDataset, FileViewerAdapter mAdapter) {
        this.mDataset = mDataset;
        this.mAdapter = mAdapter;
    }

    public void save(final double[][] data, final Context context) {
        Date currentDate = new Date(System.currentTimeMillis());
        String FILE_NAME = currentDate.toString();
        String text = "";
        FileOutputStream fos = null;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                text += data[i][j] + ";";
            }
        }

        try {
            fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            mDataset.add(FILE_NAME);
            mAdapter.notifyItemInserted(mDataset.size() - 1);

            // update internal storage
            String newMeasure = MainActivity.loadResult(context, FILE_NAME);
            MainActivity.getMeasures().add(newMeasure);
            showToast(context);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showToast(final Context context) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "Medición guardada", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
