package com.upx.doando_mais.ui.feed.adapter;


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

    // 1: Definir os "tipos de view" ---
    // Vamos usar números para identificar nossos layouts
    private static final int VIEW_TYPE_PACIENTE = 1;
    private static final int VIEW_TYPE_PUBLICA = 2;

    public interface OnCampanhaClickListener {
        void onCampanhaClick(Campanha campanha);
    }

    private final OnCampanhaClickListener listener;

    public CampanhaAdapter(OnCampanhaClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Campanha> DIFF_CALLBACK = new DiffUtil.ItemCallback<Campanha>() {
        @Override
        public boolean areItemsTheSame(@NonNull Campanha oldItem, @NonNull Campanha newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Campanha oldItem, @NonNull Campanha newItem) {
            // Adicionamos o tipoCampanha na verificação de conteúdo
            return oldItem.getTitulo().equals(newItem.getTitulo()) &&
                    oldItem.getTipoCampanha().equals(newItem.getTipoCampanha());
        }
    };

    // 2: Dizer ao Adapter qual layout usar ---
    @Override
    public int getItemViewType(int position) {
        Campanha campanha = getItem(position);
        if (campanha.getTipoCampanha() != null && campanha.getTipoCampanha().equals("Paciente")) {
            return VIEW_TYPE_PACIENTE;
        } else {
            return VIEW_TYPE_PUBLICA;
        }
    }

    @NonNull
    @Override
    public CampanhaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 3: Carregar o layout CORRETO ---
        // Agora, inflamos o layout baseado no viewType que definimos acima
        View view;
        if (viewType == VIEW_TYPE_PACIENTE) {
            // Infla o card vermelho claro
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_campanha_paciente, parent, false);
        } else {
            // Infla o card turquesa claro
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_campanha_publica, parent, false);
        }
        return new CampanhaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampanhaViewHolder holder, int position) {
        Campanha campanha = getItem(position);
        holder.bind(campanha);
    }

    // ViewHolder preenche os campos
    class CampanhaViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitulo;
        private TextView tvLocal;
        private TextView tvTipoSanguineo; // O chip no layout

        public CampanhaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Os IDs são os mesmos em ambos os layouts (item_campanha_paciente/publica)
            tvTitulo = itemView.findViewById(R.id.tv_item_titulo);
            tvLocal = itemView.findViewById(R.id.tv_item_local);
            tvTipoSanguineo = itemView.findViewById(R.id.chip_tipo_sanguineo); // É um TextView (Chip)

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Campanha campanhaClicada = getItem(position);
                    // O listener (contrato) funciona igual para ambos os fragments
                    listener.onCampanhaClick(campanhaClicada);
                }
            });
        }

        public void bind(Campanha campanha) {
            tvTitulo.setText(campanha.getTitulo());
            tvLocal.setText(campanha.getNomeHemocentro());
            tvTipoSanguineo.setText(campanha.getTipoSanguineoNecessario());
        }
    }
}