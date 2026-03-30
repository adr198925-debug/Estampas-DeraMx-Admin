package com.example.estampasderamx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuDragonBallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_dragonball);

        // 🔵 Dragon Ball S
        Button btnDragonBall = findViewById(R.id.btn_DragonBall);
        btnDragonBall.setOnClickListener(v -> {
            Intent intent = new Intent(MenuDragonBallActivity.this, DragonBallSActivity.class);
            startActivity(intent);
        });

        // 🟠 DBS Ultimate Warriors
        Button btnUltimateWarriors = findViewById(R.id.btn_DBN);
        btnUltimateWarriors.setOnClickListener(v -> {
            Intent intent = new Intent(MenuDragonBallActivity.this, UltimateWarriorsActivity.class);
            startActivity(intent);
        });
        // 🟠 DBS Ultimate Warriors
        Button btnTheLegendofsongoku = findViewById(R.id.btn_TLOSG);
        btnTheLegendofsongoku.setOnClickListener(v -> {
            Intent intent = new Intent(MenuDragonBallActivity.this,
                    LegendOfSonGokuActivity.class);
            startActivity(intent);
        });
        Button btnUniversalCollectionTarjetas = findViewById(R.id.btn_DBUCT);
        btnUniversalCollectionTarjetas.setOnClickListener(v -> {
            Intent intent = new Intent(MenuDragonBallActivity.this,
                    UniversalCollectionTActivity.class);
            startActivity(intent);
        });
        Button btnUniversalCollectionEstampas = findViewById(R.id.btn_DBUCE);
        btnUniversalCollectionEstampas.setOnClickListener(v -> {
            Intent intent = new Intent(MenuDragonBallActivity.this,
                    UniversalcollectionEActivity.class);
            startActivity(intent);
        });
        Button btnDRAGONBALLULTIMATE = findViewById(R.id.btn_DBU);
        btnDRAGONBALLULTIMATE.setOnClickListener(v -> {
            Intent intent = new Intent(MenuDragonBallActivity.this,
                    TheUltimateActivity.class);
            startActivity(intent);

        });
        Button btnRegresar = findViewById(R.id.btnRegresar);

        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(MenuDragonBallActivity.this, MenuActivity.class);
            startActivity(intent);
            finish(); // Opcional pero recomendado para que no se apilen pantallas
        });

    }

}
