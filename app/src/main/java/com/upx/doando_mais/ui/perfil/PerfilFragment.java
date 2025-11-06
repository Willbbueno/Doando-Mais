package com.upx.doando_mais.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.upx.doando_mais.R;
import com.upx.doando_mais.data.model.Usuario;
import com.upx.doando_mais.databinding.FragmentPerfilBinding;
import com.upx.doando_mais.ui.auth.AuthViewModel;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private AuthViewModel authViewModel; // Vamos usar o ViewModel compartilhado
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Pega o NavController
        navController = Navigation.findNavController(view);

        // Pega o ViewModel compartilhado (da Activity)
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // Configura os observadores e cliques
        configurarObservadores();
        configurarCliques();
    }

    private void configurarObservadores() {
        // --- 1. Observa os DADOS COMPLETOS do usuário (Nome, CPF, etc.) ---
        authViewModel.getDadosUsuarioLiveData().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                // Preenche a tela com os dados
                preencherDadosDoUsuario(usuario);
            }
        });

        // --- 2. Observa o STATUS DE LOGIN (para o logout) ---
        authViewModel.getUsuarioLogadoLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser == null) {
                // O usuário fez logout!
                // Navega de volta para a tela de login
                // Usamos popUpTo para limpar a pilha (não dá para "voltar" para o perfil)
                navController.navigate(R.id.loginFragment, null,
                        new androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.nav_graph, true)
                                .build()
                );
            }
        });
    }

    private void configurarCliques() {
        // Configura o clique do botão "Sair"
        binding.btnSair.setOnClickListener(v -> {
            authViewModel.logout();
        });

        // Configura o clique do botão "Editar"
        binding.btnEditarCadastro.setOnClickListener(v -> {
            // Navega para a tela de Atualizar Cadastro (usando a action do nav_graph)
            navController.navigate(R.id.action_perfilFragment_to_atualizarCadastroFragment);
        });
    }

    private void preencherDadosDoUsuario(Usuario usuario) {
        binding.tvPerfilNome.setText(usuario.getNomeCompleto());
        binding.tvPerfilEmail.setText(usuario.getEmail());
        binding.tvPerfilCpf.setText(usuario.getCpf()); // Assumindo que você tem um getCpf()
        binding.tvPerfilTipoSanguineo.setText(usuario.getTipoSanguineo()); // Assumindo que você tem um getTipoSanguineo()

        // pode adicionar mais campos aqui, como Perfil, Cidade, etc.)
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpa a referência do binding
    }
}