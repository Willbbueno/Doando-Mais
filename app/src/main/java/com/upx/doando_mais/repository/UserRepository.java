package com.upx.doando_mais.repository;

// vai interagir com a coleção Usuários no firestore
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upx.doando_mais.data.model.Usuario;

public class UserRepository {

    private static final String COLLECTION_NAME = "usuarios";
    private FirebaseFirestore db;
    private CollectionReference usuariosCollection;

    private MutableLiveData<Usuario> usuarioLiveData; // Informa os dados do usuário buscado
    private MutableLiveData<String> erroLiveData; // Informa se deu erro
    private MutableLiveData<Boolean> salvamentoSucessoLiveData; // Informa se o salvamento deu certo

    public UserRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.usuariosCollection = db.collection(COLLECTION_NAME);

        this.usuarioLiveData = new MutableLiveData<>();
        this.erroLiveData = new MutableLiveData<>();
        this.salvamentoSucessoLiveData = new MutableLiveData<>();
    }

    /**
     * Salva o objeto Usuario no Firestore usando o UID do Firebase Auth como ID do documento.
     * Atualiza os LiveData com o resultado da operação.
     *
     * @param uid O ID único do Firebase Authentication.
     * @param usuario O objeto Usuario com nome, cpf, tipoSanguineo, etc.
     */
    public void salvarUsuarioAdicional(String uid, Usuario usuario) {
        // Limpa o estado de erro/sucesso anterior
        erroLiveData.postValue(null);
        salvamentoSucessoLiveData.postValue(false);

        // Usa .document(uid) para definir o ID do documento como o UID do usuário
        // Usa .set(usuario) para salvar o objeto Java inteiro. O Firestore o converterá em um documento.
        usuariosCollection.document(uid).set(usuario)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // SUCESSO! Os dados do usuário foram salvos no Firestore.

                            // 1. Avisa o ViewModel que esta etapa foi concluída
                            salvamentoSucessoLiveData.postValue(true);
                        } else {
                            // FALHA!

                            // 1. Envia a mensagem de erro para o ViewModel
                            String erro = task.getException() != null ? task.getException().getMessage() : "Erro desconhecido ao salvar dados";
                            erroLiveData.postValue(erro);
                        }
                    }
                });
    }

    //Busca os dados de um usuário no Firestore usando seu UID.
    /**
     * Busca os dados de um usuário no Firestore usando seu UID.
     * (Implementação futura)
     * @param uid O ID do usuário logado.
     */
    public void buscarUsuario(String uid) {

    }

    public MutableLiveData<Usuario> getUsuarioLiveData() {
        return usuarioLiveData;
    }

    public MutableLiveData<String> getErroLiveData() {
        return erroLiveData;
    }

    public MutableLiveData<Boolean> getSalvamentoSucessoLiveData() {
        return salvamentoSucessoLiveData;
    }

    public LiveData<Boolean> getSalvamentoUsuarioSucessoLiveData() {
        return getSalvamentoUsuarioSucessoLiveData();
    }
}