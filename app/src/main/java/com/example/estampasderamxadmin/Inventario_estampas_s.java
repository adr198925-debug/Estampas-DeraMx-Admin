package com.example.estampasderamxadmin;

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

public class Inventario_estampas_s extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterEstampas adapter;
    private List<String> listaTotal;
    private List<String> disponibles;


    private ValueEventListener listener;

    private DatabaseReference refE;
    private DatabaseReference refEsp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_estampa_s);

        recyclerView = findViewById(R.id.recyclerInventario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaTotal = new ArrayList<>();
        disponibles = new ArrayList<>();

        // 🔥 MODO 15 = INVENTARIO
        adapter = new AdapterEstampas(listaTotal, disponibles, 15, "");
        recyclerView.setAdapter(adapter);

        refE = FirebaseDatabase.getInstance().getReference("DragonBall/estampas");
        refEsp = FirebaseDatabase.getInstance().getReference("DragonBall/especiales");

        cargarNodo(refE, "");
        cargarNodo(refEsp, "");

        Button btnRegresar = findViewById(R.id.btnRegresar);
        Button btnFinalizar = findViewById(R.id.btnFinalizar);


        // Guardar al regresar para no perder datos al navegar hacia atrás
        btnRegresar.setOnClickListener(v -> {
            guardarLocalmente();
            finish();
        });

        // Guardar al finalizar (sin System.exit para asegurar el commit)
        btnFinalizar.setOnClickListener(v -> {
            guardarLocalmente();
            finishAffinity();
        });
    }

    private void guardarLocalmente() {
        // CORREGIDO: getSharedPreferences (sin el "Get" doble)
        SharedPreferences pref = getSharedPreferences("Stock_DragonBallS", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Map<String, Integer> datos = adapter.getInventario();
        if (datos != null) {
            for (Map.Entry<String, Integer> entry : datos.entrySet()) {
                editor.putInt("id_" + entry.getKey(), entry.getValue());
            }
            // commit() síncrono para asegurar la persistencia física
            editor.commit();
        }
    }

    private void cargarLocalmente() {
        // CORREGIDO: getSharedPreferences (sin el "Get" doble)
        SharedPreferences pref = getSharedPreferences("Stock_DragonBallS", MODE_PRIVATE);
        Map<String, Integer> mapaActual = adapter.getInventario();

        for (String n : listaTotal) {
            // Priorizamos la cantidad guardada si es mayor a 0
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
                // Sincronizar con el almacenamiento local
                cargarLocalmente();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    }
