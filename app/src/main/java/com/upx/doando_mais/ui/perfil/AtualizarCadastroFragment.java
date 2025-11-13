package com.upx.doando_mais.ui.perfil;

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
import com.upx.doando_mais.data.model.Usuario;
import com.upx.doando_mais.databinding.FragmentAtualizarCadastroBinding; // Use o Binding de ATUALIZAR
import com.upx.doando_mais.ui.auth.AuthViewModel;

public class AtualizarCadastroFragment extends Fragment {

    private FragmentAtualizarCadastroBinding binding;
    private AuthViewModel authViewModel; // O ViewModel compartilhado que já tem os dados
    private NavController navController;
    private Usuario usuarioAtual; // Para guardar o objeto do usuário

    public AtualizarCadastroFragment() {
        // Construtor vazio necessário
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAtualizarCadastroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializa o ViewModel (compartilhado da Activity) e o NavController
        navController = Navigation.findNavController(view);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // 2. Configura os Observadores
        configurarObservadores();

        // 3. Configura o clique do botão "Salvar"
        binding.btnAtualizarDados.setOnClickListener(v -> {
            tentarAtualizarDados();
        });
    }

    private void configurarObservadores() {
        // Observa os dados do usuário para PREENCHER o formulário
        authViewModel.getDadosUsuarioLiveData().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                this.usuarioAtual = usuario; // Salva o objeto
                preencherFormulario(usuario);
            }
        });

        // Observa o SUCESSO do salvamento
        authViewModel.getSalvamentoUsuarioSucessoLiveData().observe(getViewLifecycleOwner(), sucesso -> {
            if (sucesso) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), "Dados atualizados com sucesso!", Toast.LENGTH_LONG).show();
                navController.popBackStack(); // Volta para a tela de Perfil
            }
        });

        // Observa ERROS
        authViewModel.getErroAutenticacaoLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), "Erro ao salvar: " + erro, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Preenche o formulário com os dados atuais do usuário
     */
    private void preencherFormulario(Usuario usuario) {
        binding.etAtualizarNome.setText(usuario.getNomeCompleto());
        binding.etAtualizarEmail.setText(usuario.getEmail()); // Email não é editável
        binding.etAtualizarCpf.setText(usuario.getCpf());
        binding.etAtualizarDataNasc.setText(usuario.getDataNascimento());
        binding.etAtualizarCidade.setText(usuario.getCidade());
        binding.etAtualizarEstado.setText(usuario.getEstado());
        // (Aqui você preencheria os Spinners se os tivesse adicionado)
    }

    /**
     * Coleta os dados editados e envia para o ViewModel
     */
    private void tentarAtualizarDados() {
        if (usuarioAtual == null) {
            Toast.makeText(getContext(), "Erro: Dados do usuário não carregados.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Coleta os dados do formulário
        String nome = binding.etAtualizarNome.getText().toString().trim();
        String cpf = binding.etAtualizarCpf.getText().toString().trim();
        String dataNasc = binding.etAtualizarDataNasc.getText().toString().trim();
        String cidade = binding.etAtualizarCidade.getText().toString().trim();
        String estado = binding.etAtualizarEstado.getText().toString().trim();

        // 2. Validação simples
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(cpf) || TextUtils.isEmpty(cidade)) {
            Toast.makeText(getContext(), "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. ATUALIZA o objeto 'usuarioAtual' com os novos dados
        // (Isso preserva o UID, Email, Perfil, TipoSanguineo, etc.)
        usuarioAtual.setNomeCompleto(nome);
        usuarioAtual.setCpf(cpf);
        usuarioAtual.setDataNascimento(dataNasc);
        usuarioAtual.setCidade(cidade);
        usuarioAtual.setEstado(estado);
        // (Aqui você pegaria os valores dos Spinners se os tivesse)

        // 4. Manda o ViewModel salvar o objeto ATUALIZADO
        mostrarCarregando(true);
        authViewModel.atualizarDadosUsuario(usuarioAtual);
    }

    private void mostrarCarregando(boolean carregando) {
        binding.progressBarAtualizar.setVisibility(carregando ? View.VISIBLE : View.GONE);
        binding.btnAtualizarDados.setEnabled(!carregando);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
