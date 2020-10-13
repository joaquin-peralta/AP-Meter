package com.example.medidordeparametrosacusticos.storage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.example.medidordeparametrosacusticos.activities.Results;
import com.example.medidordeparametrosacusticos.adapters.FileViewerAdapter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StorageManager implements FileViewerAdapter.OnItemListener {
    private WeakReference<Context> contextWeakReference;
    private ArrayList<String> mResults = new ArrayList<String>();
    private ArrayList<String> mMeasuresList = new ArrayList<String>();
    private FileViewerAdapter mAdapter = new FileViewerAdapter(mMeasuresList, this);

    public StorageManager(Context context) {
        contextWeakReference = new WeakReference<Context>(context);
    }

    public void loadCurrentMeasures() {
        Context context = contextWeakReference.get();
        if (context == null) {
            return;
        }

        String[] fileList = context.fileList();
        if (fileList.length != 0) {
            for (int i = 0; i < fileList.length ; i++) {
                loadFile(fileList[i]);
            }
        }
    }


    private void loadFile(String fileName) {
        Context context = contextWeakReference.get();
        if (context == null) {
            return;
        }

        FileInputStream fis = null;
        String text = null;

        try {
            fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            text = sb.toString();
            mResults.add(text);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void save(final double[][] data) {
        Context context = contextWeakReference.get();
        if (context == null) {
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM yy h:mm a", Locale.US);
        Date today = new Date();
        String fileName = formatter.format(today);
        String text = "";
        FileOutputStream fos = null;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                text += data[i][j] + ";";
            }
        }

        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            mMeasuresList.add(fileName);
            mAdapter.notifyItemInserted(mMeasuresList.size() - 1);

            // update internal storage
            loadFile(fileName);
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

    public void delete(int position) {
        Context context = contextWeakReference.get();
        if (context == null) {
            return;
        }
        String fileName = context.fileList()[position];
        context.deleteFile(fileName);
        mMeasuresList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onItemClick(int position) {
        Context context = contextWeakReference.get();
        if (context == null) {
            return;
        }
        String measure = getResults().get(position);
        Intent intent = new Intent(context, Results.class);
        intent.putExtra("Reverb times", measure);
        context.startActivity(intent);
    }

    @Override
    public void onDeleteClick(final int position) {
        final Context context = contextWeakReference.get();
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("¿Eliminar medición?");
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete(position);
                Toast.makeText(context, "Medición eliminada", Toast.LENGTH_SHORT).show();
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

    public ArrayList<String> getResults() {
        return mResults;
    }

    public ArrayList<String> getMeasuresList() {
        return mMeasuresList;
    }

    public FileViewerAdapter getAdapter() {
        return mAdapter;
    }
}
