package com.upx.doando_mais.ui.feed;

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

        // 3. Carrega o feed inicial (sem filtros)
        carregarCampanhasFiltradas(null, null);
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
     * Pede ao repositório para buscar as campanhas, aplicando filtros.
     * O Fragment chamará este método.
     *
     * @param tipo (Tipo sanguíneo) ou null para todos
     * @param cidade (Cidade) ou null para todas
     */
    public void carregarCampanhasFiltradas(String tipo, String cidade) {

        campaignRepository.buscarCampanhasFiltradas(tipo, cidade);
    }
}