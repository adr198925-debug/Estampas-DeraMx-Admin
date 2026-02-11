package com.example.estampasderamxadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Referencia al botón
        Button btnAbrirMenu = findViewById(R.id.btn_DragonBall);

        btnAbrirMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MenuDragonBallActivity.class);
                startActivity(intent);
            }
        });
    } // <--- ESTA LLAVE CIERRA EL ONCREATE
} // <--- ESTA LLAVE CIERRA LA CLASE