package com.upx.doando_mais.repository;
// Classe responsável por busca uma lista de campanhas do firebase

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    // LiveData para criação das campanhas, para que apareçam no feed assim que fossem criadas
    private MutableLiveData<Boolean> criacaoCampanhaSucessoLiveData;

    // LiveData para a campanha específica (Tela de Detalhe)
    private MutableLiveData<Campanha> campanhaDetalheLiveData;

    public CampaignRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.campanhasCollection = db.collection(COLLECTION_NAME);
        this.campanhasLiveData = new MutableLiveData<>();
        this.erroLiveData = new MutableLiveData<>();
        this.criacaoCampanhaSucessoLiveData = new MutableLiveData<>();
        this.campanhaDetalheLiveData = new MutableLiveData<>();
    }

    // --- Getters para o ViewModel ---

    public LiveData<List<Campanha>> getCampanhasLiveData() {
        return campanhasLiveData;
    }
    public LiveData<String> getErroLiveData() {
        return erroLiveData;
    }
    public LiveData<Boolean> getCriacaoCampanhaSucessoLiveData() { return criacaoCampanhaSucessoLiveData; }
    public LiveData<Campanha> getCampanhaDetalheLiveData() {
        return campanhaDetalheLiveData;
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
        // Limpa o estado anterior
        erroLiveData.postValue(null);
        criacaoCampanhaSucessoLiveData.postValue(false);

        // O Firestore preenche o dataCriacao automaticamente por causa do @ServerTimestamp
        campanhasCollection.add(novaCampanha)
                .addOnSuccessListener(documentReference -> {
                    // Campanha salva.
                    Log.d(TAG, "Campanha salva com ID: " + documentReference.getId());
                    criacaoCampanhaSucessoLiveData.postValue(true);
                })
                .addOnFailureListener(e -> {
                    // Se falhar, aparece msg de erro.
                    Log.w(TAG, "Erro ao criar campanha", e);
                    erroLiveData.postValue("Erro ao criar campanha: " + e.getMessage());
                    criacaoCampanhaSucessoLiveData.postValue(false);
                });
    }

    /**
     * Busca uma única campanha no Firestore usando seu ID de documento.
     * Usado pela tela de DetalheCampanha.
     *
     * @param campanhaId O ID do documento da campanha a ser buscada.
     */
    public void buscarCampanhaPorId(String campanhaId) {
        if (campanhaId == null || campanhaId.isEmpty()) {
            erroLiveData.postValue("ID da campanha é inválido.");
            return;
        }

        // Limpa dados anteriores
        campanhaDetalheLiveData.postValue(null);
        erroLiveData.postValue(null);

        // Busca o documento específico pelo ID
        campanhasCollection.document(campanhaId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // SUCESSO! Converte o documento no nosso objeto Campanha.java
                            Campanha campanha = document.toObject(Campanha.class);
                            if (campanha != null) {
                                campanha.setId(document.getId()); // Seta o ID
                                campanhaDetalheLiveData.postValue(campanha); // Envia o objeto
                            }
                        } else {
                            // Documento não encontrado
                            Log.w(TAG, "Nenhuma campanha encontrada com o ID: " + campanhaId);
                            erroLiveData.postValue("Campanha não encontrada.");
                        }
                    } else {
                        // Erro na busca
                        Log.e(TAG, "Erro ao buscar campanha por ID", task.getException());
                        erroLiveData.postValue("Erro ao carregar detalhes: " + task.getException().getMessage());
                    }
                });
    }
}
