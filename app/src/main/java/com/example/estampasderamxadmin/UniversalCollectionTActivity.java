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

public class UniversalCollectionTActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView titulo;
    private Spinner spinnerFiltro; // Declarar Spinner

    private AdapterEstampas adapter;

    private final List<String> listaTotal = new ArrayList<>();
    private final List<String> listaFiltrada = new ArrayList<>(); // Lista para el filtro
    private final List<String> estampasDisponibles = new ArrayList<>();

    private DatabaseReference ref;
    private String rutaActual;

    private int nivelActual = 0; // 0=D, 1=Z, 2=GT, 3=S
    private int c_D = 0, c_Z = 0, c_GT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dragoballs_universalcollectiont);

        titulo = findViewById(R.id.titulo_UniversalCollectionT);
        spinnerFiltro = findViewById(R.id.spinner_filtro_db); // Inicializar Spinner

        recyclerView = findViewById(R.id.recyclerViewAlbum);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        recyclerView.setHasFixedSize(true);

        // El adapter usa listaFiltrada para que el filtro responda
        adapter = new AdapterEstampas(listaFiltrada, estampasDisponibles, 3, "");
        recyclerView.setAdapter(adapter);

        configurarSpinner(); // Configurar colores y lógica del filtro

        Button btnRegresar = findViewById(R.id.btnRegresar);
        Button btnSiguiente = findViewById(R.id.btnSiguiente);

        btnRegresar.setOnClickListener(v -> manejarRegreso());
        btnSiguiente.setOnClickListener(v -> manejarSiguiente());

        cargarNivel(true);
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
        if (nivelActual > 0) {
            nivelActual--;
            cargarNivel(true);
        } else {
            finish();
        }
    }

    private void manejarSiguiente() {
        if (nivelActual == 0) {
            c_D = estampasDisponibles.size();
            nivelActual = 1;
        } else if (nivelActual == 1) {
            c_Z = estampasDisponibles.size();
            nivelActual = 2;
        } else if (nivelActual == 2) {
            c_GT = estampasDisponibles.size();
            nivelActual = 3;
        } else {
            int total = c_D + c_Z + c_GT + estampasDisponibles.size();
            Intent intent = new Intent(this, Resumen_Dragon_Ball_UCT.class);
            intent.putExtra("PUNTOS_UCT", total);
            startActivity(intent);
            finish();
            return;
        }
        cargarNivel(true);
    }

    private void cargarNivel(boolean resetScroll) {
        switch (nivelActual) {
            case 0: rutaActual = "DBS_UCT/D"; titulo.setText("Universal Collection - D"); break;
            case 1: rutaActual = "DBS_UCT/Z"; titulo.setText("Universal Collection - Z"); break;
            case 2: rutaActual = "DBS_UCT/GT"; titulo.setText("Universal Collection - GT"); break;
            case 3: rutaActual = "DBS_UCT/S"; titulo.setText("Universal Collection - S"); break;
        }

        adapter.setRutaActual(rutaActual);
        ref = FirebaseDatabase.getInstance().getReference(rutaActual);
        if (resetScroll) recyclerView.scrollToPosition(0);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaTotal.clear();
                estampasDisponibles.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String num = child.getKey();
                    String estado = child.child("estado").getValue(String.class);
                    if (num == null) continue;
                    listaTotal.add(num);
                    if ("D".equals(estado)) estampasDisponibles.add(num);
                }

                // LLAMADA AUTOMÁTICA AL FILTRO PARA MOSTRAR LA LISTA DE INMEDIATO
                if (spinnerFiltro != null) {
                    aplicarFiltro(spinnerFiltro.getSelectedItem().toString());
                } else {
                    aplicarFiltro("Todas");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}