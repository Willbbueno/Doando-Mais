package com.upx.doando_mais.ui.campanha_editar;

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
import com.upx.doando_mais.data.model.Campanha;
import com.upx.doando_mais.databinding.FragmentEditarCampanhaBinding; // Use o Binding de EDIÇÃO

public class EditarCampanhaFragment extends Fragment {

    private FragmentEditarCampanhaBinding binding;
    private EditarCampanhaViewModel viewModel;
    private NavController navController;

    private String campanhaId;
    private Campanha campanhaAtual; // Para guardar a campanha que estamos editando

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditarCampanhaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Pega o NavController e o ViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(this).get(EditarCampanhaViewModel.class);

        // 2. Pega o ID da campanha que foi enviado pelo AcompanhamentoFragment
        if (getArguments() != null) {
            campanhaId = getArguments().getString("campanhaId");
        }

        // 3. Configura os Observadores
        configurarObservadores();

        // 4. Configura o clique do botão "Salvar"
        binding.btnAtualizarCampanha.setOnClickListener(v -> {
            tentarAtualizarCampanha();
        });

        // 5. Manda o ViewModel buscar os dados da campanha
        if (campanhaId != null) {
            mostrarCarregando(true);
            viewModel.carregarCampanha(campanhaId);
        } else {
            Toast.makeText(getContext(), "Erro: ID da campanha não encontrado.", Toast.LENGTH_LONG).show();
            navController.popBackStack(); // Volta se não tiver ID
        }
    }

    private void configurarObservadores() {
        // Observa a CAMPANHA (para preencher o formulário)
        viewModel.getCampanhaParaEditarLiveData().observe(getViewLifecycleOwner(), campanha -> {
            if (campanha != null) {
                this.campanhaAtual = campanha; // Guarda o objeto
                preencherFormulario(campanha);
                mostrarCarregando(false);
            }
        });

        // Observa o SUCESSO da ATUALIZAÇÃO
        viewModel.getAtualizacaoSucessoLiveData().observe(getViewLifecycleOwner(), sucesso -> {
            if (sucesso) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), "Campanha atualizada com sucesso!", Toast.LENGTH_LONG).show();
                navController.popBackStack(); // Volta para a tela de Acompanhamento
            }
        });

        // Observa ERROS
        viewModel.getErroLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), "Erro: " + erro, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Preenche o formulário com os dados da campanha buscada
     */
    private void preencherFormulario(Campanha campanha) {
        binding.etTitulo.setText(campanha.getTitulo());
        binding.etDescricao.setText(campanha.getDescricao());
        binding.etTipoSanguineo.setText(campanha.getTipoSanguineoNecessario());
        binding.etMetaDoadores.setText(String.valueOf(campanha.getMetaDoadores()));
        binding.etNomePaciente.setText(campanha.getNomePaciente());
        binding.etNomeHemocentro.setText(campanha.getNomeHemocentro());
        binding.etEnderecoHemocentro.setText(campanha.getEnderecoHemocentro());
        binding.etWhatsapp.setText(campanha.getContatoWhatsApp());
    }

    /**
     * Coleta os dados, valida e manda o ViewModel salvar
     */
    private void tentarAtualizarCampanha() {
        if (campanhaAtual == null) {
            Toast.makeText(getContext(), "Erro: Campanha original não carregada.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Coleta os dados do formulário
        String titulo = binding.etTitulo.getText().toString().trim();
        String descricao = binding.etDescricao.getText().toString().trim();
        String tipoSanguineo = binding.etTipoSanguineo.getText().toString().trim();
        String metaStr = binding.etMetaDoadores.getText().toString().trim();
        String nomePaciente = binding.etNomePaciente.getText().toString().trim();
        String nomeHemocentro = binding.etNomeHemocentro.getText().toString().trim();
        String enderecoHemocentro = binding.etEnderecoHemocentro.getText().toString().trim();
        String whatsapp = binding.etWhatsapp.getText().toString().trim();

        // 2. Validação (simples, igual a de Criar)
        if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(descricao) || TextUtils.isEmpty(metaStr) || TextUtils.isEmpty(nomeHemocentro)) {
            Toast.makeText(getContext(), "Preencha os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }
        int metaDoadores = Integer.parseInt(metaStr); // (Adicionar try-catch se quiser)

        // 3. ATUALIZA o objeto 'campanhaAtual' com os novos dados
        // (Isso preserva o ID, dataCriacao, criadorUid, etc.)
        campanhaAtual.setTitulo(titulo);
        campanhaAtual.setDescricao(descricao);
        campanhaAtual.setTipoSanguineoNecessario(tipoSanguineo);
        campanhaAtual.setMetaDoadores(metaDoadores);
        campanhaAtual.setNomeHemocentro(nomeHemocentro);
        campanhaAtual.setEnderecoHemocentro(enderecoHemocentro);
        campanhaAtual.setNomePaciente(nomePaciente.isEmpty() ? null : nomePaciente);
        campanhaAtual.setContatoWhatsApp(whatsapp.isEmpty() ? null : whatsapp);

        // 4. Manda o ViewModel salvar o objeto ATUALIZADO
        mostrarCarregando(true);
        viewModel.salvarEdicao(campanhaAtual);
    }

    private void mostrarCarregando(boolean carregando) {
        binding.progressBarEditar.setVisibility(carregando ? View.VISIBLE : View.GONE);
        binding.btnAtualizarCampanha.setEnabled(!carregando);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}