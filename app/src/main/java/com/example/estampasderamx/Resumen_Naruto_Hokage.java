package com.example.estampasderamx;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Resumen_Naruto_Hokage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_coleccion_naruto_hokage);

        // Referencias UI
        TextView txtConteo = findViewById(R.id.txt_conteo);
        ProgressBar barra = findViewById(R.id.progreso_barra);
        ImageView btn_ir = findViewById(R.id.btn_ir);
        Button btnRegresar = findViewById(R.id.btnRegresar);

        // Recibir datos del Intent
        int resultadoHokage = getIntent().getIntExtra("PUNTOS_HOKAGE", 0);

        // Total del álbum
        int totalPosible = 200;

        // Mostrar conteo
        txtConteo.setText(resultadoHokage + " / " + totalPosible);
        txtConteo.setTextColor(Color.WHITE);

        // Configurar barra
        barra.setMax(totalPosible);
        barra.setProgress(resultadoHokage);
        btn_ir.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Naruto_Hokage.this, Inventario_estampa_ho.class);
            startActivity(intent);
        });

        // BOTÓN REGRESAR
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Naruto_Hokage.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}