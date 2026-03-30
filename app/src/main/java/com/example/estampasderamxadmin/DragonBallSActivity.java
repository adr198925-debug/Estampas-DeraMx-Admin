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

public class DragonBallSActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView titulo;
    private Spinner spinnerFiltro; // 1. Declarar el Spinner

    private AdapterEstampas adapter;

    private final List<String> listaTotal = new ArrayList<>(); // Lista que viene de Firebase
    private final List<String> listaFiltrada = new ArrayList<>(); // 2. Lista que verá el usuario
    private final List<String> estampasDisponibles = new ArrayList<>();

    private DatabaseReference ref;

    private boolean modoEspeciales = false;
    private String rutaActual;
    private int contadorD_Normales = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dragonballs);

        titulo = findViewById(R.id.titulo_DragonBallS);
        spinnerFiltro = findViewById(R.id.spinner_filtro_db); // Inicializar Spinner

        recyclerView = findViewById(R.id.recyclerViewAlbum);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        recyclerView.setHasFixedSize(true);

        // 3. Importante: El adapter ahora debe usar 'listaFiltrada'
        adapter = new AdapterEstampas(listaFiltrada, estampasDisponibles, 0, "");
        recyclerView.setAdapter(adapter);

        configurarSpinner(); // Llamar a la configuración del filtro

        Button btnRegresar = findViewById(R.id.btnRegresar);
        Button btnSiguiente = findViewById(R.id.btnSiguiente);

        btnRegresar.setOnClickListener(v -> manejarRegreso());
        btnSiguiente.setOnClickListener(v -> manejarSiguiente());

        rutaActual = "DragonBall/estampas";
        cargarDatos();
    }

    private void configurarSpinner() {
        String[] opciones = {"Todas", "Disponibles", "Faltantes"};

        // Usamos el layout más básico que ya existe en Android
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, opciones) {

            // ESTO ES LO QUE CAMBIA EL COLOR SIN LAYOUTS EXTRAS
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(android.graphics.Color.WHITE); // Aquí pones el color que quieras
                ((TextView) v).setTextSize(12); // Opcional: ajustar el tamaño
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextColor(android.graphics.Color.BLACK); // Color de la lista al abrirse
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

                // Al terminar de cargar, aplicar el filtro actual
                aplicarFiltro(spinnerFiltro.getSelectedItem().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void manejarRegreso() {
        if (modoEspeciales) {
            modoEspeciales = false;
            contadorD_Normales = 0;
            titulo.setText("Dragon Ball S");
            rutaActual = "DragonBall/estampas";
            cargarDatos();
        } else {
            finish();
        }
    }

    private void manejarSiguiente() {
        if (!modoEspeciales) {
            contadorD_Normales = estampasDisponibles.size();
            modoEspeciales = true;
            titulo.setText("DragonBalls P");
            rutaActual = "DragonBall/especiales";
            cargarDatos();
        } else {
            int totalFinalD = contadorD_Normales + estampasDisponibles.size();
            Intent intent = new Intent(this, Resumen_DragonBallS_Activity.class);
            intent.putExtra("RESULTADO_D", totalFinalD);
            startActivity(intent);
            finish();
        }
    }
}