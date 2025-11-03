package com.upx.doando_mais.ui.feed.adapter;
// Para a lista do RecycleView
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.upx.doando_mais.R;
import com.upx.doando_mais.data.model.Campanha;

public class CampanhaAdapter extends ListAdapter<Campanha, CampanhaAdapter.CampanhaViewHolder> {

    public CampanhaAdapter() {
        super(DIFF_CALLBACK);
    }

    // Callback para o ListAdapter calcular as diferenças (para animações)
    private static final DiffUtil.ItemCallback<Campanha> DIFF_CALLBACK = new DiffUtil.ItemCallback<Campanha>() {
        @Override
        public boolean areItemsTheSame(@NonNull Campanha oldItem, @NonNull Campanha newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Campanha oldItem, @NonNull Campanha newItem) {
            // Verifique campos que podem mudar
            return oldItem.getTitulo().equals(newItem.getTitulo()) &&
                    oldItem.getDescricao().equals(newItem.getDescricao());
        }
    };

    @NonNull
    @Override
    public CampanhaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do item que criamos: item_campanha.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_campanha, parent, false);
        return new CampanhaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampanhaViewHolder holder, int position) {
        // Pega a campanha da posição atual
        Campanha campanha = getItem(position);

        // "Binda" (conecta) os dados da campanha aos TextViews do layout do item
        holder.bind(campanha);
    }

    // --- ViewHolder ---
    // Representa cada item individual (cada card) na lista
    class CampanhaViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitulo;
        private TextView tvDescricao;
        private TextView tvLocal;

        public CampanhaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Encontra os TextViews dentro do item_campanha.xml
            tvTitulo = itemView.findViewById(R.id.tv_item_titulo);
            tvDescricao = itemView.findViewById(R.id.tv_item_descricao);
            tvLocal = itemView.findViewById(R.id.tv_item_local);

            // (Opcional) Adiciona um clique no card
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Campanha campanhaClicada = getItem(position);
                    // TODO: Navegar para a tela de Detalhes da Campanha
                    // Ex: navController.navigate(R.id.action_feed_to_detalhe);
                }
            });
        }

        public void bind(Campanha campanha) {
            // Preenche os dados
            tvTitulo.setText(campanha.getTitulo());
            tvDescricao.setText(campanha.getDescricao());

            // Exemplo de como juntar dados (pode ajustar)
            if (campanha.getNomePaciente() != null && !campanha.getNomePaciente().isEmpty()) {
                tvLocal.setText("Paciente: " + campanha.getNomePaciente() + " | Local: " + campanha.getNomeHemocentro());
            } else {
                tvLocal.setText("Local: " + campanha.getNomeHemocentro());
            }
        }
    }
}