package com.example.medidordeparametrosacusticos.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.medidordeparametrosacusticos.R;
import com.example.medidordeparametrosacusticos.adapters.MyTabAdapter;
import com.example.medidordeparametrosacusticos.databinding.ActivityMainBinding;
import com.example.medidordeparametrosacusticos.storage.InternalStorageManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private static ArrayList<String> mMeasures = new ArrayList<>();
    private static Context mAppContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAppContext = getApplicationContext();
        updateMeasuresList(mAppContext);

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
        checkPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.elements, menu);
        return true;
    }

    private static void updateMeasuresList(Context context) {
        String[] fileList = context.fileList();
        if (fileList.length != 0) {
            for (int i = 0; i < fileList.length ; i++) {
                String measure = InternalStorageManager.load(context, fileList[i]);
                mMeasures.add(measure);
            }
        }
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
        }
    }

    public static ArrayList<String> getMeasures() {
        return mMeasures;
    }

    public static Context getAppContext() {
        return mAppContext;
    }
}