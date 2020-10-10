package com.example.medidordeparametrosacusticos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.medidordeparametrosacusticos.R;
import com.example.medidordeparametrosacusticos.databinding.ResultsBinding;

public class Results extends AppCompatActivity {
    private ResultsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ResultsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Resultados");

        Intent intent = getIntent();
        String measure = intent.getStringExtra("Reverb times");
        String[] array = measure.split(";");

        binding.m32HzEDT.setText(array[0]);
        binding.m32HzTR10.setText(array[1]);
        binding.m32HzTR20.setText(array[2]);
        binding.m32HzTR30.setText(array[3]);

        binding.m63HzEDT.setText(array[4]);
        binding.m63HzTR10.setText(array[5]);
        binding.m63HzTR20.setText(array[6]);
        binding.m63HzTR30.setText(array[7]);

        binding.m125HzEDT.setText(array[8]);
        binding.m125HzTR10.setText(array[9]);
        binding.m125HzTR20.setText(array[10]);
        binding.m125HzTR30.setText(array[11]);

        binding.m250HzEDT.setText(array[12]);
        binding.m250HzTR10.setText(array[13]);
        binding.m250HzTR20.setText(array[14]);
        binding.m250HzTR30.setText(array[15]);

        binding.m500HzEDT.setText(array[16]);
        binding.m500HzTR10.setText(array[17]);
        binding.m500HzTR20.setText(array[18]);
        binding.m500HzTR30.setText(array[19]);

        binding.m1000HzEDT.setText(array[20]);
        binding.m1000HzTR10.setText(array[21]);
        binding.m1000HzTR20.setText(array[22]);
        binding.m1000HzTR30.setText(array[23]);

        binding.m2000HzEDT.setText(array[24]);
        binding.m2000HzTR10.setText(array[25]);
        binding.m2000HzTR20.setText(array[26]);
        binding.m2000HzTR30.setText(array[27]);

        binding.m4000HzEDT.setText(array[28]);
        binding.m4000HzTR10.setText(array[29]);
        binding.m4000HzTR20.setText(array[30]);
        binding.m4000HzTR30.setText(array[31]);

        binding.m8000HzEDT.setText(array[32]);
        binding.m8000HzTR10.setText(array[33]);
        binding.m8000HzTR20.setText(array[34]);
        binding.m8000HzTR30.setText(array[35]);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
