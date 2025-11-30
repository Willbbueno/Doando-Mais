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
import com.upx.doando_mais.databinding.FragmentLoginBinding; // Verifique se o nome do binding está correto

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    private NavController navController;

    public LoginFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        configurarCliques();
        observarViewModel();
    }

    private void configurarCliques() {
        binding.btnLogin.setOnClickListener(v -> tentarLogin());

        binding.tvCadastroLink.setOnClickListener(v ->
                navController.navigate(R.id.action_loginFragment_to_cadastroFragment)
        );

        binding.tvEsqueceuSenha.setOnClickListener(v ->
                navController.navigate(R.id.action_loginFragment_to_esqueceuSenhaFragment)
        );
    }

    /**
     * Observa o ViewModel para reagir a sucesso ou falha no login.
     */
    private void observarViewModel() {
        // Observa SUCESSO:
        // O login é considerado "sucesso" quando os dados do usuário (do Firestore)
        // são carregados após a autenticação.
        authViewModel.getDadosUsuarioLiveData().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                mostrarCarregando(false);
                // Navega para a tela principal (Feed)
                navController.navigate(R.id.action_loginFragment_to_feedFragment);
            }
        });

        // Observa ERRO:
        // --- ⬇️ ESTA É A CORREÇÃO DO SEU ERRO ⬇️ ---
        // Trocamos 'getErroAutenticacaoLiveData' pelo novo 'getErroLiveData' unificado
        authViewModel.getErroLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), "Erro: " + erro, Toast.LENGTH_LONG).show();

                // Limpa o erro para não ser exibido novamente (ao girar a tela, etc.)
                authViewModel.limparErro();
            }
        });
    }

    /**
     * Coleta os dados do formulário e tenta fazer o login via ViewModel.
     */
    private void tentarLogin() {
        String email = binding.etLoginEmail.getText().toString().trim();
        String senha = binding.etLoginPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
            Toast.makeText(getContext(), "Preencha email e senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        mostrarCarregando(true);
        authViewModel.login(email, senha);
    }

    /**
     * Controla a UI para mostrar o estado de "carregando".
     */
    private void mostrarCarregando(boolean carregando) {
        // TODO: Adicione um ProgressBar ao seu 'fragment_login.xml' com id 'progressBarLogin'
        // if (binding.progressBarLogin != null) {
        //     binding.progressBarLogin.setVisibility(carregando ? View.VISIBLE : View.GONE);
        // }
        binding.btnLogin.setEnabled(!carregando);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Previne memory leaks
    }
}