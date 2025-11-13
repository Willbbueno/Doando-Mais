package com.upx.doando_mais.ui.acompanhamento;

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
import com.google.firebase.auth.FirebaseUser;
import android.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.upx.doando_mais.R;
import com.upx.doando_mais.data.model.Campanha;
import com.upx.doando_mais.databinding.FragmentAcompanhamentoBinding;
import com.upx.doando_mais.ui.auth.AuthViewModel;
import com.upx.doando_mais.ui.feed.adapter.CampanhaAdapter; // Reuso do adapter do feed

public class AcompanhamentoFragment extends Fragment {

    private FragmentAcompanhamentoBinding binding;
    private AcompanhamentoViewModel acompanhamentoViewModel;
    private AuthViewModel authViewModel; // Para pegar o UID do usuário logado
    private CampanhaAdapter adapter; // O mesmo adapter do Feed

    public AcompanhamentoFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAcompanhamentoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializa os ViewModels
        acompanhamentoViewModel = new ViewModelProvider(this).get(AcompanhamentoViewModel.class);
        // Pega o AuthViewModel compartilhado da Activity
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // 2. Configura o Adapter e o RecyclerView
        setupRecyclerView();

        // 3. Configura os Observadores
        setupObservers();

        // 4. Manda o ViewModel carregar os dados
        carregarDados();
    }

    private void setupRecyclerView() {
        // Passa o NOVO listener de clique
        adapter = new CampanhaAdapter(campanha -> {
            // Ação: Mostrar o diálogo de opções
            mostrarOpcoesDialog(campanha);
        });

        binding.rvMinhasCampanhas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMinhasCampanhas.setAdapter(adapter);
    }
    private void setupObservers() {
        // Observa a lista de "Minhas Campanhas"
        acompanhamentoViewModel.getMinhasCampanhasLiveData().observe(getViewLifecycleOwner(), campanhas -> {
            binding.progressBarAcomp.setVisibility(View.GONE); // Esconde o "Carregando"

            if (campanhas != null && !campanhas.isEmpty()) {
                // Se a lista não for vazia, mostra
                adapter.submitList(campanhas);
                binding.rvMinhasCampanhas.setVisibility(View.VISIBLE);
                binding.tvAcompVazio.setVisibility(View.GONE);
            } else {
                // Se for vazia, mostra a mensagem "Vazio"
                binding.rvMinhasCampanhas.setVisibility(View.GONE);
                binding.tvAcompVazio.setVisibility(View.VISIBLE);
            }
        });

        // Observa erros
        acompanhamentoViewModel.getErroLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                binding.progressBarAcomp.setVisibility(View.GONE);
                Toast.makeText(getContext(), erro, Toast.LENGTH_LONG).show();
                binding.tvAcompVazio.setVisibility(View.VISIBLE);
            }
        });
    }

    private void carregarDados() {
        mostrarCarregando(true);

        // Pega o usuário logado (do AuthViewModel)
        FirebaseUser usuarioLogado = authViewModel.getUsuarioLogadoLiveData().getValue();

        if (usuarioLogado != null) {
            // Se tem usuário, busca as campanhas dele
            acompanhamentoViewModel.carregarMinhasCampanhas(usuarioLogado.getUid());
        } else {
            // Se não tem usuário (caso raro, mas seguro)
            mostrarCarregando(false);
            binding.tvAcompVazio.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Você não está logado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarCarregando(boolean carregando) {
        if (carregando) {
            binding.progressBarAcomp.setVisibility(View.VISIBLE);
            binding.rvMinhasCampanhas.setVisibility(View.GONE);
            binding.tvAcompVazio.setVisibility(View.GONE);
        } else {
            binding.progressBarAcomp.setVisibility(View.GONE);
        }
    }

    /**
     * Mostra um diálogo com as opções "Editar" e "Excluir"
     */
    /**
     * Mostra um diálogo com as opções "Editar" e "Excluir"
     */
    private void mostrarOpcoesDialog(Campanha campanha) {
        // Lista de opções
        CharSequence[] opcoes = {"Editar Campanha", "Excluir Campanha"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Gerenciar Campanha");
        builder.setItems(opcoes, (dialog, which) -> {
            if (which == 0) {
                // Opção 0: "Editar"
                // --- INÍCIO DA ATUALIZAÇÃO ---
                // 1. Encontra o NavController
                NavController navController = Navigation.findNavController(requireView());

                // 2. Cria o "pacote" de dados
                Bundle bundle = new Bundle();
                bundle.putString("campanhaId", campanha.getId()); // Passa o ID

                // 3. Navega para a nova tela de edição, levando o ID
                navController.navigate(R.id.action_acompanhamentoFragment_to_editarCampanhaFragment, bundle);
                // --- FIM DA ATUALIZAÇÃO ---

            } else if (which == 1) {
                // Opção 1: "Excluir"
                mostrarConfirmacaoExcluir(campanha);
            }
        });
        builder.show();
    }
    /**
     * Mostra um 2º diálogo para confirmar a exclusão
     */
    private void mostrarConfirmacaoExcluir(Campanha campanha) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja excluir a campanha '" + campanha.getTitulo() + "'? Esta ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    // Manda o ViewModel excluir
                    acompanhamentoViewModel.excluirCampanha(campanha.getId());
                    Toast.makeText(getContext(), "Campanha excluída...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null) // Não faz nada
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpa a referência do binding
    }
}