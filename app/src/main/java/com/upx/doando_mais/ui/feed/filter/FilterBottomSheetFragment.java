package com.upx.doando_mais.ui.feed.filter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.upx.doando_mais.R; // Importe o R
import com.upx.doando_mais.databinding.BottomSheetFiltroBinding; // Importe o ViewBinding

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    private BottomSheetFiltroBinding binding;
    private FilterListener mListener;

    // --- 1. Interface para devolver os dados ---
    // O FeedFragment vai implementar isso
    public interface FilterListener {
        void onFilterApplied(String tipoSanguineo, String cidade);
    }

    public static FilterBottomSheetFragment newInstance() {
        return new FilterBottomSheetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetFiltroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 2. Popula o Spinner de Tipo Sanguíneo
        popularSpinnerTipoSanguineo();

        // 3. Configura o clique do botão "Aplicar"
        binding.btnAplicarFiltros.setOnClickListener(v -> {
            String tipoSanguineo = binding.spinnerFiltroTipoSanguineo.getText().toString();
            String cidade = binding.etFiltroCidade.getText().toString().trim();

            // Manda os dados de volta para o FeedFragment
            if (mListener != null) {
                mListener.onFilterApplied(tipoSanguineo, cidade);
            }
            dismiss(); // Fecha o menu
        });

        // 4. Configura o clique do botão "Limpar"
        binding.btnLimparFiltros.setOnClickListener(v -> {
            binding.spinnerFiltroTipoSanguineo.setText("Todos");
            binding.etFiltroCidade.setText("");

            // Manda os dados limpos ("null") de volta
            if (mListener != null) {
                mListener.onFilterApplied(null, null);
            }
            dismiss(); // Fecha o menu
        });
    }

    private void popularSpinnerTipoSanguineo() {
        // Pega o array de tipos sanguíneos que você já tem em strings.xml
        String[] tiposSanguineos = getResources().getStringArray(R.array.array_tipo_sanguineo);

        // Adiciona "Todos" como primeira opção
        String[] opcoesFiltro = new String[tiposSanguineos.length + 1];
        opcoesFiltro[0] = "Todos";
        System.arraycopy(tiposSanguineos, 0, opcoesFiltro, 1, tiposSanguineos.length);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                opcoesFiltro
        );
        binding.spinnerFiltroTipoSanguineo.setAdapter(adapter);
    }

    // --- 5. Conecta o Listener ao Fragment ---
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Garante que o Fragment "pai" (FeedFragment) implementou o listener
        if (getParentFragment() instanceof FilterListener) {
            mListener = (FilterListener) getParentFragment();
        } else if (context instanceof FilterListener) {
            mListener = (FilterListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " ou o Fragment pai deve implementar FilterListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}