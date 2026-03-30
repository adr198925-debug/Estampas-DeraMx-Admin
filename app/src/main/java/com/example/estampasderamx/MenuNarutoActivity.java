package com.example.estampasderamx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuNarutoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.naruto);

        configurarBoton(R.id.btn_Unuevoinicio, UnnuevoinicioActivity.class);
        configurarBoton(R.id.btn_Hokage, HokageActivity.class);
        configurarBoton(R.id.btn_NA, NinjaAdventuresActivity.class);

        Button btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> {
            startActivity(new Intent(this, MenuActivity.class));
            finish();
        });

        // BOTÓN AGREGAR

    }

    private void configurarBoton(int idBoton, Class<?> destino) {
        Button boton = findViewById(idBoton);
        boton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuNarutoActivity.this, destino);
            startActivity(intent);
        });
    }


    }
