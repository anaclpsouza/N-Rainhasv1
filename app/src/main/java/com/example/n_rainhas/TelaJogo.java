package com.example.n_rainhas;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.SystemClock;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    private ArrayList<ImageView> listaRainhasDisponiveis = new ArrayList<>();
    private Chronometer cronometro;
    long recorde;
    private boolean tempoRodando = false;
    TextView txtRecorde;
    SharedPreferences preferences;


    //salva o estado da aplicação
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        cronometro.stop();

        outState.putSerializable("ESTADO_JOGO", jogo);
        outState.putLong("TEMPO_CRONOMETRO", cronometro.getBase());
        outState.putBoolean("CRONOMETRO_RODANDO", tempoRodando);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_jogo);

        preferences = getSharedPreferences("nrainhas", Context.MODE_PRIVATE);
        tamanho_tabuleiro = preferences.getInt("qtdRainhas", 4);
        recorde = preferences.getLong("recorde_" + tamanho_tabuleiro, Long.MAX_VALUE);
        cronometro = findViewById(R.id.cronometro);

        txtRecorde = findViewById(R.id.txtRecorde);
        txtRecorde.setText(getString(R.string.recorde) + ": " + ((recorde == Long.MAX_VALUE) ? "Sem Recorde" : formatarTempo(recorde)));

        //recupera o estado da aplicação
        if (savedInstanceState != null) {
            jogo = (Jogo) savedInstanceState.getSerializable("ESTADO_JOGO");

            long tempoSalvo = savedInstanceState.getLong("TEMPO_CRONOMETRO");
            boolean estavaRodando = savedInstanceState.getBoolean("CRONOMETRO_RODANDO");
            cronometro.setBase(tempoSalvo);
            tempoRodando = estavaRodando;

            if (tempoRodando) {
                cronometro.start();
            }

        } else {
            jogo = new Jogo(tamanho_tabuleiro);
        }


        inicializarContadorDeRainhas();
        atualizarContadorDeRainhas(jogo.getQtdRainhas());

        gridLayout = findViewById(R.id.tabuleiro);
        createChessboard(tamanho_tabuleiro);

        btnConfig = findViewById(R.id.btnConfigurar);
        btnReiniciar = findViewById(R.id.btnReiniciar);

        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // se pa um alert dialog p confirmar
                AlertDialog.Builder builer = new AlertDialog.Builder(TelaJogo.this);
                builer.setTitle("Confirmar ação");
                builer.setMessage("Seu progresso no jogo será perdido, tem certeza?");
                builer.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = getIntent();
                        finish();
                        overridePendingTransition(0, 0); // Desabilita a animação
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                });
                builer.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nao faz nothing
                    }
                });
                builer.show();
            }
        });

        btnConfig.setOnClickListener(l -> {
            // fazer aviso de que você vai perder o jogo
            AlertDialog.Builder builer = new AlertDialog.Builder(TelaJogo.this);
            builer.setTitle("Confirmar ação");
            builer.setMessage("Seu progresso no jogo não será salvo, tem certeza?");
            builer.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(), TelaConfigs.class);
                    Bundle params = new Bundle();
                    params.putString("chamada", "TelaJogo");
                    intent.putExtras(params);
                    startActivity(intent);
                    finish();
                }
            });
            builer.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // nao faz nothing
                }
            });
           builer.show();
        });

       //atualiza a tela após mudança de estado
        atualizarTela();
    }

    private void atualizarContadorDeRainhas(int quantidadeParaMostrar) {
        for (int i = 0; i < listaRainhasDisponiveis.size(); i++) {
            ImageView rainha = listaRainhasDisponiveis.get(i);

            if (i < quantidadeParaMostrar) {
                rainha.setVisibility(View.VISIBLE);
            } else {
                rainha.setVisibility(View.GONE);
            }
        }
    }

    private void inicializarContadorDeRainhas() {
        listaRainhasDisponiveis.add(findViewById(R.id.rainha_disponivel_1));
        listaRainhasDisponiveis.add(findViewById(R.id.rainha_disponivel_2));
        listaRainhasDisponiveis.add(findViewById(R.id.rainha_disponivel_3));
        listaRainhasDisponiveis.add(findViewById(R.id.rainha_disponivel_4));
        listaRainhasDisponiveis.add(findViewById(R.id.rainha_disponivel_5));
        listaRainhasDisponiveis.add(findViewById(R.id.rainha_disponivel_6));
        listaRainhasDisponiveis.add(findViewById(R.id.rainha_disponivel_7));
        listaRainhasDisponiveis.add(findViewById(R.id.rainha_disponivel_8));
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
        if (!tempoRodando) {
            cronometro.start();
            tempoRodando = true;
        }

        if(!jogo.jogada(row, col)){
            // rainhas esgotadas
            cronometro.stop();
            AlertDialog.Builder builder = new AlertDialog.Builder(TelaJogo.this);
            builder.setTitle("Você perdeu! Limite atingido");
            builder.setMessage("Deseja jogar novamente?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = getIntent();
                    finish();
                    overridePendingTransition(0, 0); // Desabilita a animação
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(TelaJogo.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder.show();
        }

        else if (jogo.isJogoGanho()) {
            String tituloDialogo;
            String mensagemDialogo;
            cronometro.stop();
            long tempoDecorridoMs = SystemClock.elapsedRealtime() - cronometro.getBase();
            if (tempoDecorridoMs < recorde) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong("recorde_" + tamanho_tabuleiro, tempoDecorridoMs);
                editor.apply();

                tituloDialogo = "Novo Recorde!";
                mensagemDialogo = "Você venceu com o tempo de " + formatarTempo(tempoDecorridoMs) + "!\n\nDeseja jogar novamente?";
                txtRecorde.setText(formatarTempo(tempoDecorridoMs));
            } else {
                tituloDialogo = "Você Ganhou!";
                mensagemDialogo = "Seu tempo: " + formatarTempo(tempoDecorridoMs) +
                        "\nRecorde: " + formatarTempo(recorde) +
                        "\n\nDeseja jogar novamente?";
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(TelaJogo.this);
            builder.setTitle(tituloDialogo);
            builder.setMessage(mensagemDialogo);

            builder.setPositiveButton("Jogar Novamente", (dialog, which) -> {
                Intent intent = getIntent();
                finish();
                overridePendingTransition(0, 0); // Desabilita a animação
                startActivity(intent);
                overridePendingTransition(0, 0);
            });

            builder.setNegativeButton("Sair", (dialog, which) -> {
                finish();
            });

            builder.show();
            return;
        }

        atualizarTela();
    }

    private String formatarTempo(long milissegundos) {
        long segundos = milissegundos / 1000;
        long minutos = segundos / 60;
        long segundosRestantes = segundos % 60;
        return String.format("%02d:%02d", minutos, segundosRestantes);
    }

    private void atualizarTela() {
        if (jogo.isJogoGanho()) {
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
            atualizarContadorDeRainhas(jogo.getQtdRainhas());
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