package com.example.estampasderamxadmin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Resumen_DragonBallS_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_coleccion_dragonball);

        // Referencias
        TextView txtConteo = findViewById(R.id.txt_conteo);
        ProgressBar barra = findViewById(R.id.progreso_barra);
        ImageView btn_ir = findViewById(R.id.btn_ir);
        Button btnRegresar = findViewById(R.id.btnRegresar);

        // Datos recibidos
        int resultadoD = getIntent().getIntExtra("RESULTADO_D", 0);

        // Total del álbum
        int totalPosible = 204;

        // Mostrar conteo
        txtConteo.setText(resultadoD + " / " + totalPosible);
        txtConteo.setTextColor(Color.WHITE);

        // Actualizar barra
        barra.setMax(totalPosible);
        barra.setProgress(resultadoD);

        // BOTÓN DE INVENTARIO
        btn_ir.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_DragonBallS_Activity.this, Inventario_estampas_s.class);
            startActivity(intent);
        });

        // BOTÓN REGRESAR
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(Resumen_DragonBallS_Activity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}