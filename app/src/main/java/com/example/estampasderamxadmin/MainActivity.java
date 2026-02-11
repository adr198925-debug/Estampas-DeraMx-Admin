package com.example.estampasderamxadmin;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    private TextView texto;
    private ImageView imgFondo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Diseño de Carga Azul

        texto = findViewById(R.id.textoPorcentaje);
        imgFondo = findViewById(R.id.imgGokuGif);

        if (imgFondo != null) {
            Glide.with(this).load(R.drawable.fondo_goku).into(imgFondo);
        }

        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(5000); // 5 segundos de carga
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progreso = (int) animation.getAnimatedValue();
                if (texto != null) {
                    texto.setText(progreso + "%");
                }
                if (progreso == 100) {
                    // CUANDO LLEGA AL 100, VA AL MENÚ
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        animator.start();
    }
}