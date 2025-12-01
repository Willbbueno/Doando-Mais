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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.upx.doando_mais.R;
import com.upx.doando_mais.data.model.Usuario;
import com.upx.doando_mais.databinding.FragmentFeedBinding;
import com.upx.doando_mais.ui.auth.AuthViewModel;
import com.upx.doando_mais.ui.feed.adapter.CampanhaAdapter;
import com.upx.doando_mais.ui.feed.filter.FilterBottomSheetFragment;

public class FeedFragment extends Fragment implements FilterBottomSheetFragment.FilterListener {

    private FeedViewModel feedViewModel;
    private AuthViewModel authViewModel;
    private FragmentFeedBinding binding;
    private CampanhaAdapter adapter;
    private String filtroTipoSanguineoAtual = null;
    private String filtroCidadeAtual = null;

    public FeedFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        feedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupClickListeners();
    }

    private void setupRecyclerView() {
        adapter = new CampanhaAdapter(campanha -> {
            NavController navController = Navigation.findNavController(requireView());
            Bundle bundle = new Bundle();
            bundle.putString("campanhaId", campanha.getId());
            navController.navigate(R.id.action_feedFragment_to_detalheCampanhaFragment, bundle);
        });

        binding.rvCampanhas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCampanhas.setAdapter(adapter);
    }

    private void setupObservers() {
        authViewModel.getDadosUsuarioLiveData().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                preencherCabecalho(usuario);
            }
        });

        feedViewModel.getCampanhasLiveData().observe(getViewLifecycleOwner(), campanhas -> {
            if (campanhas != null && !campanhas.isEmpty()) {
                adapter.submitList(campanhas);
                binding.rvCampanhas.setVisibility(View.VISIBLE);
            } else {
                binding.rvCampanhas.setVisibility(View.GONE);
            }
        });

        feedViewModel.getErroLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                Toast.makeText(getContext(), erro, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void preencherCabecalho(Usuario usuario) {
        binding.tvGreeting.setText("Olá,\n" + usuario.getNomeCompleto());
        binding.tvHeaderTipoSanguineo.setText(usuario.getTipoSanguineo());

        binding.tvHeaderDoacoesCount.setText(String.valueOf(usuario.getQuantidadeDoacoes()));

        String doacoesStr = usuario.getQuantidadeDoacoes() == 1 ? "vida salva\naté agora!" : "vidas salvas\naté agora!";
        binding.tvHeaderDoacoesText.setText(doacoesStr);

        if (usuario.getUrlFotoPerfil() != null && !usuario.getUrlFotoPerfil().isEmpty()) {
            Glide.with(this)
                    .load(usuario.getUrlFotoPerfil())
                    .placeholder(R.drawable.ic_perfil_placeholder)
                    .error(R.drawable.ic_perfil_placeholder)
                    .into(binding.ivUserPhoto);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_perfil_placeholder)
                    .into(binding.ivUserPhoto);
        }
    }

    private void setupClickListeners() {
        // Botão de Filtro
        binding.btnFiltro.setOnClickListener(v -> {
            FilterBottomSheetFragment bottomSheet = FilterBottomSheetFragment.newInstance();
            bottomSheet.show(getChildFragmentManager(), "FilterBottomSheet");
        });

        binding.btnAdicionarDoacao.setOnClickListener(v -> {
            // Navega para a tela de registro (usando o ID que está no nav_graph)
            Navigation.findNavController(requireView()).navigate(R.id.registrarDoacaoFragment);
        });
    }

    @Override
    public void onFilterApplied(String tipoSanguineo, String cidade) {
        this.filtroTipoSanguineoAtual = (tipoSanguineo != null && tipoSanguineo.equals("Todos")) ? null : tipoSanguineo;
        this.filtroCidadeAtual = (cidade != null && cidade.isEmpty()) ? null : cidade;
        feedViewModel.carregarCampanhasFiltradas(filtroTipoSanguineoAtual, filtroCidadeAtual);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Força a atualização dos dados do usuário (incluindo o contador)
        if (authViewModel != null && authViewModel.getUsuarioLogadoLiveData().getValue() != null) {
            String uid = authViewModel.getUsuarioLogadoLiveData().getValue().getUid();

            authViewModel.recarregarUsuario();
        }
    }
}