package com.example.estampasderamx;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class inventario_estampa_w extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> listaTotal = new ArrayList<>();
    private List<String> disponibles = new ArrayList<>();
    private AdapterEstampas adapter;

    private DatabaseReference refEstampas, refEspeciales, refAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_estampa_w);

        recyclerView = findViewById(R.id.recyclerInventario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // IMPORTANTE: Inicializar listas vacías
        listaTotal.clear();
        disponibles.clear();

        adapter = new AdapterEstampas(listaTotal, disponibles, 15, "");
        recyclerView.setAdapter(adapter);

        refEstampas = FirebaseDatabase.getInstance().getReference("DBS_UltimateWarriors/estampas");
        refEspeciales = FirebaseDatabase.getInstance().getReference("DBS_UltimateWarriors/especialesx");
        refAB = FirebaseDatabase.getInstance().getReference("DBS_UltimateWarriors/AB");

        // Ejecutar cargas
        cargarNodo(refEstampas, "");
        cargarNodo(refEspeciales, "⭐");
        cargarNodo(refAB, "🃏");

        Button btnRegresar = findViewById(R.id.btnRegresar);
        Button btnFinalizar = findViewById(R.id.btnFinalizar);

        btnRegresar.setOnClickListener(v -> {
            guardarLocalmente(); // Guardar también al regresar
            finish();
        });

        btnFinalizar.setOnClickListener(v -> {
            guardarLocalmente();
            finishAffinity();
        });
    }

    private void guardarLocalmente() {
        SharedPreferences pref = getSharedPreferences("Stock_UltimateWarriors", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Map<String, Integer> datos = adapter.getInventario();
        if (datos != null) {
            for (Map.Entry<String, Integer> entry : datos.entrySet()) {
                editor.putInt("id_" + entry.getKey(), entry.getValue());
            }
            editor.commit(); // commit es síncrono y obligatorio aquí
        }
    }

    private void cargarLocalmente() {
        SharedPreferences pref = getSharedPreferences("Stock_UltimateWarriors", MODE_PRIVATE);
        Map<String, Integer> mapaActual = adapter.getInventario();

        for (String n : listaTotal) {
            // Solo cargamos del SharedPreferences lo que NO esté ya en el mapa con valor > 0
            int cantidadGuardada = pref.getInt("id_" + n, 0);
            if (cantidadGuardada > 0) {
                mapaActual.put(n, cantidadGuardada);
            }
        }
        adapter.setInventario(mapaActual);
    }

    private void cargarNodo(DatabaseReference ref, String prefijo) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String numero = prefijo + child.getKey();
                    String estado = child.child("estado").getValue(String.class);

                    if (!listaTotal.contains(numero)) {
                        listaTotal.add(numero);
                        if ("D".equals(estado)) {
                            disponibles.add(numero);
                        }
                    }
                }
                // Sincronizar después de obtener los IDs de Firebase
                cargarLocalmente();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Por si el usuario usa el botón físico de "Atrás" del celular
    @Override
    public void onBackPressed() {
        guardarLocalmente();
        super.onBackPressed();
    }
}