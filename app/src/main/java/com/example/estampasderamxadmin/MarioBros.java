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

public class MarioBros extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView titulo;
    private Spinner spinnerFiltro; // 1. Declarar Spinner

    private final List<String> listaTotal = new ArrayList<>();
    private final List<String> listaFiltrada = new ArrayList<>(); // 2. Lista para el filtro
    private final List<String> estampasDisponibles = new ArrayList<>();

    private AdapterEstampas adapter;
    private DatabaseReference ref;

    private String rutaActual;
    private int nivelActual = 0; // 0=estampas, 1=especiales, 2=tarjetas
    private int conteoEstampas = 0;
    private int conteoEspeciales = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mariobros);

        titulo = findViewById(R.id.titulo_mb);
        spinnerFiltro = findViewById(R.id.spinner_filtro_db); // Inicializar Spinner

        recyclerView = findViewById(R.id.recyclerViewAlbum);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        recyclerView.setHasFixedSize(true);

        // 3. El adapter ahora usa 'listaFiltrada'
        adapter = new AdapterEstampas(listaFiltrada, estampasDisponibles, 11, "");
        recyclerView.setAdapter(adapter);

        configurarSpinner(); // Configuración de colores y lógica del filtro

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
            cargarespeciales();
        } else if (nivelActual == 1) {
            nivelActual = 0;
            cargarEstampas();
        } else {
            finish();
        }
    }

    private void manejarSiguiente() {
        if (nivelActual == 0) {
            conteoEstampas = estampasDisponibles.size();
            nivelActual = 1;
            cargarespeciales();
        } else if (nivelActual == 1) {
            conteoEspeciales = estampasDisponibles.size();
            nivelActual = 2;
            cargartarjetas();
        } else {
            int sumaEspecialesYNormales = conteoEstampas + conteoEspeciales;
            int totalTarjetas = estampasDisponibles.size();

            Intent intent = new Intent(this, Resumen_MarioBros.class);
            intent.putExtra("SUMA_ESTAMPAS_ESPECIALES", sumaEspecialesYNormales);
            intent.putExtra("TOTAL_TARJETAS", totalTarjetas);

            startActivity(intent);
            finish();
        }
    }

    private void cargarEstampas() {
        titulo.setText("Mario Bros es hora\nde jugar");
        rutaActual = "MarioBros/estampas";
        actualizarReferencia();
    }

    private void cargarespeciales() {
        titulo.setText("Mario M");
        rutaActual = "MarioBros/especialesM";
        actualizarReferencia();
    }

    private void cargartarjetas() {
        titulo.setText("Mario Bros Tarjetas");
        rutaActual = "MarioBros/tarjetas";
        actualizarReferencia();
    }

    private void actualizarReferencia() {
        adapter.setRutaActual(rutaActual);
        ref = FirebaseDatabase.getInstance().getReference(rutaActual);
        cargarDatos();
        recyclerView.scrollToPosition(0);
    }

    private void cargarDatos() {
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

                // LLAMADA AUTOMÁTICA AL FILTRO: Para mostrar la lista de inmediato
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