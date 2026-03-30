package com.example.estampasderamxadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Resumen_Naruto_Ninjadventures extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_coleccion_naruto_ninjadventures);

        // Referencias UI
        TextView txtConteo = findViewById(R.id.txt_conteo);
        ProgressBar barra = findViewById(R.id.progreso_barra);
        ImageView btn_ir = findViewById(R.id.btn_ir);
        Button btnRegresar = findViewById(R.id.btnRegresar);

        // Recibir datos
        int totalRecibido = getIntent().getIntExtra("PUNTOS_NA", 0);

        // Total del álbum
        int totalPosible = 176;

        // Mostrar conteo
        txtConteo.setText(totalRecibido + " / " + totalPosible);

        // Configurar barra
        barra.setMax(totalPosible);
        barra.setProgress(totalRecibido);
        btn_ir.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Naruto_Ninjadventures.this, Inventario_estampa_na.class);
            startActivity(intent);
        });

        // BOTÓN REGRESAR
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_Naruto_Ninjadventures.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}