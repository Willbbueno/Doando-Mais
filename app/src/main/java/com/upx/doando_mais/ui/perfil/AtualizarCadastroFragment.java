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
import com.upx.doando_mais.databinding.FragmentAtualizarCadastroBinding;
import com.upx.doando_mais.ui.auth.AuthViewModel;

public class AtualizarCadastroFragment extends Fragment {

    private FragmentAtualizarCadastroBinding binding;
    private AuthViewModel authViewModel;
    private NavController navController;
    private Usuario usuarioAtual; // Objeto clonado para edição

    public AtualizarCadastroFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAtualizarCadastroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        configurarObservadores();

        binding.btnAtualizarDados.setOnClickListener(v -> {
            tentarAtualizarDados();
        });
    }

    private void configurarObservadores() {
        // Observa os dados do usuário para PREENCHER o formulário
        authViewModel.getDadosUsuarioLiveData().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null && this.usuarioAtual == null) {
                // ⬇️ CORREÇÃO ERRO 1 ⬇️
                // Agora o 'new Usuario(usuario)' funciona
                this.usuarioAtual = new Usuario(usuario);
                preencherFormulario(this.usuarioAtual);
            }
        });

        // Observa o SUCESSO do salvamento
        authViewModel.getSalvamentoUsuarioSucessoLiveData().observe(getViewLifecycleOwner(), sucesso -> {
            if (sucesso != null && sucesso) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), "Dados atualizados com sucesso!", Toast.LENGTH_LONG).show();

                // ⬇️ CORREÇÃO ERRO 2 ⬇️
                // Agora o método 'limparStatusSalvamento()' existe
                authViewModel.limparStatusSalvamento();

                navController.popBackStack(); // Volta para a tela de Perfil
            }
        });

        // Observa ERROS
        authViewModel.getErroLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), "Erro ao salvar: " + erro, Toast.LENGTH_LONG).show();
                authViewModel.limparErro();
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
        // ⬇️ CORREÇÃO ERRO 3 (REMOVIDO) ⬇️
        // binding.etAtualizarTelefone.setText(usuario.getTelefone());
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
        // ⬇️ CORREÇÃO ERRO 4 (REMOVIDO) ⬇️
        // String telefone = binding.etAtualizarTelefone.getText().toString().trim();

        // 2. Validação simples
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(cpf) || TextUtils.isEmpty(cidade)) {
            Toast.makeText(getContext(), "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. ATUALIZA o objeto 'usuarioAtual' com os novos dados
        usuarioAtual.setNomeCompleto(nome);
        usuarioAtual.setCpf(cpf);
        usuarioAtual.setDataNascimento(dataNasc);
        usuarioAtual.setCidade(cidade);
        usuarioAtual.setEstado(estado);
        // ⬇️ CORREÇÃO ERRO 4 (REMOVIDO) ⬇️
        // usuarioAtual.setTelefone(telefone);

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