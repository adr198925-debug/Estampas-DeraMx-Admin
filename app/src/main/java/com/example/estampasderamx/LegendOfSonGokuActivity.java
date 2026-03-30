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

public class LegendOfSonGokuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView titulo;
    private Spinner spinnerFiltro;

    private AdapterEstampas adapter;

    private final List<String> listaTotal = new ArrayList<>();
    private final List<String> listaFiltrada = new ArrayList<>();
    private final List<String> estampasDisponibles = new ArrayList<>();

    private DatabaseReference ref;

    private final String RUTA = "DBS_Thelegendofsongoku/estampas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dragonballs_thelegendofsongoku);

        titulo = findViewById(R.id.titulo_Thelegendofsongoku);
        spinnerFiltro = findViewById(R.id.spinner_filtro_db);
        titulo.setText("The Legend Of Son Goku");

        recyclerView = findViewById(R.id.recyclerViewAlbum);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        recyclerView.setHasFixedSize(true);

        // El adapter usa 'listaFiltrada' para que el filtro funcione desde el inicio
        adapter = new AdapterEstampas(listaFiltrada, estampasDisponibles, 2, RUTA);
        recyclerView.setAdapter(adapter);

        configurarSpinner();

        Button btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> finish());

        Button btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(v -> {
            int total = estampasDisponibles.size();
            Intent intent = new Intent(this, Resumen_DragonBall_SG_Activity.class);
            intent.putExtra("PUNTOS_LEGEND", total);
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

    private void cargarDatos() {
        // MUY IMPORTANTE: Decirle al adaptador en qué ruta estamos
        adapter.setRutaActual(RUTA);

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

                // CORRECCIÓN: Llamamos al filtro para llenar la lista visible de inmediato
                if (spinnerFiltro != null && spinnerFiltro.getSelectedItem() != null) {
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