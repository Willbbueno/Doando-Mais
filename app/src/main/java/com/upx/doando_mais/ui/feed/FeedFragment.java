package com.upx.doando_mais.ui.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; // Importa a classe TextView
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.upx.doando_mais.R;
import com.upx.doando_mais.data.model.Usuario;
import com.upx.doando_mais.databinding.FragmentFeedBinding; // Binding para o layout da ONDA
import com.upx.doando_mais.ui.auth.AuthViewModel;
import com.upx.doando_mais.ui.feed.adapter.CampanhaAdapter;
import com.upx.doando_mais.ui.feed.filter.FilterBottomSheetFragment; // Importe o Filtro

// --- Adicione a implementação da interface ---
public class FeedFragment extends Fragment implements FilterBottomSheetFragment.FilterListener {

    private FeedViewModel feedViewModel;
    private AuthViewModel authViewModel; // Para o cabeçalho
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

        // 1. Inicializa os ViewModels
        feedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // 2. Configura o Adapter e o RecyclerView
        setupRecyclerView();

        // 3. Configura os Observadores
        setupObservers();

        // 4. Configura os cliques
        setupClickListeners();

        // (A chamada de 'carregarCampanhas()' foi removida daqui,
        // pois o ViewModel já faz isso no construtor)
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
        // Observador para o Cabeçalho
        authViewModel.getDadosUsuarioLiveData().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                preencherCabecalho(usuario);
            }
        });

        // Observa a lista de campanhas
        feedViewModel.getCampanhasLiveData().observe(getViewLifecycleOwner(), campanhas -> {
            if (campanhas != null && !campanhas.isEmpty()) {
                adapter.submitList(campanhas);
                binding.rvCampanhas.setVisibility(View.VISIBLE);
            } else {
                binding.rvCampanhas.setVisibility(View.GONE);
                // TODO: Mostrar uma view "feed vazio"
            }
        });

        // Observa erros
        feedViewModel.getErroLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                Toast.makeText(getContext(), erro, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Preenche o cabeçalho do feed com os dados do usuário
     */
    private void preencherCabecalho(Usuario usuario) {
        // Usa os IDs CORRETOS do layout da ONDA
        binding.tvGreeting.setText("Olá,\n" + usuario.getNomeCompleto());
        binding.tvHeaderTipoSanguineo.setText(usuario.getTipoSanguineo());

        // Atualiza o card de estatísticas
        binding.tvHeaderDoacoesCount.setText(String.valueOf(usuario.getQuantidadeDoacoes()));

        String doacoesStr = usuario.getQuantidadeDoacoes() == 1 ? "vida salva\naté agora!" : "vidas salvas\naté agora!";
        binding.tvHeaderDoacoesText.setText(doacoesStr);

        // TODO: Implementar o carregamento da foto de perfil
        // Glide.with(this).load(usuario.getUrlFotoPerfil()).into(binding.ivUserPhoto);
    }

    /**
     * Configura os cliques dos novos botões do layout
     */
    private void setupClickListeners() {
        // Usa o ID 'btnFiltro' do seu XML
        binding.btnFiltro.setOnClickListener(v -> {
            FilterBottomSheetFragment bottomSheet = FilterBottomSheetFragment.newInstance();

            // --- ESTA É A CORREÇÃO DO CRASH ---
            // Mostra o BottomSheet usando o FragmentManager FILHO,
            // o que torna o FeedFragment o "ParentFragment" correto.
            bottomSheet.show(getChildFragmentManager(), "FilterBottomSheet");
        });
    }

    /**
     * Este método é chamado pelo FilterBottomSheetFragment quando
     * o usuário clica em "Aplicar" ou "Limpar".
     */
    @Override
    public void onFilterApplied(String tipoSanguineo, String cidade) {
        // Guarda os filtros
        this.filtroTipoSanguineoAtual = (tipoSanguineo != null && tipoSanguineo.equals("Todos")) ? null : tipoSanguineo;
        this.filtroCidadeAtual = (cidade != null && cidade.isEmpty()) ? null : cidade;

        // Manda o ViewModel buscar a nova lista com os filtros
        feedViewModel.carregarCampanhasFiltradas(filtroTipoSanguineoAtual, filtroCidadeAtual);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}