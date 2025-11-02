package com.upx.doando_mais.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseUser;
import com.upx.doando_mais.R; // Para acessar os recursos layouts, strings
import com.upx.doando_mais.data.model.Usuario;
import com.upx.doando_mais.databinding.FragmentCadastroBinding; // Gerado automaticamente pelo ViewBinding

public class CadastroFragment extends Fragment {

    // 1. DECLARAÇÃO DE VARIÁVEIS

    // ViewBinding substitui o findViewById
    private FragmentCadastroBinding binding;

    private AuthViewModel authViewModel;

    // NavController navega entre os fragments
    private NavController navController;

    // Armazena temporariamente os dados do formulário enquanto o Auth é criado
    private Usuario usuarioPendente;

    // Construtor público vazio é necessário para Fragments
    public CadastroFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 2. INFLAR O LAYOUT
        // Infla (cria) o layout XML e o associa a esta classe Java
        binding = FragmentCadastroBinding.inflate(inflater, container, false);
        // Retorna a "view raiz" do nosso layout (o ConstraintLayout)
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 3. INICIALIZAÇÃO
        // Pega o NavController para podermos navegar para outras telas
        navController = Navigation.findNavController(view);

        // Inicializa a ViewModel.
        // Usamos 'requireActivity()' para que o ViewModel seja compartilhado
        // entre LoginFragment e CadastroFragment (útil para o estado de login).
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // 4. CONFIGURAÇÃO DA UI
        configurarSpinners();
        configurarListenersDeClique();

        // 5. OBSERVAÇÃO DOS DADOS (O CORAÇÃO DO MVVM)
        // O Fragment "observa" o ViewModel e reage a mudanças.
        observarViewModel();
    }

    /**
     * Popula os Spinners (listas suspensas) com os dados do strings.xml
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

        // Adiciona um listener para mostrar a descrição do perfil (RF01)
        binding.spinnerPerfil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO: Adicionar descrições dos perfis no strings.xml e mostrar aqui
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
            // Quando o botão "Cadastrar" for clicado, chama o método de cadastro
            tentarCadastro();
        });

        binding.tvLoginLink.setOnClickListener(v -> {
            // Navega de volta para a tela de Login
            navController.popBackStack();
        });
    }

    /**
     * Observa os LiveData do AuthViewModel para reagir a mudanças de estado.
     */
    private void observarViewModel() {

        // Observa o SUCESSO FINAL (dados salvos no Firestore)
        authViewModel.getSalvamentoUsuarioSucessoLiveData().observe(getViewLifecycleOwner(), sucesso -> {
            if (sucesso) {
                // Cadastro COMPLETO.
                binding.btnCadastrar.setEnabled(true); // Reabilita o botão
                Toast.makeText(getContext(), "Cadastro concluído com sucesso!", Toast.LENGTH_LONG).show();

                // 4. Navega para a tela principal
                navController.navigate(R.id.action_cadastroFragment_to_feedFragment);
            }
        });

        // Observa ERROS (do Auth ou do Firestore)
        authViewModel.getErroAutenticacaoLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                // Deu erro!
                binding.btnCadastrar.setEnabled(true); // Reabilita o botão
                Toast.makeText(getContext(), "Erro no cadastro: " + erro, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Coleta, valida e inicia o processo de cadastro.
     */
    private void tentarCadastro() {
        // ... (limpeza de erros e coleta de dados - igual ao código anterior)
        String nome = binding.etCadastroNome.getText().toString().trim();
        String email = binding.etCadastroEmail.getText().toString().trim();
        String senha = binding.etCadastroPassword.getText().toString();
        String confSenha = binding.etCadastroConfirmPassword.getText().toString();
        String cpf = binding.etCadastroCpf.getText().toString().trim();
        String dataNasc = binding.etCadastroDataNasc.getText().toString().trim();
        String cidade = binding.etCadastroCidade.getText().toString().trim();
        String estado = binding.etCadastroEstado.getText().toString().trim();
        String sexo = binding.spinnerSexo.getSelectedItem().toString();
        String tipoSanguineo = binding.spinnerTipoSanguineo.getSelectedItem().toString();
        String perfil = binding.spinnerPerfil.getSelectedItem().toString();

        // ... (validação do formulário - igual ao código anterior)
        boolean formValido = true;
        // ... (seu código de validação) ...
        if (TextUtils.isEmpty(nome)) {
            binding.tilCadastroNome.setError("Nome é obrigatório");
            formValido = false;
        }
        // ... (etc.) ...
        if (!formValido) {
            return;
        }

        binding.btnCadastrar.setEnabled(false);
        Toast.makeText(getContext(), "Processando...", Toast.LENGTH_SHORT).show();

        // --- ESTA É A LINHA CORRIGIDA ---
        // Inicia o processo de cadastro no ViewModel passando TODOS os dados
        authViewModel.cadastrar(
                email, senha, nome, cpf, sexo, dataNasc, cidade, estado,
                tipoSanguineo, perfil
        );
    }
}