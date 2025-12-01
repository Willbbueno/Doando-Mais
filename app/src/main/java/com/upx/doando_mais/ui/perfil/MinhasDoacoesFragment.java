package com.upx.doando_mais.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.upx.doando_mais.databinding.FragmentMinhasDoacoesBinding;
import com.upx.doando_mais.ui.doacao_registrar.DoacaoAdapter;

public class MinhasDoacoesFragment extends Fragment {

    private FragmentMinhasDoacoesBinding binding;
    private MinhasDoacoesViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMinhasDoacoesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MinhasDoacoesViewModel.class);

        DoacaoAdapter adapter = new DoacaoAdapter();
        binding.rvDoacoes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDoacoes.setAdapter(adapter);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getDoacoesLiveData().observe(getViewLifecycleOwner(), lista -> {
            binding.progressBar.setVisibility(View.GONE);
            adapter.setLista(lista);
        });

        viewModel.carregarDoacoes();
    }
}