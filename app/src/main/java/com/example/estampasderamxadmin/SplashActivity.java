package com.example.estampasderamxadmin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar barra;
    private TextView texto;
    private ImageView imgFondo;
    private int progreso = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Vincular componentes
        barra = findViewById(R.id.loadingCircle);
        texto = findViewById(R.id.textoPorcentaje);
        imgFondo = findViewById(R.id.imgGokuGif);

        // Lógica de simulación de carga (0 a 100)
        iniciarCarga();
    }

    private void iniciarCarga() {
        new Thread(() -> {
            while (progreso < 100) {
                progreso++;
                handler.post(() -> {
                    if (texto != null) {
                        texto.setText(progreso + "%");
                    }
                });
                try {
                    Thread.sleep(40); // Velocidad de la carga (4 segundos total aprox)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Al terminar, saltar al menú (Asegúrate de tener un MenuActivity o cámbialo)
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }).start();
    }
}