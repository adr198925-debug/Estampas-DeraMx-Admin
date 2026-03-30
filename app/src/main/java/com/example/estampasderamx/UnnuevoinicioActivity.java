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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class UnnuevoinicioActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView titulo;
    private Spinner spinnerFiltro; // 1. Declarar Spinner

    private AdapterEstampas adapter;

    private final List<String> listaTotal = new ArrayList<>();
    private final List<String> listaFiltrada = new ArrayList<>(); // 2. Lista para el filtro
    private final List<String> estampasDisponibles = new ArrayList<>();

    private DatabaseReference ref;
    private String rutaActual;

    // 0 = estampas, 1 = P, 2 = especiales C
    private int nivelActual = 0;
    private int contador_Normales = 0;
    private int contador_P = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.naruto_nuevoinicio);

        titulo = findViewById(R.id.titulo_Unuevoinicio);
        spinnerFiltro = findViewById(R.id.spinner_filtro_db); // Inicializar Spinner

        recyclerView = findViewById(R.id.recyclerViewAlbum);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        recyclerView.setHasFixedSize(true);

        // 3. El adapter usa listaFiltrada
        adapter = new AdapterEstampas(listaFiltrada, estampasDisponibles, 6, "");
        recyclerView.setAdapter(adapter);

        configurarSpinner(); // Configurar colores y lógica

        Button btnRegresar = findViewById(R.id.btnRegresar);
        Button btnSiguiente = findViewById(R.id.btnSiguiente);

        btnRegresar.setOnClickListener(v -> manejarRegreso());
        btnSiguiente.setOnClickListener(v -> manejarSiguiente());

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
        if (nivelActual == 2) {
            nivelActual = 1;
            cargarP();
        } else if (nivelActual == 1) {
            nivelActual = 0;
            cargarEstampas();
        } else {
            finish();
        }
    }

    private void manejarSiguiente() {
        if (nivelActual == 0) {
            contador_Normales = estampasDisponibles.size();
            nivelActual = 1;
            cargarP();
        } else if (nivelActual == 1) {
            contador_P = estampasDisponibles.size();
            nivelActual = 2;
            cargarEspecialesC();
        } else {
            int sumaNormalesP = contador_Normales + contador_P;
            int totalEspecialesC = estampasDisponibles.size();
            Intent intent = new Intent(this, Resumen_Naruto_UnuevoInicio.class);
            intent.putExtra("SUMA_NORMALES_P", sumaNormalesP);
            intent.putExtra("TOTAL_ESPECIALES_C", totalEspecialesC);
            startActivity(intent);
            finish();
        }
    }

    private void cargarEstampas() {
        titulo.setText("Un nuevo inicio");
        rutaActual = "Naruto_UnuevoInicio/estampas";
        cargarDatos();
    }

    private void cargarP() {
        titulo.setText("Un nuevo inicio P");
        rutaActual = "Naruto_UnuevoInicio/especialP";
        cargarDatos();
    }

    private void cargarEspecialesC() {
        titulo.setText("Un nuevo inicio - Especiales C");
        rutaActual = "Naruto_UnuevoInicio/especialesc";
        cargarDatos();
    }

    private void cargarDatos() {
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