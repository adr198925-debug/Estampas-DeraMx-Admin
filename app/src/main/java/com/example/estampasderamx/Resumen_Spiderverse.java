package com.example.estampasderamx;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Resumen_Spiderverse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_coleccion_spiderverse);

        TextView txtSeccion1 = findViewById(R.id.txt1);
        ProgressBar barra1 = findViewById(R.id.progreso_barra_1);

        TextView txtSeccion2 = findViewById(R.id.txt2);
        ProgressBar barra2 = findViewById(R.id.progreso_barra_2);

        ImageView btn_ir = findViewById(R.id.btn_ir);
        Button btnRegresar = findViewById(R.id.btnRegresar);

        int estampasEspeciales = getIntent().getIntExtra("ESTAMPAS_ESPECIALES", 0);
        int tarjetas = getIntent().getIntExtra("TARJETAS", 0);

        int maxEstampasEspeciales = 186;
        int maxTarjetas = 50;

        txtSeccion1.setText("Estampas: " + estampasEspeciales + " / " + maxEstampasEspeciales);
        txtSeccion1.setTextColor(Color.WHITE);

        barra1.setMax(maxEstampasEspeciales);
        barra1.setProgress(estampasEspeciales);

        txtSeccion2.setText("Tarjetas: " + tarjetas + " / " + maxTarjetas);
        txtSeccion2.setTextColor(Color.WHITE);

        barra2.setMax(maxTarjetas);
        barra2.setProgress(tarjetas);

        btn_ir.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Spiderverse.this, Inventario_estampa_spv.class);
            startActivity(intent);
        });

        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Spiderverse.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}