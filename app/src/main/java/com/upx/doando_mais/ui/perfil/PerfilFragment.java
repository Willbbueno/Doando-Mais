package com.upx.doando_mais.ui.perfil;

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
import com.google.firebase.auth.FirebaseUser; // Import para o FirebaseUser
import com.google.firebase.auth.FirebaseAuth; // Import para a data de criação
import com.upx.doando_mais.R;
import com.upx.doando_mais.data.model.Usuario;
import com.upx.doando_mais.databinding.FragmentPerfilBinding; // Import o NOVO binding
import com.upx.doando_mais.ui.auth.AuthViewModel;
import java.text.SimpleDateFormat; // Import para formatar data
import java.util.Locale; // Import para formatar data

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding; // Binding para o NOVO layout
    private AuthViewModel authViewModel; // ViewModel compartilhado
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

        // 1. Inicializa o NavController
        navController = Navigation.findNavController(view);

        // 2. Pega o ViewModel compartilhado (da Activity)
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // 3. Configura os observadores e cliques
        configurarObservadores();
        configurarCliques();
    }

    private void configurarObservadores() {
        // --- Observa os DADOS COMPLETOS do usuário (Nome, CPF, etc.) ---
        authViewModel.getDadosUsuarioLiveData().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                // Preenche a tela com os dados do Firestore
                preencherDadosDoUsuario(usuario);
            }
        });

        // --- Observa o STATUS DE LOGIN (para o logout) ---
        authViewModel.getUsuarioLogadoLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser == null) {
                // O usuário fez logout!
                // Navega de volta para a tela de login
                navController.navigate(R.id.loginFragment, null,
                        new androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.nav_graph, true) // Limpa toda a pilha de telas
                                .build()
                );
            } else {
                // Usuário está logado, busca a data de criação (bônus)
                preencherDataCriacao(firebaseUser);
            }
        });
    }

    private void configurarCliques() {
        // Botão de Sair (Logout)
        binding.btnSair.setOnClickListener(v -> {
            authViewModel.logout();
        });

        // --- ⬇️ NOVOS CLIQUES DA REATORAÇÃO ⬇️ ---

        // Botão "Atualizar Cadastro"
        binding.btnAtualizarCadastro.setOnClickListener(v -> {
            // Ação que já existia
            navController.navigate(R.id.action_perfilFragment_to_atualizarCadastroFragment);
        });

        // Botão "Alterar Senha"
        binding.btnAlterarSenha.setOnClickListener(v -> {
            // Nova Ação
            navController.navigate(R.id.action_perfilFragment_to_alterarSenhaFragment);
        });

        // Botão "Sobre o App"
        binding.btnSobreApp.setOnClickListener(v -> {
            // Nova Ação
            navController.navigate(R.id.action_perfilFragment_to_sobreAppFragment);
        });

        // Botão "Excluir Conta"
        binding.btnExcluirConta.setOnClickListener(v -> {
            // Nova Ação
            navController.navigate(R.id.action_perfilFragment_to_excluirContaFragment);
        });

        // Botão "Editar Foto" (FAB)
        binding.fabEditarFoto.setOnClickListener(v -> {
            // TODO: Implementar lógica de abrir galeria/câmera
            Toast.makeText(getContext(), "Função 'Editar Foto' será implementada.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Preenche a tela com os dados do objeto Usuario vindo do Firestore
     */
    private void preencherDadosDoUsuario(Usuario usuario) {
        binding.tvPerfilNome.setText(usuario.getNomeCompleto()); // Corrigido para getNomeCompleto()
        binding.tvPerfilEmail.setText(usuario.getEmail());

        binding.tvPerfilTelefone.setText(usuario.getTelefone() != null ? usuario.getTelefone() : "Não informado");
        binding.tvPerfilCpf.setText(usuario.getCpf() != null ? usuario.getCpf() : "Não informado");
        binding.tvPerfilDataNasc.setText(usuario.getDataNascimento() != null ? usuario.getDataNascimento() : "Não informado");
        binding.tvPerfilSexo.setText(usuario.getSexo() != null ? usuario.getSexo() : "Não informado");

        String local = (usuario.getCidade() != null ? usuario.getCidade() : "") +
                (usuario.getEstado() != null ? " - " + usuario.getEstado() : "");
        binding.tvPerfilLocalizacao.setText(local.isEmpty() ? "Não informado" : local);

        // TODO: Implementar lógica de upload/download de foto (Fase 3)
        // Por enquanto, o tools:src do XML está mostrando um avatar
    }

    /**
     * Busca a data de criação da conta (Firebase Auth)
     */
    private void preencherDataCriacao(FirebaseUser firebaseUser) {
        if (firebaseUser.getMetadata() != null) {
            long timestamp = firebaseUser.getMetadata().getCreationTimestamp();
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM, yyyy", new Locale("pt", "BR"));
            binding.tvPerfilMembroDesde.setText(sdf.format(new java.util.Date(timestamp)));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpa a referência do binding
    }
}