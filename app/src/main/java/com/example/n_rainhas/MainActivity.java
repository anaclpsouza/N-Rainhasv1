package com.example.n_rainhas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends BaseActivity {
    Button btnJogar, btnConfiguracoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnJogar = findViewById(R.id.btnJogar);
        btnConfiguracoes = findViewById(R.id.btnConfig);

        btnJogar.setOnClickListener(l -> {
            Intent intent = new Intent(getApplicationContext(), TelaJogo.class);
            startActivity(intent);
        });

        btnConfiguracoes.setOnClickListener(l -> {
            Intent intent = new Intent(getApplicationContext(), TelaConfigs.class);
            startActivity(intent);
        });
    }
}