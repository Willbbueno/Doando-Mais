package com.upx.doando_mais.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns; // Import para validar email
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast; // Import do Toast
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.upx.doando_mais.R;
import com.upx.doando_mais.data.model.Usuario;
import com.upx.doando_mais.databinding.FragmentCadastroBinding;

public class CadastroFragment extends Fragment {

    private FragmentCadastroBinding binding;
    private AuthViewModel authViewModel;
    private NavController navController;

    public CadastroFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCadastroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        configurarSpinners();
        configurarListenersDeClique();
        observarViewModel();
    }

    /**
     * Popula os Spinners (agora AutoCompleteTextViews) com os dados.
     * Este método já estava correto, pois AutoCompleteTextView também usa setAdapter.
     */
    private void configurarSpinners() {
        // Configura Spinner de Sexo
        ArrayAdapter<CharSequence> sexoAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.array_sexo, android.R.layout.simple_spinner_item
        );
        sexoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSexo.setAdapter(sexoAdapter);

        // Configura Spinner de Tipo Sanguíneo
        ArrayAdapter<CharSequence> tipoSanguineoAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.array_tipo_sanguineo, android.R.layout.simple_spinner_item
        );
        tipoSanguineoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTipoSanguineo.setAdapter(tipoSanguineoAdapter);

        // Configura Spinner de Perfil
        ArrayAdapter<CharSequence> perfilAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.array_perfil, android.R.layout.simple_spinner_item
        );
        perfilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPerfil.setAdapter(perfilAdapter);

        // Listener para mostrar a descrição do perfil (RF01)
        binding.spinnerPerfil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1: // Doador
                        binding.tvPerfilDescricao.setText("Perfil para quem deseja doar sangue.");
                        break;
                    case 2: // Organização
                        binding.tvPerfilDescricao.setText("Perfil para empresas e ONGs que desejam criar campanhas.");
                        break;
                    case 3: // Mobilizador
                        binding.tvPerfilDescricao.setText("Perfil para quem precisa criar uma campanha para um paciente.");
                        break;
                    default:
                        binding.tvPerfilDescricao.setText("Selecione um perfil para ver a descrição.");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * Configura os cliques dos botões e links de texto
     */
    private void configurarListenersDeClique() {
        binding.btnCadastrar.setOnClickListener(v -> {
            tentarCadastro();
        });

        binding.tvLoginLink.setOnClickListener(v -> {
            navController.popBackStack(); // Volta para a tela de Login
        });
    }

    /**
     * Observa os LiveData do AuthViewModel para reagir a mudanças de estado.
     */
    private void observarViewModel() {
        // Observa o SUCESSO FINAL (dados salvos no Firestore)
        authViewModel.getSalvamentoUsuarioSucessoLiveData().observe(getViewLifecycleOwner(), sucesso -> {
            // Verificamos se sucesso não é nulo antes de agir
            if (sucesso != null && sucesso) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), "Cadastro concluído com sucesso!", Toast.LENGTH_LONG).show();

                // Limpa o status para não disparar de novo
                authViewModel.limparStatusSalvamento();

                // Navega para a tela principal
                navController.navigate(R.id.action_cadastroFragment_to_feedFragment);
            }
        });

        // Observa ERROS (do Auth ou do Firestore, agora unificado)
        authViewModel.getErroLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), "Erro no cadastro: " + erro, Toast.LENGTH_LONG).show();
                // Limpa o erro para não ser exibido novamente
                authViewModel.limparErro();
            }
        });
    }

    /**
     * Coleta, valida e inicia o processo de cadastro.
     */
    private void tentarCadastro() {
        // 1. Limpa erros de validação anteriores
        limparErrosValidacao();

        // 2. Coleta de Dados
        String nome = binding.etCadastroNome.getText().toString().trim();
        String email = binding.etCadastroEmail.getText().toString().trim();
        String senha = binding.etCadastroPassword.getText().toString();
        String confSenha = binding.etCadastroConfirmPassword.getText().toString();
        String cpf = binding.etCadastroCpf.getText().toString().trim();
        String dataNasc = binding.etCadastroDataNasc.getText().toString().trim();
        String cidade = binding.etCadastroCidade.getText().toString().trim();
        String estado = binding.etCadastroEstado.getText().toString().trim();

        // ⬇️ --- CORREÇÃO 1: MUDANÇA DE .getSelectedItem() PARA .getText() --- ⬇️
        String sexo = binding.spinnerSexo.getText().toString();
        String tipoSanguineo = binding.spinnerTipoSanguineo.getText().toString();
        String perfil = binding.spinnerPerfil.getText().toString();

        // 3. Validação
        if (!validarFormulario(nome, email, senha, confSenha, cpf, dataNasc, cidade, estado, sexo, tipoSanguineo, perfil)) {
            return; // Para se o formulário for inválido
        }

        // 4. Mostrar Carregando
        mostrarCarregando(true);

        // 5. Criar Objeto Usuario
        Usuario novoUsuario = new Usuario(
                null, nome, email, cpf, sexo, dataNasc, cidade, estado,
                tipoSanguineo, perfil, 0, null, null
        );

        // 6. Chamar o ViewModel com a nova assinatura (Objeto + Senha)
        authViewModel.cadastrar(novoUsuario, senha);
    }

    /**
     * Controla a visibilidade do ProgressBar e habilita/desabilita o botão.
     */
    private void mostrarCarregando(boolean carregando) {
        binding.progressBarCadastro.setVisibility(carregando ? View.VISIBLE : View.GONE);
        binding.btnCadastrar.setEnabled(!carregando);

        if(carregando) {
            Toast.makeText(getContext(), "Processando...", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Valida todos os campos do formulário antes de enviar.
     */
    // ⬇️ --- CORREÇÃO 2: ATUALIZADA A VALIDAÇÃO DOS SPINNERS --- ⬇️
    private boolean validarFormulario(String nome, String email, String senha, String confSenha,
                                      String cpf, String dataNasc, String cidade, String estado,
                                      String sexo, String tipoSanguineo, String perfil) {
        boolean formValido = true;
        limparErrosValidacao(); // Limpa erros antes de validar de novo

        if (TextUtils.isEmpty(nome)) {
            binding.tilCadastroNome.setError("Nome é obrigatório");
            formValido = false;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilCadastroEmail.setError("Insira um email válido");
            formValido = false;
        }
        if (TextUtils.isEmpty(senha) || senha.length() < 6) {
            binding.tilCadastroPassword.setError("Senha deve ter no mínimo 6 caracteres");
            formValido = false;
        }
        if (!senha.equals(confSenha)) {
            binding.tilCadastroConfirmPassword.setError("As senhas não conferem");
            formValido = false;
        }
        if (TextUtils.isEmpty(cpf)) {
            binding.tilCadastroCpf.setError("CPF é obrigatório");
            formValido = false;
        }
        if (TextUtils.isEmpty(dataNasc)) {
            binding.tilCadastroDataNasc.setError("Data é obrigatória");
            formValido = false;
        }
        if (TextUtils.isEmpty(cidade)) {
            binding.tilCadastroCidade.setError("Cidade é obrigatória");
            formValido = false;
        }
        if (TextUtils.isEmpty(estado) || estado.length() != 2) {
            binding.tilCadastroEstado.setError("Insira a sigla do estado (Ex: SP)");
            formValido = false;
        }

        // Validação dos Spinners (agora AutoCompleteTextView)
        if (sexo.trim().isEmpty()) {
            binding.tilSexo.setError("Selecione um sexo");
            formValido = false;
        }
        if (tipoSanguineo.trim().isEmpty()) {
            binding.tilTipoSanguineo.setError("Selecione um tipo sanguíneo");
            formValido = false;
        }
        if (perfil.trim().isEmpty()) {
            binding.tilPerfil.setError("Selecione um perfil");
            formValido = false;
        }

        return formValido;
    }

    /**
     * Limpa os erros de validação dos campos.
     */
    // ⬇️ --- CORREÇÃO 3: ADICIONADA A LIMPEZA DOS SPINNERS --- ⬇️
    private void limparErrosValidacao() {
        binding.tilCadastroNome.setError(null);
        binding.tilCadastroEmail.setError(null);
        binding.tilCadastroPassword.setError(null);
        binding.tilCadastroConfirmPassword.setError(null);
        binding.tilCadastroCpf.setError(null);
        binding.tilCadastroDataNasc.setError(null);
        binding.tilCadastroCidade.setError(null);
        binding.tilCadastroEstado.setError(null);
        binding.tilSexo.setError(null);
        binding.tilTipoSanguineo.setError(null);
        binding.tilPerfil.setError(null);
    }

    /**
     * Limpa o ViewBinding para evitar vazamento de memória (memory leaks).
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}