package com.upx.doando_mais.ui.campanha_detalhe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.upx.doando_mais.data.model.Campanha;
import com.upx.doando_mais.databinding.FragmentDetalheCampanhaBinding;

public class DetalheCampanhaFragment extends Fragment {

    private FragmentDetalheCampanhaBinding binding;
    private DetalheCampanhaViewModel detalheCampanhaViewModel;
    private String campanhaId;

    public DetalheCampanhaFragment() {
        // Construtor vazio obrigatório
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetalheCampanhaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Pega o ID que foi enviado pelo Adapter
        if (getArguments() != null) {
            campanhaId = getArguments().getString("campanhaId");
        }

        // 2. Inicializa o ViewModel
        detalheCampanhaViewModel = new ViewModelProvider(this).get(DetalheCampanhaViewModel.class);

        // 3. Configura os Observadores
        configurarObservadores();

        // 5. Manda o ViewModel buscar os dados (e mostra o "Carregando")
        if (campanhaId != null) {
            mostrarCarregando(true);
            detalheCampanhaViewModel.carregarDetalhesCampanha(campanhaId);
        } else {
            Toast.makeText(getContext(), "Erro: ID da campanha não encontrado.", Toast.LENGTH_LONG).show();
        }
    }

    private void configurarObservadores() {
        // Observa o SUCESSO da busca
        detalheCampanhaViewModel.getCampanhaDetalheLiveData().observe(getViewLifecycleOwner(), campanha -> {
            if (campanha != null) {
                mostrarCarregando(false);
                preencherDados(campanha);
            }
        });

        // Observa o ERRO da busca
        detalheCampanhaViewModel.getErroLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), erro, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Preenche todos os TextViews do layout com os dados da Campanha.
     */
    private void preencherDados(Campanha campanha) {
        binding.tvDetalheTitulo.setText(campanha.getTitulo());
        binding.tvDetalheOrganizador.setText("Criada por: " + campanha.getNomeOrganizador());
        binding.tvDetalheDescricao.setText(campanha.getDescricao());

        // Card de Informações
        binding.tvDetalheLocal.setText(campanha.getNomeHemocentro());
        binding.tvDetalheEndereco.setText(campanha.getEnderecoHemocentro());
        binding.tvDetalheTipoSanguineo.setText(campanha.getTipoSanguineoNecessario());

        // Paciente (só mostra se existir)
        if (campanha.getNomePaciente() != null && !campanha.getNomePaciente().isEmpty()) {
            binding.tvLabelPaciente.setVisibility(View.VISIBLE);
            binding.tvDetalhePaciente.setVisibility(View.VISIBLE);
            binding.tvDetalhePaciente.setText(campanha.getNomePaciente());
        } else {
            binding.tvLabelPaciente.setVisibility(View.GONE);
            binding.tvDetalhePaciente.setVisibility(View.GONE);
        }

        // Contato (só mostra o card se existir)
        if (campanha.getContatoWhatsApp() != null && !campanha.getContatoWhatsApp().isEmpty()) {
            binding.cardContato.setVisibility(View.VISIBLE);
            binding.tvDetalheWhatsapp.setText(campanha.getContatoWhatsApp());
        } else {
            binding.cardContato.setVisibility(View.GONE);
        }
    }

    private void mostrarCarregando(boolean carregando) {
        if (carregando) {
            binding.progressBarDetalhe.setVisibility(View.VISIBLE);
            binding.scrollView.setVisibility(View.GONE);
            binding.btnVouDoar.setVisibility(View.GONE);
        } else {
            binding.progressBarDetalhe.setVisibility(View.GONE);
            binding.scrollView.setVisibility(View.VISIBLE);
            binding.btnVouDoar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpa a referência do binding
    }
}