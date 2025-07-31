package com.example.n_rainhas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Objects;

import classes.Musica;

public class TelaConfigs extends BaseActivity {
    Switch swMusica;
    Spinner spRainhas;
    ArrayAdapter<Integer> qtdRainhas;
    Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_configs);

        //configurando o sharedPreferences
        SharedPreferences preferences = getSharedPreferences("nrainhas", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        btnVoltar = findViewById(R.id.btnVoltar);
        swMusica = findViewById(R.id.swSom);

        // Configurando spinner
        ArrayList<Integer> num = new ArrayList<>();
        num.add(4); num.add(5); num.add(6); num.add(7); num.add(8);
        qtdRainhas = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, num);
        spRainhas = findViewById(R.id.spQtdRainhas);
        spRainhas.setAdapter(qtdRainhas);

        spRainhas.setSelection(preferences.getInt("qtdRainhas", 4) - 4);
        spRainhas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putInt("qtdRainhas", (Integer) adapterView.getItemAtPosition(i));
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Configura o switch de acordo com a escolha anterior do usuário
        boolean musicaAtivada = preferences.getBoolean("musica", true);
        swMusica.setChecked(musicaAtivada);
        swMusica.setText(musicaAtivada ? R.string.ativar : R.string.desativar);

        swMusica.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("musica", isChecked);
            editor.apply();

            Intent intent = new Intent(getApplicationContext(), Musica.class);

            if (isChecked) {
                swMusica.setText(R.string.ativar);
                intent.setAction(Musica.ACTION_PLAY);
                startService(intent);
            } else {
                swMusica.setText(R.string.desativar);
                intent.setAction(Musica.ACTION_STOP);
                startService(intent);
            }
        });

        btnVoltar.setOnClickListener(l -> {
            Intent it = getIntent();

            if (it.getExtras() != null && Objects.requireNonNull(it.getExtras()).getString("chamada").equals("TelaJogo")) {
                Intent it2 = new Intent(TelaConfigs.this, TelaJogo.class);
                startActivity(it2);
            }
            finish();
        });
    }
}