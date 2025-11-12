package com.upx.doando_mais.ui.acompanhamento;
// telas para exibir detalhes das camapanhas

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.upx.doando_mais.data.model.Campanha;
import com.upx.doando_mais.repository.CampaignRepository; // Importe o repositório

import java.util.List;

/**
 * ViewModel para a tela de Acompanhamento (Minhas Campanhas).
 * Conecta a UI (Fragment) com a lógica de negócios (Repository).
 */
public class AcompanhamentoViewModel extends ViewModel {


    // LiveData que o Fragment vai observar
    private LiveData<List<Campanha>> minhasCampanhasLiveData;
    private CampaignRepository campaignRepository;
    private LiveData<String> erroLiveData;

    public AcompanhamentoViewModel() {
        // 1. Cria a instância do repositório
        campaignRepository = new CampaignRepository();

        // 2. "Pega emprestado" os LiveData do repositório
        minhasCampanhasLiveData = campaignRepository.getMinhasCampanhasLiveData();
        erroLiveData = campaignRepository.getErroLiveData();
    }

    // --- Getters para o Fragment ---

    /**
     * O Fragment vai observar este LiveData para receber a lista de
     * campanhas que o usuário criou.
     */
    public LiveData<List<Campanha>> getMinhasCampanhasLiveData() {
        return minhasCampanhasLiveData;
    }

    /**
     * O Fragment vai observar este LiveData para saber se deu algum erro.
     */
    public LiveData<String> getErroLiveData() {
        return erroLiveData;
    }

    // --- Ação ---

    /**
     * O Fragment chama este método no "onViewCreated", passando o ID
     * do usuário que está logado.
     *
     * @param uidCriador O ID do usuário logado (para o filtro).
     */
    public void carregarMinhasCampanhas(String uidCriador) {
        // Simplesmente repassa a tarefa para o repositório
        campaignRepository.buscarMinhasCampanhas(uidCriador);
    }
    /**
     * O Fragment chama este método para excluir uma campanha.
     * @param campanhaId O ID da campanha a ser excluída.
     */
    public void excluirCampanha(String campanhaId) {
        campaignRepository.excluirCampanha(campanhaId);
    }
}
