package com.upx.doando_mais.ui.feed;
// classe para criar uma instancia do campaignRepository e deixar observável os livedatas para o feedfragment. Na prática irá guardar o dado para a tela.
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.upx.doando_mais.data.model.Campanha;
import com.upx.doando_mais.repository.CampaignRepository; // Importe o repositório

import java.util.List;

public class FeedViewModel extends ViewModel {

    private CampaignRepository campaignRepository;

    // LiveData que o Fragment vai observar
    private LiveData<List<Campanha>> campanhasLiveData;
    private LiveData<String> erroLiveData;

    // Construtor
    public FeedViewModel() {
        // 1. Cria a instância do repositório
        campaignRepository = new CampaignRepository();

        // 2. "Pega emprestado" os LiveData do repositório
        campanhasLiveData = campaignRepository.getCampanhasLiveData();
        erroLiveData = campaignRepository.getErroLiveData();
    }

    // --- Getters para o Fragment ---

    public LiveData<List<Campanha>> getCampanhasLiveData() {
        return campanhasLiveData;
    }

    public LiveData<String> getErroLiveData() {
        return erroLiveData;
    }

    // --- Ação ---

    /**
     * Pede ao repositório para começar a buscar as campanhas.
     * O Fragment chamará este método.
     */
    public void carregarCampanhas() {
        campaignRepository.buscarTodasCampanhas();
    }
}