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


public class DetalheCampanhaFragment extends Fragment {

    private FragmentDetalheCampanhaBinding binding;
    private DetalheCampanhaViewModel detalheCampanhaViewModel;
    private String campanhaId;
    private Campanha campanhaAtual; // Para guardar a campanha carregada

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

        // 1. Pega o ID que foi enviado pelo Adapter
        if (getArguments() != null) {
            campanhaId = getArguments().getString("campanhaId");
        }

        // 2. Inicializa o ViewModel
        detalheCampanhaViewModel = new ViewModelProvider(this).get(DetalheCampanhaViewModel.class);

        // 3. Configura os Observadores
        configurarObservadores();

        // 4. Configura os Cliques (serão ativados após os dados carregarem)
        configurarCliques();

        // 5. Manda o ViewModel buscar os dados
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
                this.campanhaAtual = campanha; // Salva a campanha
                preencherDados(campanha);
                mostrarCarregando(false);
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
     * Preenche todos os campos com os dados da Campanha.
     * ESTA É A LÓGICA PRINCIPAL DA FASE 6.
     */
    private void preencherDados(Campanha campanha) {
        // --- 1. Preenche Dados Comuns ---
        binding.tvDetalheTitulo.setText(campanha.getTitulo());
        binding.tvDetalheOrganizador.setText("Criada por: " + campanha.getNomeOrganizador());
        binding.tvDetalheDescricao.setText(campanha.getDescricao());

        // --- 2. Lógica de Visibilidade (Paciente vs. Pública) ---
        if (campanha.getTipoCampanha() != null && campanha.getTipoCampanha().equals("Paciente")) {
            // --- MOSTRAR LAYOUT DE PACIENTE ---
            binding.groupDetalhePaciente.setVisibility(View.VISIBLE);
            binding.groupDetalhePublica.setVisibility(View.GONE);

            // Preenche os campos de Paciente
            String meta = campanha.getMetaDoadores() + (campanha.getMetaDoadores() > 1 ? " Doadores" : " Doador");
            binding.tvDetalheMeta.setText(meta);
            binding.tvDetalheTipoSanguineo.setText(campanha.getTipoSanguineoNecessario());
            binding.tvDetalheLocalPaciente.setText(campanha.getNomeHemocentro());
            binding.tvDetalheEnderecoPaciente.setText(campanha.getEnderecoHemocentro());
            binding.tvDetalheTelefonePaciente.setText(campanha.getContatoWhatsApp());

            // Configura o botão de Ação (Agendar)
            binding.btnAcaoDetalhe.setText("Agendar Doação");
            // (A lógica de clique está em configurarCliques())

        } else {
            // --- MOSTRAR LAYOUT PÚBLICO ---
            binding.groupDetalhePaciente.setVisibility(View.GONE);
            binding.groupDetalhePublica.setVisibility(View.VISIBLE);

            // Preenche os campos Públicos
            // (A "convocação" já está na descrição, mas podemos usar o campo de descrição aqui também)
            // binding.tvDetalheConvocacao.setText(campanha.getDescricao()); // Se quiser duplicar
            binding.tvDetalheLocalPublica.setText(campanha.getNomeHemocentro());
            binding.tvDetalheTelefonePublica.setText(campanha.getContatoWhatsApp());

            // TODO: O Horário de Atendimento precisa ser salvo no Hemocentro e na Campanha
            binding.tvDetalheHorario.setText("Seg a Sex, das 08h às 17h"); // Valor "mockado"

            // Configura o botão de Ação (Ele some)
            binding.btnAcaoDetalhe.setVisibility(View.GONE);
        }
    }

    /**
     * Configura os cliques dos botões (Google Maps, Agendamento, Compartilhar)
     */
    private void configurarCliques() {

        // Botão de Ação Principal (Agendar Doação - Apenas para Paciente)
        binding.btnAcaoDetalhe.setOnClickListener(v -> {
            // TODO: Fase 7 - Incrementar o contador no Firestore
            // detalheCampanhaViewModel.incrementarAgendamento(campanhaId);

            // Abre o link da Colsan (conforme diretriz)
            String url = "https://colsan.org.br/site/agendamento";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        // Botão de Mapa (Apenas para Paciente)
        binding.btnDetalheMapa.setOnClickListener(v -> {
            if (campanhaAtual != null) {
                // Abre o Google Maps com o endereço do hemocentro
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
            // (Os botões são mostrados ou escondidos pela lógica do preencherDados)
            binding.btnCompartilharDetalhe.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpa a referência do binding
    }
}