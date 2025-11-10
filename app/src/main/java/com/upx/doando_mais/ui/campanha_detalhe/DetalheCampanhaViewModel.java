package com.upx.doando_mais.ui.campanha_detalhe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.upx.doando_mais.data.model.Campanha;
import com.upx.doando_mais.repository.CampaignRepository; // Importe o repositório

/**
 * ViewModel para a tela de Detalhes da Campanha.
 * Busca e armazena os dados de uma campanha específica.
 */
public class DetalheCampanhaViewModel extends ViewModel {

    private CampaignRepository campaignRepository;

    // LiveData que o Fragment vai observar
    private LiveData<Campanha> campanhaDetalheLiveData;
    private LiveData<String> erroLiveData;

    public DetalheCampanhaViewModel() {
        // 1. Pega a instância do repositório
        campaignRepository = new CampaignRepository();

        // 2. "Pega emprestado" os LiveData do repositório
        campanhaDetalheLiveData = campaignRepository.getCampanhaDetalheLiveData();
        erroLiveData = campaignRepository.getErroLiveData();
    }

    // --- Getters para o Fragment ---

    /**
     * O Fragment vai observar este LiveData para receber o objeto Campanha.
     */
    public LiveData<Campanha> getCampanhaDetalheLiveData() {
        return campanhaDetalheLiveData;
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
     * que ele recebeu do Feed.
     *
     * @param campanhaId O ID da campanha a ser buscada.
     */
    public void carregarDetalhesCampanha(String campanhaId) {
        // Simplesmente repassa a tarefa para o repositório
        campaignRepository.buscarCampanhaPorId(campanhaId);
    }
}
