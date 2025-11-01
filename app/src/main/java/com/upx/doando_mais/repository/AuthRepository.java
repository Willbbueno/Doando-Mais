package com.upx.doando_mais.repository;
// classe que lida Login, cadastro, logout via firestore auth, cuida de toda a autenticação.

import com.google.firebase.auth.FirebaseAuth;
import androidx.lifecycle.MutableLiveData; // Importante
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {

    private FirebaseAuth firebaseAuth;
    private MutableLiveData<FirebaseUser> usuarioLogadoLiveData; // Informa quem está logado
    private MutableLiveData<String> erroAutenticacaoLiveData; // Informa se deu erro
    private MutableLiveData<Boolean> cadastroSucessoLiveData; // Informa se o cadastro deu certo

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.usuarioLogadoLiveData = new MutableLiveData<>();
        this.erroAutenticacaoLiveData = new MutableLiveData<>();
        this.cadastroSucessoLiveData = new MutableLiveData<>();

        // Primeiro verifica se já existe um usuário logado ao iniciar o app
        if (firebaseAuth.getCurrentUser() != null) {
            usuarioLogadoLiveData.postValue(firebaseAuth.getCurrentUser());
        }
    }

    // Métodos para autenticação - LOGIN, CADASTRAR E LOGOUT
    public void login(String email, String senha) {

    }

    // --- Método de Cadastro ---
    public void cadastrar(String email, String senha) {

    }

    // --- Método de Logout ---
    public void logout() {
        firebaseAuth.signOut();
        usuarioLogadoLiveData.postValue(null); // Avisa que o usuário deslogou
    }



    public MutableLiveData<FirebaseUser> getUsuarioLogadoLiveData() {
        return usuarioLogadoLiveData;
    }

    public MutableLiveData<String> getErroAutenticacaoLiveData() {
        return erroAutenticacaoLiveData;
    }

    public MutableLiveData<Boolean> getCadastroSucessoLiveData() {
        return cadastroSucessoLiveData;
    }
}