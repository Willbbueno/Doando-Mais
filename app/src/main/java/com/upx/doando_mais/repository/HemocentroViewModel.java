package com.upx.doando_mais.repository; // (Ou o pacote que você escolheu, ex: ui.campanha_criar)

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.upx.doando_mais.data.model.Hemocentro;
import java.util.List;

/**
 * ViewModel dedicado a buscar e fornecer a lista de Hemocentros (Locais da Colsan).
 */
public class HemocentroViewModel extends ViewModel {

    private HemocentroRepository hemocentroRepository;
    private LiveData<List<Hemocentro>> hemocentrosLiveData;
    private LiveData<String> erroLiveData;

    public HemocentroViewModel() {
        this.hemocentroRepository = new HemocentroRepository();
        this.hemocentrosLiveData = hemocentroRepository.getHemocentrosLiveData();
        this.erroLiveData = hemocentroRepository.getErroLiveData();

        // Já manda buscar a lista assim que o ViewModel for criado
        carregarHemocentros();
    }

    // --- Getters para o Fragment ---
    public LiveData<List<Hemocentro>> getHemocentrosLiveData() {
        return hemocentrosLiveData;
    }

    public LiveData<String> getErroLiveData() {
        return erroLiveData;
    }

    // --- Ação ---
    public void carregarHemocentros() {
        hemocentroRepository.buscarTodosHemocentros();
    }
}