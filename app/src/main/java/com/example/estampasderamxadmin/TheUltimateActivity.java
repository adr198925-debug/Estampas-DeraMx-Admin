package com.example.estampasderamxadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TheUltimateActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView titulo;
    private Spinner spinnerFiltro; // 1. Declarar Spinner

    private AdapterEstampas adapter;

    private final List<String> listaTotal = new ArrayList<>();
    private final List<String> listaFiltrada = new ArrayList<>(); // 2. Lista para el filtro
    private final List<String> estampasDisponibles = new ArrayList<>();

    private DatabaseReference ref;
    private ValueEventListener listener;
    private String rutaActual;

    private boolean modoEspeciales = false;
    private int contadorD_Normales = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dragonballs_theultimate);

        titulo = findViewById(R.id.titulo_TheUltimate);
        spinnerFiltro = findViewById(R.id.spinner_filtro_db); // Inicializar Spinner

        recyclerView = findViewById(R.id.recyclerViewAlbum);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        recyclerView.setHasFixedSize(true);

        // 3. El adapter ahora usa 'listaFiltrada'
        adapter = new AdapterEstampas(listaFiltrada, estampasDisponibles, 0, "");
        recyclerView.setAdapter(adapter);

        configurarSpinner(); // Configurar lógica y colores del filtro

        Button btnRegresar = findViewById(R.id.btnRegresar);
        Button btnSiguiente = findViewById(R.id.btnSiguiente);

        btnRegresar.setOnClickListener(v -> {
            if (modoEspeciales) {
                modoEspeciales = false;
                titulo.setText("Dragon Ball TheUltimate");
                rutaActual = "DBS_TheUltimate/estampas";
                cargarEstampas();
            } else {
                finish();
            }
        });

        btnSiguiente.setOnClickListener(v -> {
            if (!modoEspeciales) {
                contadorD_Normales = estampasDisponibles.size();
                modoEspeciales = true;
                titulo.setText("Dragon Ball F");
                rutaActual = "DBS_TheUltimate/especiales";
                cargarEstampas();
            } else {
                int totalFinalD = contadorD_Normales + estampasDisponibles.size();
                Intent intent = new Intent(TheUltimateActivity.this, Resumen_Dragon_Ball_Ultimate.class);
                intent.putExtra("PUNTOS_D", totalFinalD);
                startActivity(intent);
                finish();
            }
        });

        rutaActual = "DBS_TheUltimate/estampas";
        cargarEstampas();
    }

    private void configurarSpinner() {
        String[] opciones = {"Todas", "Disponibles", "Faltantes"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, opciones) {

            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(android.graphics.Color.WHITE);
                ((TextView) v).setTextSize(12);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextColor(android.graphics.Color.BLACK);
                return v;
            }
        };

        spinnerFiltro.setAdapter(spinnerAdapter);

        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltro(opciones[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void aplicarFiltro(String criterio) {
        listaFiltrada.clear();

        if (criterio.equals("Todas")) {
            listaFiltrada.addAll(listaTotal);
        } else if (criterio.equals("Disponibles")) {
            for (String num : listaTotal) {
                if (estampasDisponibles.contains(num)) {
                    listaFiltrada.add(num);
                }
            }
        } else if (criterio.equals("Faltantes")) {
            for (String num : listaTotal) {
                if (!estampasDisponibles.contains(num)) {
                    listaFiltrada.add(num);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void cargarEstampas() {
        if (listener != null && ref != null) {
            ref.removeEventListener(listener);
        }

        adapter.setRutaActual(rutaActual);
        ref = FirebaseDatabase.getInstance().getReference(rutaActual);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int posicionActual = ((GridLayoutManager) recyclerView.getLayoutManager())
                        .findFirstVisibleItemPosition();

                listaTotal.clear();
                estampasDisponibles.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String numero = child.getKey();
                    String estado = child.child("estado").getValue(String.class);

                    if (numero != null) {
                        listaTotal.add(numero);
                        if ("D".equals(estado)) {
                            estampasDisponibles.add(numero);
                        }
                    }
                }

                // 4. LLAMADA AUTOMÁTICA: Para que la lista cargue sola al entrar
                if (spinnerFiltro != null) {
                    aplicarFiltro(spinnerFiltro.getSelectedItem().toString());
                } else {
                    aplicarFiltro("Todas");
                }

                recyclerView.scrollToPosition(posicionActual);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        ref.addValueEventListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null && ref != null) ref.removeEventListener(listener);
    }
}