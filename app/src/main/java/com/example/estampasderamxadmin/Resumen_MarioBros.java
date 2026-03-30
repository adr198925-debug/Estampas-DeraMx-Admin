package com.example.estampasderamxadmin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Resumen_MarioBros extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_coleccion_mariobros);

        // BLOQUE 1
        TextView txtConteo1 = findViewById(R.id.txt2);
        ProgressBar barra1 = findViewById(R.id.progreso_barra_2);

        // BLOQUE 2
        TextView txtConteo2 = findViewById(R.id.txt1);
        ProgressBar barra2 = findViewById(R.id.progreso_barra_1);
        ImageView btn_ir = findViewById(R.id.btn_ir);
        Button btnRegresar = findViewById(R.id.btnRegresar);

        // Datos recibidos
        int sumaEstampasEspeciales = getIntent().getIntExtra("SUMA_ESTAMPAS_ESPECIALES", 0);
        int totalTarjetas = getIntent().getIntExtra("TOTAL_TARJETAS", 0);

        // Totales del álbum
        int maxEstampasEspeciales = 250;
        int maxTarjetas = 50;

        // BLOQUE 1: Estampas + Especiales
        txtConteo1.setText("Estampas " + sumaEstampasEspeciales + " / " + maxEstampasEspeciales);
        txtConteo1.setTextColor(Color.WHITE);

        barra1.setMax(maxEstampasEspeciales);
        barra1.setProgress(sumaEstampasEspeciales);

        // BLOQUE 2: Tarjetas
        txtConteo2.setText("Tarjetas " + totalTarjetas + " / " + maxTarjetas);
        txtConteo2.setTextColor(Color.WHITE);

        barra2.setMax(maxTarjetas);
        barra2.setProgress(totalTarjetas);
        btn_ir.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_MarioBros.this, Inventario_estampa_mb.class);
            startActivity(intent);
        });

        // BOTÓN REGRESAR
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_MarioBros.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}