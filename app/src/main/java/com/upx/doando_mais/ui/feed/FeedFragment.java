package com.upx.doando_mais.ui.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.upx.doando_mais.databinding.FragmentFeedBinding; // Importe o ViewBinding
import com.upx.doando_mais.ui.feed.adapter.CampanhaAdapter; // Importe o Adapter

public class FeedFragment extends Fragment {

    private FeedViewModel feedViewModel;
    private FragmentFeedBinding binding; // Binding para o fragment_feed.xml
    private CampanhaAdapter adapter; // Nosso adapter da lista

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla o layout usando ViewBinding
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializa o ViewModel
        feedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        // 2. Configura o Adapter e o RecyclerView
        setupRecyclerView();

        // 3. Configura os Observadores
        setupObservers();

        // 4. Manda o ViewModel carregar os dados
        // (Mostra o "Carregando...")
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.rvCampanhas.setVisibility(View.GONE);
        binding.tvFeedVazio.setVisibility(View.GONE);
        feedViewModel.carregarCampanhas();
    }

    private void setupRecyclerView() {
        adapter = new CampanhaAdapter();
        binding.rvCampanhas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCampanhas.setAdapter(adapter);
    }

    private void setupObservers() {
        // Observa a lista de campanhas
        feedViewModel.getCampanhasLiveData().observe(getViewLifecycleOwner(), campanhas -> {
            binding.progressBar.setVisibility(View.GONE); // Esconde o "Carregando"

            if (campanhas != null && !campanhas.isEmpty()) {
                // Se a lista não for vazia, mostra a lista
                adapter.submitList(campanhas);
                binding.rvCampanhas.setVisibility(View.VISIBLE);
                binding.tvFeedVazio.setVisibility(View.GONE);
            } else {
                // Se for vazia, mostra a mensagem "Feed Vazio"
                binding.rvCampanhas.setVisibility(View.GONE);
                binding.tvFeedVazio.setVisibility(View.VISIBLE);
            }
        });

        // Observa erros
        feedViewModel.getErroLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), erro, Toast.LENGTH_LONG).show();
                binding.tvFeedVazio.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpa a referência do binding
    }
}