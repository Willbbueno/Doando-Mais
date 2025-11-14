package com.upx.doando_mais.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.upx.doando_mais.data.model.Hemocentro; // Importe seu modelo
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório para buscar dados da coleção "hemocentros" no Firestore.
 * (Ex: Lista de locais da Colsan)
 */
public class HemocentroRepository {

    private static final String TAG = "HemocentroRepository";
    private static final String COLLECTION_NAME = "hemocentros"; // Nome da coleção no Firestore

    private FirebaseFirestore db;
    private CollectionReference hemocentroCollection;

    // LiveData para a lista de locais
    private MutableLiveData<List<Hemocentro>> hemocentrosLiveData;
    private MutableLiveData<String> erroLiveData; // Podemos reutilizar o erro do Auth/Campaign

    public HemocentroRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.hemocentroCollection = db.collection(COLLECTION_NAME);
        this.hemocentrosLiveData = new MutableLiveData<>();
        this.erroLiveData = new MutableLiveData<>();
    }

    // --- Getters para o ViewModel ---

    public LiveData<List<Hemocentro>> getHemocentrosLiveData() {
        return hemocentrosLiveData;
    }

    public LiveData<String> getErroLiveData() {
        return erroLiveData;
    }

    // --- Ação ---

    /**
     * Busca todos os locais de doação (Hemocentros) do Firestore.
     */
    public void buscarTodosHemocentros() {

        // Usamos addSnapshotListener para que a lista se atualize
        // se um novo hemocentro for adicionado no futuro.
        hemocentroCollection.orderBy("nome") // Ordena por nome
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Erro ao buscar hemocentros.", error);
                        erroLiveData.postValue("Erro ao carregar locais: " + error.getMessage());
                        return;
                    }

                    if (value != null) {
                        List<Hemocentro> listaLocais = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            // Converte o documento no nosso objeto Hemocentro.java
                            Hemocentro hemocentro = document.toObject(Hemocentro.class);
                            hemocentro.setId(document.getId()); // Guarda o ID do documento
                            listaLocais.add(hemocentro);
                        }
                        // Envia a lista completa para o LiveData
                        hemocentrosLiveData.postValue(listaLocais);
                    }
                });
    }
}