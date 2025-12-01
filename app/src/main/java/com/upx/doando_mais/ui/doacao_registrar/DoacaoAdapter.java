package com.upx.doando_mais.ui.doacao_registrar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.upx.doando_mais.R;
import com.upx.doando_mais.data.model.Doacao;
import java.util.ArrayList;
import java.util.List;

public class DoacaoAdapter extends RecyclerView.Adapter<DoacaoAdapter.DoacaoViewHolder> {

    private List<Doacao> lista = new ArrayList<>();

    public void setLista(List<Doacao> novaLista) {
        this.lista = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DoacaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doacao, parent, false);
        return new DoacaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoacaoViewHolder holder, int position) {
        Doacao doacao = lista.get(position);
        holder.bind(doacao);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    class DoacaoViewHolder extends RecyclerView.ViewHolder {
        TextView tvData, tvLocal, tvTipo;
        ImageView ivComprovante;
        Button btnVer;

        public DoacaoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvData = itemView.findViewById(R.id.tv_item_data);
            tvLocal = itemView.findViewById(R.id.tv_item_local);
            tvTipo = itemView.findViewById(R.id.tv_item_tipo);
            ivComprovante = itemView.findViewById(R.id.iv_comprovante);
            btnVer = itemView.findViewById(R.id.btn_ver_comprovante);
        }

        void bind(Doacao doacao) {
            tvData.setText(doacao.getDataDoacao());
            tvLocal.setText(doacao.getLocal());
            tvTipo.setText(doacao.getTipoSanguineo());

            // Lógica para mostrar/esconder comprovante
            if (doacao.getUrlComprovante() != null && !doacao.getUrlComprovante().isEmpty()) {
                btnVer.setVisibility(View.VISIBLE);
                ivComprovante.setVisibility(View.GONE); // Começa escondido

                btnVer.setOnClickListener(v -> {
                    if (ivComprovante.getVisibility() == View.VISIBLE) {
                        ivComprovante.setVisibility(View.GONE);
                        btnVer.setText("Ver Comprovante");
                    } else {
                        ivComprovante.setVisibility(View.VISIBLE);
                        btnVer.setText("Ocultar Comprovante");
                        Glide.with(itemView.getContext()).load(doacao.getUrlComprovante()).into(ivComprovante);
                    }
                });
            } else {
                btnVer.setVisibility(View.GONE);
                ivComprovante.setVisibility(View.GONE);
            }
        }
    }
}