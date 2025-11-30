package com.upx.doando_mais.repository;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.upx.doando_mais.data.model.Usuario;

public class UserRepository {

    private static final String COLLECTION_NAME = "usuarios";

    // --- Serviços do Firebase ---
    private FirebaseFirestore db;
    private CollectionReference usuariosCollection;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    // --- LiveData ---
    private MutableLiveData<Usuario> dadosUsuarioLiveData;
    private MutableLiveData<String> erroLiveData;

    // ⬇️ 1. VARIÁVEL QUE FALTAVA (DECLARAÇÃO) ⬇️
    private MutableLiveData<Boolean> salvamentoUsuarioSucessoLiveData;

    public UserRepository() {
        // Inicializa Serviços
        this.db = FirebaseFirestore.getInstance();
        this.usuariosCollection = db.collection(COLLECTION_NAME);
        this.storage = FirebaseStorage.getInstance();
        this.storageRef = storage.getReference();

        // Inicializa LiveData
        this.dadosUsuarioLiveData = new MutableLiveData<>();
        this.erroLiveData = new MutableLiveData<>();

        // ⬇️ 2. VARIÁVEL QUE FALTAVA (INICIALIZAÇÃO) ⬇️
        this.salvamentoUsuarioSucessoLiveData = new MutableLiveData<>();
    }

    /**
     * Salva ou atualiza os dados de um usuário no Firestore.
     */
    public void salvarUsuarioAdicional(String uid, Usuario usuario) {
        erroLiveData.postValue(null);

        // 'set' (gravar/substituir) o documento com o ID do usuário
        usuariosCollection.document(uid).set(usuario)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        salvamentoUsuarioSucessoLiveData.postValue(true);
                        dadosUsuarioLiveData.postValue(usuario); // Atualiza os dados locais
                    } else {
                        String erro = task.getException() != null ? task.getException().getMessage() : "Erro desconhecido ao salvar dados";
                        erroLiveData.postValue(erro);
                    }
                });
    }

    /**
     * Busca os dados completos de um usuário no Firestore.
     */
    public void buscarUsuario(String uid) {
        if (uid == null || uid.isEmpty()) {
            erroLiveData.postValue("UID nulo. Não é possível buscar usuário.");
            return;
        }

        usuariosCollection.document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Usuario usuario = document.toObject(Usuario.class);
                            dadosUsuarioLiveData.postValue(usuario);
                        } else {
                            erroLiveData.postValue("Usuário não encontrado no banco de dados.");
                            dadosUsuarioLiveData.postValue(null);
                        }
                    } else {
                        String erro = task.getException() != null ? task.getException().getMessage() : "Erro ao buscar dados do usuário.";
                        erroLiveData.postValue(erro);
                    }
                });
    }

    /**
     * Faz o upload da foto de perfil para o Firebase Storage
     * e atualiza a URL no Firestore.
     */
    public void uploadFotoPerfil(String uid, Uri imageUri) {
        if (uid == null || imageUri == null) {
            erroLiveData.postValue("Usuário ou imagem inválida.");
            return;
        }

        StorageReference fotoRef = storageRef.child("profile_images/" + uid + ".jpg");

        fotoRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Upload deu certo, agora pega a URL de download
                    fotoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();

                        // Salva a URL no documento do usuário no Firestore
                        usuariosCollection.document(uid)
                                .update("urlFotoPerfil", downloadUrl)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("UserRepository", "URL da foto salva no Firestore.");
                                    // Recarrega os dados do usuário para atualizar a foto na UI
                                    buscarUsuario(uid);
                                })
                                .addOnFailureListener(e -> {
                                    erroLiveData.postValue("Erro ao salvar URL: " + e.getMessage());
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    // O upload (putFile) falhou
                    erroLiveData.postValue("Erro no upload da foto: " + e.getMessage());
                });
    }

    // --- Getters para os ViewModels ---

    public LiveData<Usuario> getDadosUsuarioLiveData() {
        return dadosUsuarioLiveData;
    }

    public LiveData<String> getErroLiveData() {
        return erroLiveData;
    }

    // ⬇️ 3. AGORA ESTES MÉTODOS FUNCIONARÃO ⬇️
    public LiveData<Boolean> getSalvamentoUsuarioSucessoLiveData() {
        return this.salvamentoUsuarioSucessoLiveData;
    }

    public void limparStatusSalvamento() {
        if (salvamentoUsuarioSucessoLiveData != null) {
            salvamentoUsuarioSucessoLiveData.setValue(null);
        }
    }
}