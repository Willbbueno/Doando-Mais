package com.upx.doando_mais.ui.campanha_criar;
// ViewModel para criação de campanhas, também conecta a UI Fragment de camapanhas com a lógica de negócios "Repository".
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.upx.doando_mais.data.model.Campanha;
import com.upx.doando_mais.repository.CampaignRepository;

/**
 * ViewModel para a tela de Criação de Campanhas.
 * Conecta a UI (Fragment) com a lógica de negócios (Repository).
 */
public class CriarCampanhaViewModel extends ViewModel {

    private CampaignRepository campaignRepository;

    // LiveData que o Fragment vai observar
    private LiveData<Boolean> criacaoSucessoLiveData;
    private LiveData<String> erroLiveData;

    public CriarCampanhaViewModel() {
        // 1. Pega a instância do repositório
        campaignRepository = new CampaignRepository();

        // 2. "Pega emprestado" os LiveData do repositório
        criacaoSucessoLiveData = campaignRepository.getCriacaoCampanhaSucessoLiveData();
        erroLiveData = campaignRepository.getErroLiveData();
    }

    // --- Getters para o Fragment ---

    /**
     * O Fragment vai observar este LiveData para saber se o salvamento deu certo.
     */
    public LiveData<Boolean> getCriacaoSucessoLiveData() {
        return criacaoSucessoLiveData;
    }

    /**
     * O Fragment vai observar este LiveData para saber se deu algum erro.
     */
    public LiveData<String> getErroLiveData() {
        return erroLiveData;
    }

    // --- Ação ---

    /**
     * O Fragment chama este método quando o usuário clica em "Salvar".
     * O Fragment é responsável por criar o objeto 'Campanha' com os dados do formulário.
     *
     * @param novaCampanha O objeto Campanha preenchido com os dados da UI.
     */
    public void salvarCampanha(Campanha novaCampanha) {
        // Simplesmente repassa a tarefa para o repositório
        campaignRepository.criarCampanha(novaCampanha);
    }
}