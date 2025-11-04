package com.upx.doando_mais.ui.campanha_criar;

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
import com.google.firebase.auth.FirebaseUser;
import com.upx.doando_mais.data.model.Campanha;
import com.upx.doando_mais.data.model.Usuario; // Import o modelo Usuario
import com.upx.doando_mais.databinding.FragmentCriarCampanhaBinding;
import com.upx.doando_mais.ui.auth.AuthViewModel; // Importe o AuthViewModel

public class CriarCampanhaFragment extends Fragment {

    private FragmentCriarCampanhaBinding binding;
    private CriarCampanhaViewModel criarCampanhaViewModel;
    private AuthViewModel authViewModel; // Precisamos dele para pegar o nome do usuário

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCriarCampanhaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializa os ViewModels
        // Usamos 'requireActivity()' para compartilhar o estado de login
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        criarCampanhaViewModel = new ViewModelProvider(this).get(CriarCampanhaViewModel.class);

        // 2. Configura os Listeners
        configurarListenersDeClique();

        // 3. Configura os Observadores
        configurarObservadores();
    }

    private void configurarListenersDeClique() {
        binding.btnSalvarCampanha.setOnClickListener(v -> {
            tentarSalvarCampanha();
        });
    }

    private void configurarObservadores() {
        // Observa o SUCESSO da criação
        criarCampanhaViewModel.getCriacaoSucessoLiveData().observe(getViewLifecycleOwner(), sucesso -> {
            if (sucesso) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), "Campanha criada com sucesso!", Toast.LENGTH_LONG).show();
                limparFormulario();
                // Opcional: Navegar para o Feed ou para a aba "Acompanhamento"
                // NavController navController = Navigation.findNavController(v);
                // navController.navigate(R.id.action_criar_to_feed);
            }
        });

        // Observa o ERRO da criação
        criarCampanhaViewModel.getErroLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), "Erro: " + erro, Toast.LENGTH_LONG).show();
            }
        });

        // --- OBSERVAÇÃO BÔNUS ---
        // Observa os dados do usuário para mostrar uma mensagem de "Boas-vindas" (opcional)
        authViewModel.getDadosUsuarioLiveData().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                // Exemplo: Colocar uma saudação no topo (requer um TextView no XML)
                // binding.tvSaudacaoCriador.setText("Olá, " + usuario.getNome() + "! Crie sua campanha.");
            }
        });
    }

    private void tentarSalvarCampanha() {
        // --- 1. COLETA DE DADOS ---
        String titulo = binding.etTitulo.getText().toString().trim();
        String descricao = binding.etDescricao.getText().toString().trim();
        String tipoSanguineo = binding.etTipoSanguineo.getText().toString().trim();
        String metaStr = binding.etMetaDoadores.getText().toString().trim();
        String nomePaciente = binding.etNomePaciente.getText().toString().trim();
        String nomeHemocentro = binding.etNomeHemocentro.getText().toString().trim();
        String enderecoHemocentro = binding.etEnderecoHemocentro.getText().toString().trim();
        String whatsapp = binding.etWhatsapp.getText().toString().trim();

        // --- 2. VALIDAÇÃO ---
        if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(descricao) || TextUtils.isEmpty(tipoSanguineo) || TextUtils.isEmpty(metaStr) || TextUtils.isEmpty(nomeHemocentro)) {
            Toast.makeText(getContext(), "Preencha os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        int metaDoadores = 0;
        try {
            metaDoadores = Integer.parseInt(metaStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Meta de doadores inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 3. PEGAR DADOS DO CRIADOR (DO USUÁRIO LOGADO) ---

        // Primeiro, o UID (do Firebase Auth)
        FirebaseUser usuarioLogado = authViewModel.getUsuarioLogadoLiveData().getValue();
        if (usuarioLogado == null) {
            Toast.makeText(getContext(), "Erro: Você não está logado.", Toast.LENGTH_SHORT).show();
            return;
        }
        String uidCriador = usuarioLogado.getUid();

        // Pega o nome do Firestore, via AuthViewModel
        Usuario dadosUsuario = authViewModel.getDadosUsuarioLiveData().getValue();
        String nomeCriador;

        if (dadosUsuario != null && dadosUsuario.getNomeCompleto() != null) {
            nomeCriador = dadosUsuario.getNomeCompleto();
        } else {
            // Fallback (plano B) caso os dados ainda não tenham carregado
            nomeCriador = usuarioLogado.getEmail();
        }

        // --- 4. CRIAR O OBJETO CAMPANHA ---
        Campanha novaCampanha = new Campanha();
        novaCampanha.setTitulo(titulo);
        novaCampanha.setDescricao(descricao);
        novaCampanha.setTipoSanguineoNecessario(tipoSanguineo);
        novaCampanha.setMetaDoadores(metaDoadores);
        novaCampanha.setNomeHemocentro(nomeHemocentro);
        novaCampanha.setEnderecoHemocentro(enderecoHemocentro);

        // Campos que podem ser nulos
        novaCampanha.setNomePaciente(nomePaciente.isEmpty() ? null : nomePaciente);
        novaCampanha.setContatoWhatsApp(whatsapp.isEmpty() ? null : whatsapp);

        // Dados do Criador
        novaCampanha.setCriadorUid(uidCriador);
        novaCampanha.setNomeOrganizador(nomeCriador); // <--- Nome correto aqui!

        // Dados automáticos (o Firestore preenche a dataCriacao)
        novaCampanha.setStatus("Ativa");
        novaCampanha.setContadorIntencoes(0);
        novaCampanha.setContadorCompartilhamentos(0);

        // --- 5. ENVIAR PARA O VIEWMODEL ---
        mostrarCarregando(true);
        criarCampanhaViewModel.salvarCampanha(novaCampanha);
    }

    private void limparFormulario() {
        binding.etTitulo.setText("");
        binding.etDescricao.setText("");
        binding.etTipoSanguineo.setText("");
        binding.etMetaDoadores.setText("");
        binding.etNomePaciente.setText("");
        binding.etNomeHemocentro.setText("");
        binding.etEnderecoHemocentro.setText("");
        binding.etWhatsapp.setText("");
        binding.etTitulo.requestFocus();
    }

    private void mostrarCarregando(boolean carregando) {
        if (carregando) {
            binding.progressBarCriar.setVisibility(View.VISIBLE);
            binding.btnSalvarCampanha.setEnabled(false);
        } else {
            binding.progressBarCriar.setVisibility(View.GONE);
            binding.btnSalvarCampanha.setEnabled(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpa a referência do binding
    }
}