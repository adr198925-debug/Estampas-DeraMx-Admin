package com.example.estampasderamx;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Resumen_CaptainTsubasa extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_coleccion_captaintsubasa);

        // Referencias UI
        TextView txtConteo = findViewById(R.id.txt_conteo);
        ProgressBar barra = findViewById(R.id.progreso_barra);
        ImageView btn_ir = findViewById(R.id.btn_ir);
        Button btnRegresar = findViewById(R.id.btnRegresar);

        // Recibir datos
        int resultadoCTS = getIntent().getIntExtra("PUNTOS_CTS", 0);

        // Total del álbum
        int totalPosible = 200;

        // Mostrar conteo
        txtConteo.setText(resultadoCTS + " / " + totalPosible);
        txtConteo.setTextColor(Color.WHITE);

        // Configurar barra
        barra.setMax(totalPosible);
        barra.setProgress(resultadoCTS);
        // BOTÓN DE INVENTARIO
        btn_ir.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_CaptainTsubasa.this, Inventario_estampa_ct.class);
            startActivity(intent);
        });

        // BOTÓN REGRESAR
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_CaptainTsubasa.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}