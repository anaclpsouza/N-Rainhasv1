package classes;
import android.graphics.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Jogo {

    private final int tamanhoDoTab;
    private final int[][] tabuleiro; // A matriz que representa o jogo: 0 = vazio, 1 = rainha
    private int qtdRainhas;

    // Variáveis que guardam o estado atual do jogo
    private boolean jogoGanho = false;
    private ArrayList<Point> rainhasEmConflito = new ArrayList<>();

    public Jogo(int tamanho) {
        this.tamanhoDoTab = tamanho;
        this.tabuleiro = new int[tamanho][tamanho];
        this.qtdRainhas = tamanho;
    }

    public boolean jogada(int row, int col) {
        if (tabuleiro[row][col] == 1) {
            //remove
            tabuleiro[row][col] = 0;
            qtdRainhas++;
            return true;
        } else {
            //se todas as rainhas já tiverem sido posicionadas
            if (qtdRainhas == 0) {
                return false;
            }
            tabuleiro[row][col] = 1;
            qtdRainhas--;
        }

        // Após qualquer movimento, atualiza o estado geral do jogo (conflitos, vitória)
        updateGameState();
        return true;
    }

    private void updateGameState() {
        // Encontra os conflitos atuais
        this.rainhasEmConflito = findConflicts();

        // Verifica a condição de vitória
        this.jogoGanho = (qtdRainhas == 0) && this.rainhasEmConflito.isEmpty();
    }

    // --- MÉTODOS DE CONSULTA (GETTERS) ---
    // A tela usará estes métodos para saber como se desenhar.

    public boolean hasQueen(int row, int col) {
        return tabuleiro[row][col] == 1;
    }
    public ArrayList<Point> getRainhasEmConflito() {
        return findConflicts();
    }
    public boolean isJogoGanho() {
        return this.jogoGanho;
    }
    public int getTamanhoDoTab() {
        return tamanhoDoTab;
    }

    public int getQtdRainhas(){return qtdRainhas;}

    private ArrayList<Point> findConflicts() {
        Set<Point> rainhasEmConflito = new HashSet<>();
        List<Point> posRainhas = new ArrayList<>();

        for (int lin = 0; lin < tamanhoDoTab; lin++) {
            for (int col = 0; col < tamanhoDoTab; col++) {
                if (tabuleiro[lin][col] == 1) {
                    posRainhas.add(new Point(col, lin));
                }
            }
        }
        for (int i = 0; i < posRainhas.size(); i++) {
            for (int j = i + 1; j < posRainhas.size(); j++) {
                Point q1 = posRainhas.get(i);
                Point q2 = posRainhas.get(j);

                if (q1.y == q2.y || q1.x == q2.x || Math.abs(q1.y - q2.y) == Math.abs(q1.x - q2.x)) {
                    rainhasEmConflito.add(q1);
                    rainhasEmConflito.add(q2);
                }
            }
        }
        return new ArrayList<>(rainhasEmConflito);
    }
}