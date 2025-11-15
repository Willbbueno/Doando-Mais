package com.upx.doando_mais.ui.orientacoes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.upx.doando_mais.databinding.FragmentOrientacoesBinding; // Importe o Binding

public class OrientacoesFragment extends Fragment {

    private FragmentOrientacoesBinding binding;

    public OrientacoesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla o layout usando ViewBinding
        binding = FragmentOrientacoesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Nenhuma lógica de Java é necessária aqui,
        // pois o layout XML é 100% estático.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpa a referência do binding
    }
}