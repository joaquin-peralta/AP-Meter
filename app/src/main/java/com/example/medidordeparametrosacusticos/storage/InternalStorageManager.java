package com.example.medidordeparametrosacusticos.storage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.medidordeparametrosacusticos.activities.MainActivity;
import com.example.medidordeparametrosacusticos.adapters.FileViewerAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InternalStorageManager {
    ArrayList<String> mMeasures;
    FileViewerAdapter mAdapter;

    Date today;
    SimpleDateFormat formatter;

    public InternalStorageManager(ArrayList<String> mMeasures, FileViewerAdapter mAdapter) {
        this.mMeasures = mMeasures;
        this.mAdapter = mAdapter;
    }

    public void save(final double[][] data, final Context context) {
        formatter = new SimpleDateFormat("EEE d MMM yy h:mm a", Locale.US);
        today = new Date();
        String FILE_NAME = formatter.format(today);
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
            mMeasures.add(FILE_NAME);
            mAdapter.notifyItemInserted(mMeasures.size() - 1);

            // update internal storage
            String newMeasure = load(context, FILE_NAME);
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

    public static String load(Context context, String FILE_NAME) {
        FileInputStream fis = null;
        String text = null;

        try {
            fis = context.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            text = sb.toString();
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
        return text;
    }

    public void delete(int position, Context context) {
        String FILE_NAME = context.fileList()[position];
        context.deleteFile(FILE_NAME);
        mMeasures.remove(position);
        mAdapter.notifyItemRemoved(position);
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
