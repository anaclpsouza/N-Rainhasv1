package classes;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.n_rainhas.R;

public class Musica extends Service {
    private MediaPlayer mp;

    // Ações para o Intent
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PAUSE = "PAUSE";
    public static final String ACTION_STOP = "STOP";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Cria o MediaPlayer apenas uma vez, quando o serviço é iniciado.
        mp = MediaPlayer.create(getApplicationContext(), R.raw.musica_classica);
        mp.setLooping(true); // Toca em loop
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_PLAY:
                    play();
                    break;
                case ACTION_PAUSE:
                    pause();
                    break;
                case ACTION_STOP:
                    stop();
                    break;
            }
        }
        // START_NOT_STICKY garante que o serviço não reinicie sozinho se for morto pelo sistema.
        return START_NOT_STICKY;
    }

    private void play() {
        // Verifica se o MediaPlayer existe e não está tocando antes de dar start.
        if (mp != null && !mp.isPlaying()) {
            mp.start();
        }
    }

    private void pause() {
        // Pausa a música se ela estiver tocando.
        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    private void stop() {
        // Para a música, libera os recursos e encerra o serviço.
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
        stopSelf(); // Encerra o serviço.
    }

    @Override
    public void onDestroy() {
        // Garante que os recursos sejam liberados ao destruir o serviço.
        if (mp != null) {
            mp.release();
            mp = null;
        }
        super.onDestroy();
    }
}