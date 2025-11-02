package com.upx.doando_mais.ui.auth;

import android.os.Bundle;


import android.text.TextUtils;
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


import com.upx.doando_mais.R;
import com.upx.doando_mais.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    // 1. DECLARAÇÕES
    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    private NavController navController;

    public LoginFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 2. INFLAR O LAYOUT
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 3. INICIALIZAÇÃO
        navController = Navigation.findNavController(view);

        // Pega a ViewModel compartilhada (mesma do CadastroFragment)
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // 4. CONFIGURAR LISTENERS
        configurarListenersDeClique();

        // 5. OBSERVAR VIEWMODEL
        observarViewModel();
    }

    private void configurarListenersDeClique() {
        binding.btnLogin.setOnClickListener(v -> {
            // Quando o botão "Entrar" for clicado
            tentarLogin();
        });

        binding.tvRegisterLink.setOnClickListener(v -> {
            // Navega para a tela de Cadastro
            // TODO: Criar a ação no nav_graph.xml
            navController.navigate(R.id.action_loginFragment_to_cadastroFragment);

        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            // Navega para a tela "Esqueci minha senha"
            // TODO: Criar a ação no nav_graph.xml
            // navController.navigate(R.id.action_loginFragment_to_esqueceuSenhaFragment);
            Toast.makeText(getContext(), "Navegar para Esq. Senha (Impl. NavGraph)", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Observa os LiveData do AuthViewModel para reagir a mudanças de estado.
     */
    private void observarViewModel() {
        // Observa o SUCESSO do Login
        authViewModel.getUsuarioLogadoLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null) {
                // Usuário está logado!
                binding.btnLogin.setEnabled(true);
                Toast.makeText(getContext(), "Login bem-sucedido!", Toast.LENGTH_SHORT).show();

                // Navega para a tela principal (Feed)
                navController.navigate(R.id.action_loginFragment_to_feedFragment);
            }
        });

        // Observa ERROS de autenticação (Login ou Cadastro)
        authViewModel.getErroAutenticacaoLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                // Deu erro!
                binding.btnLogin.setEnabled(true); // Reabilita o botão
                Toast.makeText(getContext(), "Erro: " + erro, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Coleta, valida e inicia o processo de login.
     */
    private void tentarLogin() {
        // Limpa erros anteriores
        binding.tilLoginEmail.setError(null);
        binding.tilLoginPassword.setError(null);

        // Coleta os dados
        String email = binding.etLoginEmail.getText().toString().trim();
        String senha = binding.etLoginPassword.getText().toString();

        // Validação
        if (TextUtils.isEmpty(email)) {
            binding.tilLoginEmail.setError("E-mail é obrigatório");
            return;
        }
        if (TextUtils.isEmpty(senha)) {
            binding.tilLoginPassword.setError("Senha é obrigatória");
            return;
        }

        // Desabilita o botão e chama a ViewModel
        binding.btnLogin.setEnabled(false);
        Toast.makeText(getContext(), "Verificando...", Toast.LENGTH_SHORT).show();
        authViewModel.login(email, senha);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Limpa a referência ao binding
        binding = null;
    }
}