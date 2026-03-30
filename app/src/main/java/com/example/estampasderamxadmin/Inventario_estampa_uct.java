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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventario_estampa_uct extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> listaTotal = new ArrayList<>();
    private List<String> disponibles = new ArrayList<>();
    private AdapterEstampas adapter;

    private DatabaseReference refD;
    private DatabaseReference refZ;
    private DatabaseReference refGT;
    private DatabaseReference refS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_estampa_dbs_uct);

        recyclerView = findViewById(R.id.recyclerInventario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Limpieza de seguridad al iniciar
        listaTotal.clear();
        disponibles.clear();

        adapter = new AdapterEstampas(listaTotal, disponibles, 15, "");
        recyclerView.setAdapter(adapter);

        refD = FirebaseDatabase.getInstance().getReference("DBS_UCT/D");
        refZ = FirebaseDatabase.getInstance().getReference("DBS_UCT/Z");
        refGT = FirebaseDatabase.getInstance().getReference("DBS_UCT/GT");
        refS = FirebaseDatabase.getInstance().getReference("DBS_UCT/S");

        cargarNodo(refD, "D");
        cargarNodo(refZ, "Z");
        cargarNodo(refGT, "GT");
        cargarNodo(refS, "S");

        Button btnRegresar = findViewById(R.id.btnRegresar);
        Button btnFinalizar = findViewById(R.id.btnFinalizar);

        // Guardar también al regresar para no perder datos al navegar hacia atrás
        btnRegresar.setOnClickListener(v -> {
            guardarLocalmente();
            finish();
        });

        btnFinalizar.setOnClickListener(v -> {
            guardarLocalmente();
            finishAffinity();
        });
    }

    private void guardarLocalmente() {
        SharedPreferences pref = getSharedPreferences("Stock_UCT", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Map<String, Integer> datos = adapter.getInventario();
        if (datos != null) {
            for (Map.Entry<String, Integer> entry : datos.entrySet()) {
                editor.putInt("id_" + entry.getKey(), entry.getValue());
            }
            // commit() asegura que se escriba físicamente antes de cualquier otra acción
            editor.commit();
        }
    }

    private void cargarLocalmente() {
        SharedPreferences pref = getSharedPreferences("Stock_UCT", MODE_PRIVATE);
        Map<String, Integer> mapaActual = adapter.getInventario();

        for (String n : listaTotal) {
            // Priorizamos la cantidad guardada si es mayor a 0 para no sobreescribir con ceros
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

                    if (numero != null && !listaTotal.contains(numero)) {
                        listaTotal.add(numero);
                        if ("D".equals(estado)) {
                            disponibles.add(numero);
                        }
                    }
                }
                // Sincronizar con el almacenamiento local después de obtener los IDs
                cargarLocalmente();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Blindaje para el botón físico de atrás del dispositivo
    @Override
    public void onBackPressed() {
        guardarLocalmente();
        super.onBackPressed();
    }
}