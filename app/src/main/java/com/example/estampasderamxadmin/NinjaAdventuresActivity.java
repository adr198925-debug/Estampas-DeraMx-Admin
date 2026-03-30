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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class NinjaAdventuresActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView titulo;
    private Spinner spinnerFiltro; // 1. Declarar Spinner

    private final List<String> listaTotal = new ArrayList<>();
    private final List<String> listaFiltrada = new ArrayList<>(); // 2. Lista para el filtro
    private final List<String> estampasDisponibles = new ArrayList<>();

    private AdapterEstampas adapter;
    private DatabaseReference ref;

    private boolean modoEspeciales = false;
    private String rutaActual;

    private int conteoBase = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.naruto_ninjaadventures);

        titulo = findViewById(R.id.titulo_NinjaAdventure);
        spinnerFiltro = findViewById(R.id.spinner_filtro_db); // Inicializar Spinner

        recyclerView = findViewById(R.id.recyclerViewAlbum);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        recyclerView.setHasFixedSize(true);

        // 3. El adapter ahora usa 'listaFiltrada'
        adapter = new AdapterEstampas(listaFiltrada, estampasDisponibles, 8, "");
        recyclerView.setAdapter(adapter);

        configurarSpinner(); // Configuración de colores y lógica del filtro

        Button btnRegresar = findViewById(R.id.btnRegresar);
        Button btnSiguiente = findViewById(R.id.btnSiguiente);

        btnRegresar.setOnClickListener(v -> manejarRegreso());
        btnSiguiente.setOnClickListener(v -> manejarSiguiente());

        rutaActual = "Naruto_NinjaAdventures/estampas";
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
                if (estampasDisponibles.contains(num)) listaFiltrada.add(num);
            }
        } else if (criterio.equals("Faltantes")) {
            for (String num : listaTotal) {
                if (!estampasDisponibles.contains(num)) listaFiltrada.add(num);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void manejarRegreso() {
        if (modoEspeciales) {
            modoEspeciales = false;
            titulo.setText("Ninja Adventures");
            rutaActual = "Naruto_NinjaAdventures/estampas";
            cargarEstampas();
        } else {
            finish();
        }
    }

    private void manejarSiguiente() {
        if (!modoEspeciales) {
            conteoBase = estampasDisponibles.size();
            modoEspeciales = true;
            titulo.setText("Ninja Adventures X");
            rutaActual = "Naruto_NinjaAdventures/especiales";
            cargarEstampas();
        } else {
            int totalFinal = conteoBase + estampasDisponibles.size();
            Intent intent = new Intent(this, Resumen_Naruto_Ninjadventures.class);
            intent.putExtra("PUNTOS_NA", totalFinal);
            startActivity(intent);
            finish();
        }
    }

    private void cargarEstampas() {
        adapter.setRutaActual(rutaActual);
        ref = FirebaseDatabase.getInstance().getReference(rutaActual);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaTotal.clear();
                estampasDisponibles.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String numero = child.getKey();
                    String estado = child.child("estado").getValue(String.class);
                    if (numero == null) continue;
                    listaTotal.add(numero);
                    if ("D".equals(estado)) {
                        estampasDisponibles.add(numero);
                    }
                }

                // LLAMADA AUTOMÁTICA AL FILTRO
                if (spinnerFiltro != null) {
                    aplicarFiltro(spinnerFiltro.getSelectedItem().toString());
                } else {
                    aplicarFiltro("Todas");
                }
                recyclerView.scrollToPosition(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}