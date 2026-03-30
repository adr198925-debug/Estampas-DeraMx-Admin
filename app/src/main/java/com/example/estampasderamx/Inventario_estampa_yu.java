package com.example.estampasderamx;

import android.content.SharedPreferences; // Importante para persistencia local
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

public class Inventario_estampa_yu extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> listaTotal;
    private List<String> disponibles;
    private AdapterEstampas adapter;

    private DatabaseReference refE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_estampa_yu);

        recyclerView = findViewById(R.id.recyclerInventario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaTotal = new ArrayList<>();
        disponibles = new ArrayList<>();


        adapter = new AdapterEstampas(listaTotal, disponibles, 15, "");
        recyclerView.setAdapter(adapter);

        refE = FirebaseDatabase.getInstance().getReference("Yugioh/estampas");

        cargarNodo(refE, "");

        Button btnRegresar = findViewById(R.id.btnRegresar);
        Button btnFinalizar = findViewById(R.id.btnFinalizar);

        btnRegresar.setOnClickListener(v ->{
                guardarLocalmente();
        finish();
    });
        btnFinalizar.setOnClickListener(v -> {
            // Guardamos localmente antes de forzar el cierre del proceso
            guardarLocalmente();
            finishAffinity();

        });
    }

    // MÉTODO NUEVO: Guarda en la memoria del celular
    private void guardarLocalmente() {
        SharedPreferences pref = getSharedPreferences("Stock_Yugioh", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Map<String, Integer> datos = adapter.getInventario();
        if (datos != null) {
            for (Map.Entry<String, Integer> entry : datos.entrySet()) {
                editor.putInt("id_" + entry.getKey(), entry.getValue());
            }
            // commit() es vital aquí para asegurar la escritura física antes del exit
            editor.commit();
        }
    }

    // MÉTODO NUEVO: Recupera los datos guardados
    private void cargarLocalmente() {
        SharedPreferences pref = getSharedPreferences("Stock_Yugioh", MODE_PRIVATE);
        Map<String, Integer> mapaActual = adapter.getInventario();

        for (String n : listaTotal) {
            int cantidadGuardada = pref.getInt("id_" + n, 0);
            if (cantidadGuardada > 0) {
                mapaActual.put(n, cantidadGuardada);
            }
        }
            adapter.setInventario(mapaActual);
    }

        private void cargarNodo(DatabaseReference refE, String prefijo) {
            refE.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Limpiamos por si se llegara a re-ejecutar el listener
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
                // Cargamos contadores locales una vez que tenemos la lista base de Firebase
                cargarLocalmente();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}