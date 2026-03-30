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

public class HokageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView titulo;
    private Spinner spinnerFiltro; // 1. Declarar Spinner

    private List<String> listaTotal;
    private List<String> listaFiltrada; // 2. Lista para el filtro
    private List<String> estampasDisponibles;

    private DatabaseReference ref;
    private ValueEventListener listener;
    private AdapterEstampas adapter;

    private final String RUTA = "Naruto_Hokage/estampas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.naruto_hokage);

        titulo = findViewById(R.id.titulo_Hokage);
        spinnerFiltro = findViewById(R.id.spinner_filtro_db); // Inicializar Spinner
        titulo.setText("Hokage");

        recyclerView = findViewById(R.id.recyclerViewAlbum);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));

        listaTotal = new ArrayList<>();
        listaFiltrada = new ArrayList<>();
        estampasDisponibles = new ArrayList<>();

        // 3. El adapter ahora usa 'listaFiltrada'
        adapter = new AdapterEstampas(listaFiltrada, estampasDisponibles, 7, RUTA);
        recyclerView.setAdapter(adapter);

        configurarSpinner(); // Configuración de colores y lógica del filtro

        Button btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> finish());

        Button btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(v -> {
            int total = estampasDisponibles.size();
            Intent intent = new Intent(this, Resumen_Naruto_Hokage.class);
            intent.putExtra("PUNTOS_HOKAGE", total);
            startActivity(intent);
        });

        ref = FirebaseDatabase.getInstance().getReference(RUTA);
        cargarDatos();
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

    private void cargarDatos() {
        adapter.setRutaActual(RUTA);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int posicionActual = 0;
                if (recyclerView.getLayoutManager() != null) {
                    posicionActual = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                }

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

                // 4. LLAMADA AUTOMÁTICA AL FILTRO
                if (spinnerFiltro != null) {
                    aplicarFiltro(spinnerFiltro.getSelectedItem().toString());
                } else {
                    aplicarFiltro("Todas");
                }

                if (posicionActual != RecyclerView.NO_POSITION) {
                    recyclerView.scrollToPosition(posicionActual);
                }
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