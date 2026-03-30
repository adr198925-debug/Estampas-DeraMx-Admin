package com.example.estampasderamx;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Resumen_Dragon_Ball_UCT extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_coleccion_dragonballuct);

        // Referencias UI
        TextView txtConteo = findViewById(R.id.txt_conteo);
        ProgressBar barra = findViewById(R.id.progreso_barra);
        ImageView btn_ir = findViewById(R.id.btn_ir);
        Button btnRegresar = findViewById(R.id.btnRegresar);

        // Recibir datos del Intent
        int resultadoUCT = getIntent().getIntExtra("PUNTOS_UCT", 0);

        // Total del álbum
        int totalPosible = 240;

        // Mostrar conteo
        txtConteo.setText(resultadoUCT + " / " + totalPosible);
        txtConteo.setTextColor(Color.WHITE);

        // Configurar barra
        barra.setMax(totalPosible);
        barra.setProgress(resultadoUCT);
        // BOTÓN DE INVENTARIO
        btn_ir.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Dragon_Ball_UCT.this, Inventario_estampa_uct.class);
            startActivity(intent);
        });

        // BOTÓN REGRESAR
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Dragon_Ball_UCT.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}