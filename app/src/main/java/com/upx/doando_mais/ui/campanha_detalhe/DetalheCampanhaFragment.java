package com.upx.doando_mais.ui.campanha_detalhe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment; // <-- A linha que faltava
import com.upx.doando_mais.databinding.FragmentDetalheCampanhaBinding;

public class DetalheCampanhaFragment extends Fragment {

    private FragmentDetalheCampanhaBinding binding;
    // TODO criar o ViewModel e o NavController

    public DetalheCampanhaFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla o layout
        binding = FragmentDetalheCampanhaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Mostra o "Carregando..." enquanto buscamos os dados
        binding.progressBarDetalhe.setVisibility(View.VISIBLE);

        // TODO Aqui virá a lógica para buscar os dados
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpa a referência do binding
    }
}