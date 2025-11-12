package com.upx.doando_mais.ui.feed.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.upx.doando_mais.R;
import com.upx.doando_mais.data.model.Campanha;

public class CampanhaAdapter extends ListAdapter<Campanha, CampanhaAdapter.CampanhaViewHolder> {

    // --- NOVA INTERFACE ---
    // Define um "contrato" que o Fragment deve implementar
    public interface OnCampanhaClickListener {
        void onCampanhaClick(Campanha campanha);
    }
    // ----------------------

    private final OnCampanhaClickListener listener; // Armazena o listener

    // --- CONSTRUTOR MODIFICADO ---
    // Agora o adapter EXIGE um listener quando é criado
    public CampanhaAdapter(OnCampanhaClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }
    // ---------------------------

    private static final DiffUtil.ItemCallback<Campanha> DIFF_CALLBACK = new DiffUtil.ItemCallback<Campanha>() {
        @Override
        public boolean areItemsTheSame(@NonNull Campanha oldItem, @NonNull Campanha newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Campanha oldItem, @NonNull Campanha newItem) {
            return oldItem.getTitulo().equals(newItem.getTitulo()) &&
                    oldItem.getDescricao().equals(newItem.getDescricao());
        }
    };

    @NonNull
    @Override
    public CampanhaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_campanha, parent, false);
        return new CampanhaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampanhaViewHolder holder, int position) {
        Campanha campanha = getItem(position);
        holder.bind(campanha);
    }

    // --- VIEW HOLDER MODIFICADO ---
    class CampanhaViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitulo;
        private TextView tvDescricao;
        private TextView tvLocal;

        public CampanhaViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitulo = itemView.findViewById(R.id.tv_item_titulo);
            tvDescricao = itemView.findViewById(R.id.tv_item_descricao);
            tvLocal = itemView.findViewById(R.id.tv_item_local);

            // A lógica de clique agora é simples:
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Campanha campanhaClicada = getItem(position);
                    // Chama o "contrato" (listener) que o Fragment nos deu
                    listener.onCampanhaClick(campanhaClicada);
                }
            });
        }

        public void bind(Campanha campanha) {
            tvTitulo.setText(campanha.getTitulo());
            tvDescricao.setText(campanha.getDescricao());

            if (campanha.getNomePaciente() != null && !campanha.getNomePaciente().isEmpty()) {
                tvLocal.setText("Paciente: " + campanha.getNomePaciente() + " | Local: " + campanha.getNomeHemocentro());
            } else {
                tvLocal.setText("Local: " + campanha.getNomeHemocentro());
            }
        }
    }
}