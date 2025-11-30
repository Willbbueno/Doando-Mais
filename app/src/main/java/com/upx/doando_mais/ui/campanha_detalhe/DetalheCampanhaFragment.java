package com.upx.doando_mais.ui.campanha_detalhe;

import android.content.Intent;
import android.net.Uri;
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
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetalheCampanhaFragment extends Fragment {

    private FragmentDetalheCampanhaBinding binding;
    private DetalheCampanhaViewModel detalheCampanhaViewModel;
    private String campanhaId;
    private Campanha campanhaAtual;

    public DetalheCampanhaFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetalheCampanhaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            campanhaId = getArguments().getString("campanhaId");
        }

        detalheCampanhaViewModel = new ViewModelProvider(this).get(DetalheCampanhaViewModel.class);

        configurarObservadores();
        configurarCliques(); // A lógica de clique já simula a ida ao site

        if (campanhaId != null) {
            mostrarCarregando(true);
            detalheCampanhaViewModel.carregarDetalhesCampanha(campanhaId);
        } else {
            Toast.makeText(getContext(), "Erro: ID da campanha não encontrado.", Toast.LENGTH_LONG).show();
        }
    }

    private void configurarObservadores() {
        detalheCampanhaViewModel.getCampanhaDetalheLiveData().observe(getViewLifecycleOwner(), campanha -> {
            if (campanha != null) {
                this.campanhaAtual = campanha;
                preencherDados(campanha);
                mostrarCarregando(false);
            }
        });

        detalheCampanhaViewModel.getErroLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), erro, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void preencherDados(Campanha campanha) {
        binding.tvDetalheTitulo.setText(campanha.getTitulo());
        binding.tvDetalheOrganizador.setText("Criada por: " + campanha.getNomeOrganizador());
        binding.tvDetalheDescricao.setText(campanha.getDescricao());

        // <-- MUDANÇA 1: Garante que o botão apareça -->
        binding.btnAcaoDetalhe.setVisibility(View.VISIBLE);
        // Atualiza o texto para "Vou doar" (como no XML)
        binding.btnAcaoDetalhe.setText("Vou doar");

        if (campanha.getTipoCampanha() != null && campanha.getTipoCampanha().equals("Paciente")) {
            // --- MOSTRAR LAYOUT DE PACIENTE ---
            binding.groupDetalhePaciente.setVisibility(View.VISIBLE);
            binding.groupDetalhePublica.setVisibility(View.GONE);

            String meta = campanha.getMetaDoadores() + (campanha.getMetaDoadores() > 1 ? " Doadores" : " Doador");
            binding.tvDetalheMeta.setText(meta);
            binding.tvDetalheTipoSanguineo.setText(campanha.getTipoSanguineoNecessario());
            binding.tvDetalheLocalPaciente.setText(campanha.getNomeHemocentro());
            binding.tvDetalheEnderecoPaciente.setText(campanha.getEnderecoHemocentro());
            binding.tvDetalheTelefonePaciente.setText(campanha.getContatoWhatsApp());

        } else {
            // --- MOSTRAR LAYOUT PÚBLICO ---
            binding.groupDetalhePaciente.setVisibility(View.GONE);
            binding.groupDetalhePublica.setVisibility(View.VISIBLE);

            binding.tvDetalheLocalPublica.setText(campanha.getNomeHemocentro());
            binding.tvDetalheTelefonePublica.setText(campanha.getContatoWhatsApp());
            binding.tvDetalheHorario.setText("Seg a Sex, das 08h às 17h"); // Valor "mockado"

            // <-- MUDANÇA 2: LINHA REMOVIDA -->
            // A linha abaixo foi removida para que o botão continue visível
            // binding.btnAcaoDetalhe.setVisibility(View.GONE);
        }
    }

    private void configurarCliques() {

        // Botão de Ação Principal (Vou doar)
        binding.btnAcaoDetalhe.setOnClickListener(v -> {
            // Simulação para a apresentação:
            Toast.makeText(getContext(), "Redirecionando para agendamento...", Toast.LENGTH_SHORT).show();

            // Lógica real (já implementada):
            // String url = "https://colsan.org.br/site/agendamento";
            // Intent i = new Intent(Intent.ACTION_VIEW);
            // i.setData(Uri.parse(url));
            // startActivity(i);
        });

        // Botão de Mapa (Apenas para Paciente)
        binding.btnDetalheMapa.setOnClickListener(v -> {
            if (campanhaAtual != null) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(campanhaAtual.getEnderecoHemocentro()));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(getContext(), "Google Maps não instalado.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Botão de Compartilhar (Comum a ambos)
        binding.btnCompartilharDetalhe.setOnClickListener(v -> {
            if (campanhaAtual != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = "Ajude nesta campanha de doação de sangue!\n\n" +
                        "*" + campanhaAtual.getTitulo() + "*\n" +
                        "Local: " + campanhaAtual.getNomeHemocentro() + "\n\n" +
                        "Baixe o app Doando+ e ajude a salvar vidas!";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(shareIntent, "Compartilhar Campanha"));
            }
        });
    }

    private void mostrarCarregando(boolean carregando) {
        if (carregando) {
            binding.progressBarDetalhe.setVisibility(View.VISIBLE);
            binding.scrollView.setVisibility(View.GONE);
            binding.btnAcaoDetalhe.setVisibility(View.GONE);
            binding.btnCompartilharDetalhe.setVisibility(View.GONE);
        } else {
            binding.progressBarDetalhe.setVisibility(View.GONE);
            binding.scrollView.setVisibility(View.VISIBLE);

            // <-- MUDANÇA 3: Garante que os botões voltem -->
            // (a lógica 'preencherDados' vai decidir sobre o btn_acao_detalhe)
            binding.btnCompartilharDetalhe.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}