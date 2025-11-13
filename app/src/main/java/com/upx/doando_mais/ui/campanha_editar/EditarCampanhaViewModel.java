package com.upx.doando_mais.ui.campanha_editar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.upx.doando_mais.data.model.Campanha;
import com.upx.doando_mais.repository.CampaignRepository;

/**
 * ViewModel para a tela de Edição de Campanhas.
 * Combina a lógica de buscar uma campanha e de atualizar uma campanha.
 */
public class EditarCampanhaViewModel extends ViewModel {

    private CampaignRepository campaignRepository;

    // LiveData que o Fragment vai observar
    private LiveData<Campanha> campanhaParaEditarLiveData; // Dados para preencher o form
    private LiveData<Boolean> atualizacaoSucessoLiveData; // Para saber se salvou
    private LiveData<String> erroLiveData; // Para erros

    public EditarCampanhaViewModel() {
        this.campaignRepository = new CampaignRepository();

        // "Pega emprestado" os LiveData do repositório
        // Usamos o 'detalhe' para buscar os dados
        this.campanhaParaEditarLiveData = campaignRepository.getCampanhaDetalheLiveData();
        // Usamos o 'atualizacao' para salvar
        this.atualizacaoSucessoLiveData = campaignRepository.getAtualizacaoCampanhaSucessoLiveData();
        this.erroLiveData = campaignRepository.getErroLiveData();
    }

    // --- Getters para o Fragment ---

    public LiveData<Campanha> getCampanhaParaEditarLiveData() {
        return campanhaParaEditarLiveData;
    }

    public LiveData<Boolean> getAtualizacaoSucessoLiveData() {
        return atualizacaoSucessoLiveData;
    }

    public LiveData<String> getErroLiveData() {
        return erroLiveData;
    }

    // --- Ações (chamadas pelo Fragment) ---

    /**
     * Chamado para buscar os dados da campanha e preencher o formulário.
     */
    public void carregarCampanha(String campanhaId) {
        // Reutiliza o método que já tínhamos
        campaignRepository.buscarCampanhaPorId(campanhaId);
    }

    /**
     * Chamado quando o usuário clica em "Salvar Alterações".
     */
    public void salvarEdicao(Campanha campanhaAtualizada) {
        // Chama o novo método que criamos no repositório
        campaignRepository.atualizarCampanha(campanhaAtualizada);
    }
}