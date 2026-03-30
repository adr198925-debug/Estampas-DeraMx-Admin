package com.example.estampasderamxadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Resumen_Dragon_Ball_UCE extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_coleccion_dragonball_uce);

        // Referencias UI
        TextView txtConteo = findViewById(R.id.txt_conteo);
        ProgressBar barra = findViewById(R.id.progreso_barra);
        ImageView btn_ir = findViewById(R.id.btn_ir);
        Button btnRegresar = findViewById(R.id.btnRegresar);

        // Recibir datos del Intent
        int totalRecibido = getIntent().getIntExtra("PUNTOS_UCT", 0);

        // Total del álbum
        int totalPosible = 400;

        // Mostrar conteo
        txtConteo.setText(totalRecibido + " / " + totalPosible);

        // Configurar barra
        barra.setMax(totalPosible);
        barra.setProgress(totalRecibido);
        btn_ir.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Dragon_Ball_UCE.this, Inventario_estampa_uce.class);
            startActivity(intent);
        });

        // BOTÓN REGRESAR
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Dragon_Ball_UCE.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}