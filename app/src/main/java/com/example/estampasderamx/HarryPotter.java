package com.example.estampasderamx;

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

public class HarryPotter extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView titulo;
    private Spinner spinnerFiltro; // 1. Declarar Spinner

    private List<String> listaTotal;
    private List<String> listaFiltrada; // 2. Lista para el filtro
    private List<String> estampasDisponibles;

    private DatabaseReference ref;
    private ValueEventListener listener;
    private AdapterEstampas adapter;

    private String rutaActual;

    // 0 = estampas | 1 = tarjetas
    private int nivelActual = 0;
    private int conteoEstampas = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harrypotter);

        titulo = findViewById(R.id.titulo_harry);
        spinnerFiltro = findViewById(R.id.spinner_filtro_db); // Inicializar Spinner

        recyclerView = findViewById(R.id.recyclerViewAlbum);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));

        listaTotal = new ArrayList<>();
        listaFiltrada = new ArrayList<>();
        estampasDisponibles = new ArrayList<>();

        // 3. El adapter ahora usa 'listaFiltrada'
        adapter = new AdapterEstampas(listaFiltrada, estampasDisponibles, 12, "");
        recyclerView.setAdapter(adapter);

        configurarSpinner(); // Configuración de colores y lógica del filtro

        Button btnRegresar = findViewById(R.id.btnRegresar);
        Button btnSiguiente = findViewById(R.id.btnSiguiente);

        btnRegresar.setOnClickListener(v -> {
            if (nivelActual == 1) {
                nivelActual = 0;
                cargarEstampas();
            } else {
                finish();
            }
        });

        btnSiguiente.setOnClickListener(v -> {
            if (nivelActual == 0) {
                conteoEstampas = estampasDisponibles.size();
                nivelActual = 1;
                cargarTarjetas();
            } else {
                int totalFinalHP = conteoEstampas + estampasDisponibles.size();
                Intent intent = new Intent(this, Resumen_HarryPotter.class);
                intent.putExtra("PUNTOS_HP", totalFinalHP);
                startActivity(intent);
                finish();
            }
        });

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
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void aplicarFiltro(String criterio) {
        listaFiltrada.clear();
        if (criterio.equals("Todas")) {
            listaFiltrada.addAll(listaTotal);
        } else if (criterio.equals("Disponibles")) {
            for (String num : listaTotal) {
                if (estampasDisponibles.contains(num)) listaFiltrada.add(num);
            }
        } else if (criterio.equals("Faltantes")) {
            for (String num : listaTotal) {
                if (!estampasDisponibles.contains(num)) listaFiltrada.add(num);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void cargarEstampas() {
        titulo.setText("Harry Potter");
        rutaActual = "Harry_Potter/estampas";
        cargarDatos();
    }

    private void cargarTarjetas() {
        titulo.setText("Harry Potter Tarjetas");
        rutaActual = "Harry_Potter/tarjetas";
        cargarDatos();
    }

    private void cargarDatos() {
        if (listener != null && ref != null) {
            ref.removeEventListener(listener);
        }

        adapter.setRutaActual(rutaActual);
        ref = FirebaseDatabase.getInstance().getReference(rutaActual);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                // LLAMADA AUTOMÁTICA AL FILTRO: Carga la lista de inmediato
                if (spinnerFiltro != null) {
                    aplicarFiltro(spinnerFiltro.getSelectedItem().toString());
                } else {
                    aplicarFiltro("Todas");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        ref.addValueEventListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null && ref != null) {
            ref.removeEventListener(listener);
        }
    }
}