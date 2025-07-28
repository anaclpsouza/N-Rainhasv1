package com.example.n_rainhas;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import classes.Jogo;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TelaJogo extends BaseActivity {
    private GridLayout gridLayout;
    private int tamanho_tabuleiro;
    private Jogo jogo;
    private View[][] views;
    private Button btnReiniciar;
    private ImageButton btnConfig;
    private HashMap<Point, ObjectAnimator> mapaDeAnimacoes = new HashMap<>();

    LinearLayout layoutRainhas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_jogo);

        SharedPreferences preferences = getSharedPreferences("nrainhas", Context.MODE_PRIVATE);
        tamanho_tabuleiro = preferences.getInt("qtdRainhas", 4);

        jogo = new Jogo(tamanho_tabuleiro);
        colocarRainhas(tamanho_tabuleiro);


        gridLayout = findViewById(R.id.tabuleiro);
        createChessboard(tamanho_tabuleiro);

        btnConfig = findViewById(R.id.btnConfigurar);
        btnReiniciar = findViewById(R.id.btnReiniciar);

        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // se pa um alert dialog p confirmar
                Intent intent = getIntent();
                finish();
                overridePendingTransition(0, 0); // Desabilita a animação
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        btnConfig.setOnClickListener(l -> {
            // fazer aviso de que você vai perder o jogo
            Intent intent = new Intent(getApplicationContext(), TelaConfigs.class);
            Bundle params = new Bundle();
            params.putString("chamada", "TelaJogo");
            intent.putExtras(params);
            startActivity(intent);
            finish();
        });
    }

    private void colocarRainhas(int tamanhoTabuleiro) {
        int qteRainhas = tamanhoTabuleiro;
        layoutRainhas = findViewById(R.id.layoutRainhas);

        for (int i = 0; i < qteRainhas; i++) {
            ImageView rainha = new ImageView(this);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, // Largura
                    LinearLayout.LayoutParams.MATCH_PARENT  // Altura
            );

            params.setMargins(8, 0, 8, 0);

            rainha.setLayoutParams(params);

            if (i % 2 != 0) {
                rainha.setImageResource(R.drawable.rainha_marrom);
            } else {
                rainha.setImageResource(R.drawable.rainha_branca);
            }

            layoutRainhas.addView(rainha);
        }
    }

    private void createChessboard(int tamanho_tabuleiro) {

        gridLayout.setColumnCount(tamanho_tabuleiro);
        gridLayout.setRowCount(tamanho_tabuleiro);
        views = new View[tamanho_tabuleiro][tamanho_tabuleiro];

        int corVinho = ContextCompat.getColor(this, R.color.vinho);
        int corBege = ContextCompat.getColor(this, R.color.bege);

        for (int lin = 0; lin < tamanho_tabuleiro; lin++) {
            for (int col = 0; col < tamanho_tabuleiro; col++) {
                View celula = new View(this);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                        GridLayout.spec(lin, 1f),
                        GridLayout.spec(col, 1f)
                );
                params.width = 0;
                params.height = 0;

                celula.setLayoutParams(params);
                final int finalLinha = lin;
                final int finalCol = col;

                views[lin][col] = celula;

                int backgroundColor = ((lin + col) % 2 == 0) ? corVinho : corBege;
                celula.setBackgroundColor(backgroundColor);
                celula.setTag(backgroundColor);

                celula.setOnClickListener(v -> {
                    jogar(finalLinha, finalCol);
                });

                gridLayout.addView(celula);
            }
        }
    }

    private void jogar(int row, int col){
        if(!jogo.jogada(row, col)){
            // rainhas esgotadas
        }

        else if (jogo.isJogoGanho()) {
            // mensagem de vitória
            return;
        }

        atualizarTela();
    }

    private void atualizarTela() {

        if (jogo.isJogoGanho()) {
            // mensagem de vitória
            return;
        }

        ArrayList<Point> rainhasEmConflito = jogo.getRainhasEmConflito();

        for (int r = 0; r < tamanho_tabuleiro; r++) {
            for (int c = 0; c < tamanho_tabuleiro; c++) {
                Point p = new Point(c, r);

                if (mapaDeAnimacoes.containsKey(p)) {
                    Objects.requireNonNull(mapaDeAnimacoes.get(p)).end();
                    mapaDeAnimacoes.remove(p);
                }

                if (jogo.hasQueen(r, c)) {
                    desenharRainha(r, c);
                    btnReiniciar.setEnabled(true);
                } else {
                    limparCelula(r, c);
                    if (jogo.getQtdRainhas() == tamanho_tabuleiro) btnReiniciar.setEnabled(false);
                }
            }

            if (layoutRainhas.getChildCount() > 0 && layoutRainhas.getChildCount() > jogo.getQtdRainhas()) {
                layoutRainhas.removeViewAt(0);
            }


        }

        for (Point p : rainhasEmConflito) {
            View celula = views[p.y][p.x];
            ObjectAnimator animator = ObjectAnimator.ofFloat(celula, "alpha", 1.0f, 0.4f, 1.0f);
            animator.setDuration(1200);
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            animator.start();
            mapaDeAnimacoes.put(p, animator);
        }
    }
    private void desenharRainha(int linha, int col){
        View cell = views[linha][col];
        int originalColor = (int) cell.getTag();
        int queenDrawableId = (originalColor == ContextCompat.getColor(this, R.color.vinho)) ? R.drawable.rainha_branca : R.drawable.rainha_marrom;

        LayerDrawable finalBackground = new LayerDrawable(
                new Drawable[] {
                        new ColorDrawable(originalColor),
                        ContextCompat.getDrawable(this, queenDrawableId)
                }
        );
        cell.setBackground(finalBackground);
    }

    private void limparCelula(int row, int col) {
        View cell = views[row][col];
        cell.setAlpha(1.0f);
        int originalColor = (int) cell.getTag();
        cell.setBackgroundColor(originalColor);
    }
}