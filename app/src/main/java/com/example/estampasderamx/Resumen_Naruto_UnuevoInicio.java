package com.example.estampasderamx;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Resumen_Naruto_UnuevoInicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_coleccion_naruto_unuevoinicio);

        // Referencias UI
        TextView txtSeccion1 = findViewById(R.id.txt1);
        ProgressBar barra1 = findViewById(R.id.progreso_barra_1);

        TextView txtSeccion2 = findViewById(R.id.txt2);
        ProgressBar barra2 = findViewById(R.id.progreso_barra_2);
        ImageView btn_ir = findViewById(R.id.btn_ir);
        Button btnRegresar = findViewById(R.id.btnRegresar);

        // Datos recibidos
        int sumaNormalesP = getIntent().getIntExtra("SUMA_NORMALES_P", 0);
        int totalEspecialesC = getIntent().getIntExtra("TOTAL_ESPECIALES_C", 0);

        // Totales del álbum
        int maxNormalesP = 186;
        int maxEspecialesC = 50;

        // BLOQUE 1
        txtSeccion1.setText("Estampas: " + sumaNormalesP + " / " + maxNormalesP);
        txtSeccion1.setTextColor(Color.WHITE);

        barra1.setMax(maxNormalesP);
        barra1.setProgress(sumaNormalesP);

        // BLOQUE 2
        txtSeccion2.setText("Especiales C: " + totalEspecialesC + " / " + maxEspecialesC);
        txtSeccion2.setTextColor(Color.WHITE);

        barra2.setMax(maxEspecialesC);
        barra2.setProgress(totalEspecialesC);
        btn_ir.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Naruto_UnuevoInicio.this, Inventario_estampa_uni.class);
            startActivity(intent);
        });

        // BOTÓN REGRESAR
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Naruto_UnuevoInicio.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}