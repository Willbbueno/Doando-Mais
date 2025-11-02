package com.upx.doando_mais.repository;
// Classe responsável por busca uma lista de campanhas do firebase

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.upx.doando_mais.data.model.Campanha;

import java.util.ArrayList;
import java.util.List;

public class CampaignRepository {

    private static final String TAG = "CampaignRepository";
    private static final String COLLECTION_NAME = "campanhas"; // Nome da coleção no Firestore

    private FirebaseFirestore db;
    private CollectionReference campanhasCollection;

    // LiveData para a lista de campanhas (para o Feed)
    private MutableLiveData<List<Campanha>> campanhasLiveData;

    // LiveData para erros
    private MutableLiveData<String> erroLiveData;

    public CampaignRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.campanhasCollection = db.collection(COLLECTION_NAME);
        this.campanhasLiveData = new MutableLiveData<>();
        this.erroLiveData = new MutableLiveData<>();
    }

    // --- Getters para o ViewModel ---

    public LiveData<List<Campanha>> getCampanhasLiveData() {
        return campanhasLiveData;
    }

    public LiveData<String> getErroLiveData() {
        return erroLiveData;
    }

    // --- Métodos de Acesso ao Banco ---

    /**
     * Busca todas as campanhas do Firestore em tempo real.
     * Ordena pelas mais recentes.
     */
    public void buscarTodasCampanhas() {

        // Query para buscar campanhas, ordenadas pela data de criação (mais novas primeiro)
        campanhasCollection.orderBy("dataCriacao", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Se der erro, informa o ViewModel
                        Log.w(TAG, "Erro ao buscar campanhas.", error);
                        erroLiveData.postValue("Erro ao carregar o feed: " + error.getMessage());
                        return;
                    }

                    if (value != null) {
                        // Converte os documentos em objetos "Campanha"
                        List<Campanha> listaCampanhas = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {

                            // Converte o documento do Firestore para nosso objeto Campanha.java
                            Campanha campanha = document.toObject(Campanha.class);

                            //Seta o ID do documento no objeto
                            campanha.setId(document.getId());

                            listaCampanhas.add(campanha);
                        }
                        // Envia a lista completa para o LiveData
                        campanhasLiveData.postValue(listaCampanhas);
                    }
                });
    }

    /**
     * (FUTURO) Método para criar uma nova campanha.
     * Você usará isso na tela "CriarCampanhaFragment".
     */
    public void criarCampanha(Campanha novaCampanha) {
        // Exemplo de como você faria para salvar uma nova:
        /*
        campanhasCollection.add(novaCampanha)
                .addOnSuccessListener(documentReference -> {
                    // Sucesso
                })
                .addOnFailureListener(e -> {
                    // Falha
                });
        */
    }
}