package com.example.medidordeparametrosacusticos.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.medidordeparametrosacusticos.adapters.MyTabAdapter;
import com.example.medidordeparametrosacusticos.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private static ArrayList<String> mMeasures = new ArrayList<>();
    private static Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        appContext = getApplicationContext();

        ViewPager2 viewPager2 = binding.viewPager2;
        viewPager2.setAdapter(new MyTabAdapter(this));

        TabLayout tabLayout = binding.tabLayout;
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0: {
                        tab.setText("Rec");
                        break;
                    }
                    case 1: {
                        tab.setText("Mediciones");
                        break;
                    }
                }

            }
        }
        );
        tabLayoutMediator.attach();

        String[] fileList = getApplicationContext().fileList();
        if (fileList.length != 0) {
            for (int i = 0; i < fileList.length ; i++) {
                String measure = loadResult(getApplicationContext(), fileList[i]);
                mMeasures.add(measure);
            }
        }
        checkPermission();
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
        }
    }

    public static String loadResult(Context context, String FILE_NAME) {
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

    public static ArrayList<String> getMeasures() {
        return mMeasures;
    }

    public static Context getAppContext() {
        return appContext;
    }
}