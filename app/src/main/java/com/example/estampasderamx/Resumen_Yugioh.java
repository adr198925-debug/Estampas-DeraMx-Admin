package com.example.estampasderamx;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Resumen_Yugioh extends AppCompatActivity {

    private static final int TOTAL_ESTAMPAS = 184;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_coleccion_yugioh);

        TextView txtConteo = findViewById(R.id.txt_conteo);
        ProgressBar barra = findViewById(R.id.progreso_barra);

        ImageView btn_ir = findViewById(R.id.btn_ir);
        Button btnRegresar = findViewById(R.id.btnRegresar);

        int totalObtenido = getIntent().getIntExtra("PUNTOS_YUGI", 0);

        txtConteo.setText("Estampas " + totalObtenido + " / " + TOTAL_ESTAMPAS);
        txtConteo.setTextColor(Color.WHITE);

        barra.setMax(TOTAL_ESTAMPAS);
        barra.setProgress(totalObtenido);
        btn_ir.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Yugioh.this, Inventario_estampa_yu.class);
            startActivity(intent);
        });

        // BOTÓN REGRESAR
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Yugioh.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}