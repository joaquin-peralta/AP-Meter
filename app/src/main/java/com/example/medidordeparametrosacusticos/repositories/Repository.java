package com.example.medidordeparametrosacusticos.repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Repository {
    private static Repository instance;
    private WeakReference<Context> contextWeakReference;
    private ArrayList<String> mResults = new ArrayList<String>();
    private ArrayList<String> mFileList = new ArrayList<String>();

    private Repository(Context context) {
        contextWeakReference = new WeakReference<Context>(context);
    }

    public static Repository getInstance(Context context) {
        if (instance == null) {
            instance = new Repository(context);
        }
        return instance;
    }

    public MutableLiveData<ArrayList<String>> getFileList() {
        Context context = contextWeakReference.get();
        String[] fileList = context.fileList();
        mFileList.addAll(Arrays.asList(fileList));
        MutableLiveData<ArrayList<String>> data = new MutableLiveData<>();
        data.setValue(mFileList);
        return data;
    }

    public MutableLiveData<ArrayList<String>> getResults() {
        loadMeasures();
        MutableLiveData<ArrayList<String>> data = new MutableLiveData<>();
        data.setValue(mResults);
        return data;
    }

    public void loadMeasures() {
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
            mFileList.add(fileName);

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
        mFileList.remove(position);
    }
}
