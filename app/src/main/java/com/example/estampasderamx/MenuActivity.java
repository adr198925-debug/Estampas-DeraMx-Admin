package com.example.estampasderamx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        configurarBoton(R.id.btn_DragonBall, MenuDragonBallActivity.class);
        configurarBoton(R.id.btn_Naruto, MenuNarutoActivity.class);
        configurarBoton(R.id.btn_CPTS, CaptainTsubasa.class);
        configurarBoton(R.id.btn_SPV, SpiderverseActivity.class);
        configurarBoton(R.id.btn_MB, MarioBros.class);
        configurarBoton(R.id.btn_potter, HarryPotter.class);
        configurarBoton(R.id.btn_CDZ, CaballerosDelZodiaco.class);
        configurarBoton(R.id.btn_Yugioh, Yugioh.class);

    }

    private void configurarBoton(int idBoton, Class<?> activityDestino) {

        Button boton = findViewById(idBoton);

        boton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, activityDestino);
            startActivity(intent);
        });
    }



}