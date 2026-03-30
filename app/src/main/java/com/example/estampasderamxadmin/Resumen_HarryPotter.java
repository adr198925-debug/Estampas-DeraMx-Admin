package com.example.estampasderamxadmin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Resumen_HarryPotter extends AppCompatActivity {
    private static final int TOTAL_ESTAMPAS = 254;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_coleccion_harrypotter);

        // 1. Referencias
        TextView txtConteo = findViewById(R.id.txt_conteo);
        ProgressBar barra = findViewById(R.id.progreso_barra);

        ImageView btn_ir = findViewById(R.id.btn_ir);
        Button btnRegresar = findViewById(R.id.btnRegresar);

        // 2. Recibir (Asegúrate que el nombre sea el mismo que en el putExtra)
        int resultadoD = getIntent().getIntExtra("PUNTOS_HP", 0);

        // 3. Mostrar datos
        if (txtConteo != null) {
            txtConteo.setText("Estampas " + resultadoD + " / " + TOTAL_ESTAMPAS);
            txtConteo.setTextColor(Color.WHITE);
        }

        // 4. Progreso
        int totalPosible = 24;
        int porcentaje = (resultadoD * 100) / totalPosible;

        if (barra != null) {
            barra.setProgress(porcentaje);
            btn_ir.setOnClickListener(v -> {
                Intent intent = new Intent(Resumen_HarryPotter.this, Inventario_estampa_hp.class);
                startActivity(intent);
            });

            // BOTÓN REGRESAR
            btnRegresar.setOnClickListener(v -> {
                Intent intent = new Intent(Resumen_HarryPotter.this, MenuActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}
