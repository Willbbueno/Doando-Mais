package com.upx.doando_mais.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
// Importe o ViewBinding correto para seu layout
import com.upx.doando_mais.databinding.FragmentSobreAppBinding;

public class SobreAppFragment extends Fragment {

    private FragmentSobreAppBinding binding;

    public SobreAppFragment() {
        // Construtor vazio obrigatório
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Infla o layout usando o ViewBinding
        binding = FragmentSobreAppBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Nenhuma lógica de clique ou de dados é necessária aqui.
        // O layout XML estático já contém todas as informações.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpa a referência do binding para evitar memory leaks
    }
}