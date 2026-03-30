package com.example.estampasderamxadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterEstampas extends RecyclerView.Adapter<AdapterEstampas.ViewHolder> {

    private List<String> listaTotal;
    private List<String> disponibles;
    private int modo;
    private String rutaActual;
    private Map<String, Integer> inventario = new HashMap<>();

    public AdapterEstampas(List<String> listaTotal, List<String> disponibles, int modo, String rutaActual) {
        this.listaTotal = listaTotal;
        this.disponibles = disponibles;
        this.modo = modo;
        this.rutaActual = rutaActual;

        if (this.listaTotal != null) {
            for (String s : this.listaTotal) {
                this.inventario.put(s, 0);
            }
        }
    }

    public void setRutaActual(String rutaActual) {
        this.rutaActual = rutaActual;
    }

    public void setInventario(Map<String, Integer> nuevoInventario) {
        if (nuevoInventario != null) {
            this.inventario.putAll(nuevoInventario);
            notifyDataSetChanged();
        }
    }

    public Map<String, Integer> getInventario() {
        return (this.inventario != null) ? this.inventario : new HashMap<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (modo == 15) ? R.layout.item_inventario_s : R.layout.item_estampa;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String numero = listaTotal.get(position);

        // INVENTARIO
        if (modo == 15) {
            holder.txtNumero.setText(numero);
            int cantidad = inventario.getOrDefault(numero, 0);
            holder.txtCantidad.setText(String.valueOf(cantidad));

            holder.btnMas.setOnClickListener(v -> {
                int actual = inventario.getOrDefault(numero, 0) + 1;
                inventario.put(numero, actual);
                holder.txtCantidad.setText(String.valueOf(actual));
            });

            holder.btnMenos.setOnClickListener(v -> {
                int actual = inventario.getOrDefault(numero, 0);
                if (actual > 0) {
                    actual--;
                    inventario.put(numero, actual);
                    holder.txtCantidad.setText(String.valueOf(actual));
                }
            });
            return;
        }

        // DISPONIBILIDAD
        holder.boton.setText(numero);
        boolean estaDisponible = disponibles.contains(numero);
        configurarFondoBoton(holder.boton, estaDisponible);

        holder.boton.setOnClickListener(v -> {
            if (rutaActual == null || rutaActual.isEmpty()) return;

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(rutaActual)
                    .child(numero)
                    .child("estado");

            if (disponibles.contains(numero)) {
                disponibles.remove(numero);
                ref.setValue("ND");
            } else {
                disponibles.add(numero);
                ref.setValue("D");
            }

            notifyItemChanged(holder.getAdapterPosition());
        });
    }

    private void configurarFondoBoton(Button boton, boolean estaDisponible) {
        boton.setBackgroundResource(
                estaDisponible ? R.drawable.bg_estampa : R.drawable.bg_estampa_gris
        );
    }

    @Override
    public int getItemCount() {
        return (listaTotal != null) ? listaTotal.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button boton, btnMas, btnMenos;
        TextView txtNumero, txtCantidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            boton = itemView.findViewById(R.id.btnEstampa);
            txtNumero = itemView.findViewById(R.id.txtNumero);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            btnMas = itemView.findViewById(R.id.btnMas);
            btnMenos = itemView.findViewById(R.id.btnMenos);
        }
    }
}