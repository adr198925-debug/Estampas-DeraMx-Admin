package com.example.estampasderamx;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Resumen_Dragon_Ball_Ultimate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resume_coleccion_dragonball_ultimate);

        // Referencias UI
        TextView txtConteo = findViewById(R.id.txt_conteo);
        ProgressBar barra = findViewById(R.id.progreso_barra);
        ImageView btn_ir = findViewById(R.id.btn_ir);
        Button btnRegresar = findViewById(R.id.btnRegresar);

        // Recibir datos
        int resultadoD = getIntent().getIntExtra("PUNTOS_D", 0);

        // Total del álbum
        int totalPosible = 192;

        // Mostrar conteo
        txtConteo.setText(resultadoD + " / " + totalPosible);
        txtConteo.setTextColor(Color.WHITE);

        // Configurar barra
        barra.setMax(totalPosible);
        barra.setProgress(resultadoD);
        btn_ir.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Dragon_Ball_Ultimate.this, Inventario_estampa_ult.class);
            startActivity(intent);
        });

        // BOTÓN REGRESAR
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Dragon_Ball_Ultimate.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}