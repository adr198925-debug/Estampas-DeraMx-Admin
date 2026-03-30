package com.example.estampasderamxadmin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

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

        barra = findViewById(R.id.loadingCircle);
        texto = findViewById(R.id.textoPorcentaje);
        imgFondo = findViewById(R.id.imgGokuGif);

        // 🔥 AQUÍ ESTABA EL ERROR
        Glide.with(this)
                .asGif()
                .load(R.drawable.fondo_goku) // tu gif
                .into(imgFondo);

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
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            handler.post(() -> {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            });

        }).start();
    }
}
