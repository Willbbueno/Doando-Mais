package com.upx.doando_mais.repository;

// vai interagir com a coleção Usuários no firestore
import androidx.lifecycle.MutableLiveData;
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

    //Salva os dados adicionais do usuário no Firestore após o cadastro.
    public void salvarUsuarioAdicional(String uid, Usuario usuario) {
    }

    //Busca os dados de um usuário no Firestore usando seu UID.

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
}