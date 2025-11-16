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
import com.upx.doando_mais.databinding.FragmentFormPublicaBinding; // Use o Binding de Campanha PÚBLICA
import com.upx.doando_mais.repository.HemocentroViewModel; // Reutiliza o ViewModel
import com.upx.doando_mais.ui.auth.AuthViewModel;

import java.util.ArrayList;
import java.util.List;

public class FormAbertaFragment extends Fragment {

    private FragmentFormPublicaBinding binding;
    private NavController navController;

    // Os mesmos 3 ViewModels
    private CriarCampanhaViewModel criarCampanhaViewModel;
    private AuthViewModel authViewModel;
    private HemocentroViewModel hemocentroViewModel;

    // Lista para guardar os locais
    private List<Hemocentro> listaDeHemocentros = new ArrayList<>();
    private Hemocentro hemocentroSelecionado;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFormPublicaBinding.inflate(inflater, container, false);
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
        // Observa a lista de HEMOCENTROS (mesma lógica)
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
                Toast.makeText(getContext(), "Campanha pública criada!", Toast.LENGTH_LONG).show();
                // Reseta o estado (agora da forma correTA)
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
        binding.spinnerPublicaLocal.setAdapter(adapter);
    }

    private void configurarCliques() {
        // Ação de salvar
        binding.btnSalvarPublica.setOnClickListener(v -> {
            tentarSalvarCampanhaPublica();
        });

        // Quando o usuário SELECIONA um local no spinner
        binding.spinnerPublicaLocal.setOnItemClickListener((parent, view, position, id) -> {
            // Pega o objeto Hemocentro selecionado
            hemocentroSelecionado = listaDeHemocentros.get(position);
            // Preenche o endereço automaticamente
            binding.etPublicaEndereco.setText(hemocentroSelecionado.getEndereco());
        });
    }

    private void tentarSalvarCampanhaPublica() {
        // --- 1. COLETA DE DADOS ---
        String titulo = binding.etPublicaTitulo.getText().toString().trim();
        String descricao = binding.etPublicaDescricao.getText().toString().trim();

        // --- 2. VALIDAÇÃO ---
        if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(descricao) || hemocentroSelecionado == null) {
            Toast.makeText(getContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
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
        novaCampanha.setTipoCampanha("Pública"); // Define o tipo
        novaCampanha.setStatus("Ativa");

        // Dados do Criador (Empresa/Organização)
        novaCampanha.setCriadorUid(usuarioLogado.getUid());
        novaCampanha.setNomeOrganizador(usuarioLogado.getNomeCompleto()); // Ex: "Carlos Mendes"

        // Dados do Formulário
        novaCampanha.setTitulo(titulo);
        novaCampanha.setDescricao(descricao);

        // Dados do Local (baseado na seleção)
        novaCampanha.setNomeHemocentro(hemocentroSelecionado.getNome());
        novaCampanha.setEnderecoHemocentro(hemocentroSelecionado.getEndereco());
        novaCampanha.setCidadeHemocentro(hemocentroSelecionado.getCidade());
        // (Conforme diretriz, telefone e horário são fixos da Colsan)
        novaCampanha.setContatoWhatsApp(hemocentroSelecionado.getTelefone()); // Reutilizamos o campo

        // Dados não aplicáveis a campanhas públicas
        novaCampanha.setNomePaciente(null);
        novaCampanha.setMetaDoadores(0);
        novaCampanha.setTipoSanguineoNecessario("Todos"); // Campanha pública é para todos

        // A dataCriacao é preenchida pelo @ServerTimestamp no Model

        // --- 5. ENVIAR PARA O VIEWMODEL ---
        mostrarCarregando(true);
        criarCampanhaViewModel.salvarCampanha(novaCampanha);
    }

    private void mostrarCarregando(boolean carregando) {
        binding.progressBarPublica.setVisibility(carregando ? View.VISIBLE : View.GONE);
        binding.btnSalvarPublica.setEnabled(!carregando);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}