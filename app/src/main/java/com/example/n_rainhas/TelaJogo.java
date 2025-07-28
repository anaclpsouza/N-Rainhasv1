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
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class TelaJogo extends BaseActivity {
    private GridLayout gridLayout;
    private int tamanho_tabuleiro;
    private Jogo jogo;
    private View[][] views;
    private Button btnReiniciar;
    private ImageButton btnConfig;
    private HashMap<Point, ObjectAnimator> mapaDeAnimacoes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_jogo);

        SharedPreferences preferences = getSharedPreferences("nrainhas", Context.MODE_PRIVATE);
        tamanho_tabuleiro = preferences.getInt("qtdRainhas", 4);

        jogo = new Jogo(tamanho_tabuleiro);

        gridLayout = findViewById(R.id.tabuleiro);
        createChessboard(tamanho_tabuleiro);

        btnConfig = findViewById(R.id.btnConfigurar);
        btnReiniciar = findViewById(R.id.btnReiniciar);

        btnConfig.setOnClickListener(l -> {
            Intent intent = new Intent(getApplicationContext(), TelaConfigs.class);
            startActivity(intent);
        });
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
                    mapaDeAnimacoes.get(p).cancel();
                    mapaDeAnimacoes.remove(p);
                }

                if (jogo.hasQueen(r, c)) {
                    desenharRainha(r, c);

                } else {
                    limparCelula(r, c);
                }
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