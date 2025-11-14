package com.upx.doando_mais.ui.campanha_criar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.upx.doando_mais.data.model.Campanha;
import com.upx.doando_mais.data.model.Hemocentro;
import com.upx.doando_mais.data.model.Usuario;
import com.upx.doando_mais.databinding.FragmentFormPacienteBinding;
import com.upx.doando_mais.repository.HemocentroViewModel; // Importe o novo ViewModel
import com.upx.doando_mais.ui.auth.AuthViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Calendar; // Import para data

public class FormPacienteFragment extends Fragment {

    private FragmentFormPacienteBinding binding;
    private NavController navController;

    // Os 3 ViewModels
    private CriarCampanhaViewModel criarCampanhaViewModel;
    private AuthViewModel authViewModel;
    private HemocentroViewModel hemocentroViewModel;

    // Lista para guardar os locais
    private List<Hemocentro> listaDeHemocentros = new ArrayList<>();
    private Hemocentro hemocentroSelecionado;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFormPacienteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializa NavController e ViewModels
        navController = Navigation.findNavController(view);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        criarCampanhaViewModel = new ViewModelProvider(this).get(CriarCampanhaViewModel.class);
        hemocentroViewModel = new ViewModelProvider(this).get(HemocentroViewModel.class);

        // 2. Configura os Observadores
        configurarObservadores();

        // 3. Configura os Cliques
        configurarCliques();
    }

    private void configurarObservadores() {
        // Observa a lista de HEMOCENTROS
        hemocentroViewModel.getHemocentrosLiveData().observe(getViewLifecycleOwner(), hemocentros -> {
            if (hemocentros != null && !hemocentros.isEmpty()) {
                this.listaDeHemocentros = hemocentros;
                popularSpinnerLocais(hemocentros);
            }
        });

        // Observa o SUCESSO da criação
        criarCampanhaViewModel.getCriacaoSucessoLiveData().observe(getViewLifecycleOwner(), sucesso -> {
            if (sucesso) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), "Campanha para paciente criada!", Toast.LENGTH_LONG).show();
                // Limpa o LiveData para evitar re-trigger (CORRIGIDO)
                criarCampanhaViewModel.resetarEstadoDeSucesso();
                // Volta para a tela de Acompanhamento (ou para o Feed)
                navController.popBackStack(); // Volta para a tela de seleção
            }
        });

        // Observa ERROS
        criarCampanhaViewModel.getErroLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), "Erro: " + erro, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Preenche o AutoCompleteTextView (Spinner) com os nomes dos locais
     */
    private void popularSpinnerLocais(List<Hemocentro> hemocentros) {
        List<String> nomesHemocentros = new ArrayList<>();
        for (Hemocentro h : hemocentros) {
            nomesHemocentros.add(h.getNome());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                nomesHemocentros
        );
        binding.spinnerPacienteLocal.setAdapter(adapter);
    }

    private void configurarCliques() {
        // Ação de salvar
        binding.btnSalvarPaciente.setOnClickListener(v -> {
            tentarSalvarCampanhaPaciente();
        });

        // Quando o usuário SELECIONA um local no spinner
        binding.spinnerPacienteLocal.setOnItemClickListener((parent, view, position, id) -> {
            // Pega o objeto Hemocentro selecionado
            hemocentroSelecionado = listaDeHemocentros.get(position);
            // Preenche o endereço automaticamente
            binding.etPacienteEndereco.setText(hemocentroSelecionado.getEndereco());
        });
    }

    private void tentarSalvarCampanhaPaciente() {
        // --- 1. COLETA DE DADOS ---
        String nomePaciente = binding.etPacienteNome.getText().toString().trim();
        String descricao = binding.etPacienteDescricao.getText().toString().trim();
        String tipoSanguineo = binding.etPacienteTipoSanguineo.getText().toString().trim();
        String metaStr = binding.etPacienteMeta.getText().toString().trim();
        String telefoneContato = binding.etPacienteTelefone.getText().toString().trim();

        // --- 2. VALIDAÇÃO ---
        if (TextUtils.isEmpty(nomePaciente) || TextUtils.isEmpty(descricao) || TextUtils.isEmpty(metaStr) || hemocentroSelecionado == null) {
            Toast.makeText(getContext(), "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 3. PEGAR DADOS DO CRIADOR (USUÁRIO LOGADO) ---
        Usuario usuarioLogado = authViewModel.getDadosUsuarioLiveData().getValue();
        if (usuarioLogado == null) {
            Toast.makeText(getContext(), "Erro: Não foi possível identificar o usuário.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 4. CRIAR O OBJETO CAMPANHA ---
        Campanha novaCampanha = new Campanha();
        novaCampanha.setTipoCampanha("Paciente"); // Define o tipo
        novaCampanha.setStatus("Ativa");

        // Dados do Criador
        novaCampanha.setCriadorUid(usuarioLogado.getUid());
        novaCampanha.setNomeOrganizador(usuarioLogado.getNomeCompleto());
        novaCampanha.setContatoWhatsApp(telefoneContato.isEmpty() ? usuarioLogado.getTelefone() : telefoneContato);

        // Dados do Formulário
        novaCampanha.setTitulo("Campanha para: " + nomePaciente); // Título automático
        novaCampanha.setNomePaciente(nomePaciente);
        novaCampanha.setDescricao(descricao);
        novaCampanha.setTipoSanguineoNecessario(tipoSanguineo);
        novaCampanha.setMetaDoadores(Integer.parseInt(metaStr));

        // Dados do Local (baseado na seleção)
        novaCampanha.setNomeHemocentro(hemocentroSelecionado.getNome());
        novaCampanha.setEnderecoHemocentro(hemocentroSelecionado.getEndereco());
        // (Podemos adicionar o telefone do hemocentro ao modelo Hemocentro.java depois)

        // Data de Expiração (1 Mês, conforme diretriz)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        novaCampanha.setDataTermino(cal.getTime());
        // A dataCriacao é preenchida pelo @ServerTimestamp no Model

        // --- 5. ENVIAR PARA O VIEWMODEL ---
        mostrarCarregando(true);
        criarCampanhaViewModel.salvarCampanha(novaCampanha);
    }

    private void mostrarCarregando(boolean carregando) {
        binding.progressBarPaciente.setVisibility(carregando ? View.VISIBLE : View.GONE);
        binding.btnSalvarPaciente.setEnabled(!carregando);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}