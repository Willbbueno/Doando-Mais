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
    private MutableLiveData<List<Campanha>> minhasCampanhasLiveData;
    private MutableLiveData<Boolean> atualizacaoCampanhaSucessoLiveData;

    public CampaignRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.campanhasCollection = db.collection(COLLECTION_NAME);
        this.campanhasLiveData = new MutableLiveData<>();
        this.erroLiveData = new MutableLiveData<>();
        this.criacaoCampanhaSucessoLiveData = new MutableLiveData<>();
        this.campanhaDetalheLiveData = new MutableLiveData<>();
        this.minhasCampanhasLiveData = new MutableLiveData<>();
        this.atualizacaoCampanhaSucessoLiveData = new MutableLiveData<>();

    }

    // --- Getters para o ViewModel ---

    public LiveData<List<Campanha>> getCampanhasLiveData() {
        return campanhasLiveData;
    }
    public LiveData<String> getErroLiveData() {
        return erroLiveData;
    }
    public LiveData<Boolean> getCriacaoCampanhaSucessoLiveData() { return criacaoCampanhaSucessoLiveData; }
    public LiveData<Campanha> getCampanhaDetalheLiveData() { return campanhaDetalheLiveData;}
    public LiveData<List<Campanha>> getMinhasCampanhasLiveData() {return minhasCampanhasLiveData;}
    public LiveData<Boolean> getAtualizacaoCampanhaSucessoLiveData() { return atualizacaoCampanhaSucessoLiveData;}
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
    /**
     * Busca, em tempo real, apenas as campanhas criadas pelo usuário logado.
     * Usado pela tela de Acompanhamento.
     *
     * @param uidCriador O ID do usuário logado.
     */
    public void buscarMinhasCampanhas(String uidCriador) {
        if (uidCriador == null || uidCriador.isEmpty()) {
            // Se o UID for nulo, retorna uma lista vazia
            minhasCampanhasLiveData.postValue(new ArrayList<>());
            return;
        }

        // Query para buscar campanhas onde "criadorUid" == uidCriador
        // Ordena pelas mais recentes
        campanhasCollection.whereEqualTo("criadorUid", uidCriador)
                .orderBy("dataCriacao", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Erro ao buscar 'minhas campanhas'.", error);
                        erroLiveData.postValue("Erro ao carregar suas campanhas: " + error.getMessage());
                        return;
                    }

                    if (value != null) {
                        // Sucesso! Converte os documentos
                        List<Campanha> listaCampanhas = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            Campanha campanha = document.toObject(Campanha.class);
                            campanha.setId(document.getId());
                            listaCampanhas.add(campanha);
                        }
                        // Envia a lista filtrada para o LiveData
                        minhasCampanhasLiveData.postValue(listaCampanhas);
                    }
                });
    }
    /**
     * Exclui uma campanha específica do Firestore.
     * @param campanhaId O ID do documento a ser excluído.
     */
    public void excluirCampanha(String campanhaId) {
        if (campanhaId == null || campanhaId.isEmpty()) {
            erroLiveData.postValue("ID da campanha inválido para exclusão.");
            return;
        }

        campanhasCollection.document(campanhaId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Campanha excluída com sucesso: " + campanhaId);

                    // Pega a lista atual que está no LiveData
                    List<Campanha> listaAtual = minhasCampanhasLiveData.getValue();

                    // Verifica se a lista não é nula
                    if (listaAtual != null) {
                        // 1. Cria uma nova lista (isto é crucial para o LiveData e o Adapter notarem a mudança após a exclusão)
                        List<Campanha> novaLista = new ArrayList<>(listaAtual);

                        // 2. Remove o item da nova lista usando o ID
                        novaLista.removeIf(campanha -> campanha.getId().equals(campanhaId));

                        // 3. Posta a nova lista atualizada.
                        minhasCampanhasLiveData.postValue(novaLista);
                    }

                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Erro ao excluir campanha", e);
                    erroLiveData.postValue("Erro ao excluir campanha: " + e.getMessage());
                });
    }

    /**
     * Atualiza uma campanha existente no Firestore.
     * @param campanhaAtualizada O objeto Campanha com os dados modificados.
     */
    public void atualizarCampanha(Campanha campanhaAtualizada) {
        String id = campanhaAtualizada.getId();
        if (id == null || id.isEmpty()) {
            erroLiveData.postValue("ID da campanha é inválido. Não é possível atualizar.");
            return;
        }

        // Limpa o estado anterior
        atualizacaoCampanhaSucessoLiveData.postValue(false);
        erroLiveData.postValue(null);

        // Usa .document(id).set(objeto) para sobrescrever os dados da campanha
        campanhasCollection.document(id).set(campanhaAtualizada)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Campanha atualizada com sucesso: " + id);
                    atualizacaoCampanhaSucessoLiveData.postValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Erro ao atualizar campanha", e);
                    erroLiveData.postValue("Erro ao atualizar: " + e.getMessage());
                });
    }

    /**
     * Reseta o LiveData de sucesso da criação para 'false'.
     * Isso evita que o Fragmento reaja a um sucesso antigo.
     */
    public void resetarSucessoCriacao() {
        if (criacaoCampanhaSucessoLiveData != null) {
            criacaoCampanhaSucessoLiveData.postValue(false);
        }
    }
}

