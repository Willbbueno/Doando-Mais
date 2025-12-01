package com.upx.doando_mais.repository;

import android.net.Uri;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.upx.doando_mais.data.model.Doacao;

import java.util.UUID;

public class DoacaoRepository {

    private FirebaseFirestore db;
    private CollectionReference doacoesCollection;
    private CollectionReference usuariosCollection;
    private StorageReference storageRef;

    private MutableLiveData<Boolean> sucessoRegistroLiveData;
    private MutableLiveData<String> erroLiveData;

    public DoacaoRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.doacoesCollection = db.collection("doacoes");
        this.usuariosCollection = db.collection("usuarios");
        this.storageRef = FirebaseStorage.getInstance().getReference();

        this.sucessoRegistroLiveData = new MutableLiveData<>();
        this.erroLiveData = new MutableLiveData<>();
    }

    public void registrarDoacao(Doacao doacao, Uri uriComprovante) {
        if (uriComprovante != null) {
            String nomeArquivo = UUID.randomUUID().toString() + ".jpg";
            StorageReference comprovanteRef = storageRef.child("comprovantes/" + doacao.getIdUsuario() + "/" + nomeArquivo);

            comprovanteRef.putFile(uriComprovante)
                    .addOnSuccessListener(taskSnapshot -> {
                        comprovanteRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Pegou a URL, atualiza o objeto e salva no banco
                            doacao.setUrlComprovante(uri.toString());
                            salvarNoFirestore(doacao);
                        });
                    })
                    .addOnFailureListener(e -> erroLiveData.postValue("Erro ao enviar comprovante: " + e.getMessage()));
        } else {
            salvarNoFirestore(doacao);
        }
    }

    private void salvarNoFirestore(Doacao doacao) {
        // Gera um ID único para a doação
        String doacaoId = doacoesCollection.document().getId();
        doacao.setId(doacaoId);

        doacoesCollection.document(doacaoId).set(doacao)
                .addOnSuccessListener(aVoid -> {
                    // 2. Sucesso! Agora incrementa o contador do usuário
                    incrementarContadorUsuario(doacao.getIdUsuario());
                })
                .addOnFailureListener(e -> erroLiveData.postValue("Erro ao salvar doação: " + e.getMessage()));
    }

    private void incrementarContadorUsuario(String uid) {
        // Usa o FieldValue.increment(1) para aumentar o número atomicamente
        usuariosCollection.document(uid)
                .update("quantidadeDoacoes", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> {
                    sucessoRegistroLiveData.postValue(true);
                })
                .addOnFailureListener(e -> {
                    sucessoRegistroLiveData.postValue(true);
                });
    }

    public MutableLiveData<Boolean> getSucessoRegistroLiveData() {
        return sucessoRegistroLiveData;
    }

    public MutableLiveData<String> getErroLiveData() {
        return erroLiveData;
    }

    private androidx.lifecycle.MutableLiveData<java.util.List<Doacao>> listaDoacoesLiveData = new androidx.lifecycle.MutableLiveData<>();

    public void carregarDoacoes(String uid) {
        doacoesCollection.whereEqualTo("idUsuario", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    java.util.List<Doacao> lista = queryDocumentSnapshots.toObjects(Doacao.class);
                    listaDoacoesLiveData.postValue(lista);
                })
                .addOnFailureListener(e -> erroLiveData.postValue("Erro ao carregar doações: " + e.getMessage()));
    }

    public androidx.lifecycle.LiveData<java.util.List<Doacao>> getListaDoacoesLiveData() {
        return listaDoacoesLiveData;
    }
}