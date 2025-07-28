package com.example.n_rainhas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

import classes.Musica;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("nrainhas", Context.MODE_PRIVATE);
        if (preferences.getBoolean("musica", true)) {
            Intent intent = new Intent(getApplicationContext(), Musica.class);
            intent.setAction(Musica.ACTION_PLAY);
            startService(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(getApplicationContext(), Musica.class);
        intent.setAction(Musica.ACTION_PAUSE);
        startService(intent);
    }
}