package com.upx.doando_mais.repository;
// classe que lida Login, cadastro, logout via firestore auth, cuida de toda a autenticação.

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
        // Limpa o estado de erro anterior
        erroAutenticacaoLiveData.postValue(null);

        firebaseAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // SUCESSO! O usuário está logado.

                            // 1. Atualiza o LiveData com o usuário logado
                            usuarioLogadoLiveData.postValue(firebaseAuth.getCurrentUser());
                        } else {
                            // FALHA! (Ex: usuário não encontrado, senha incorreta)

                            // 1. Envia a mensagem de erro para o ViewModel
                            String erro = task.getException() != null ? task.getException().getMessage() : "Erro desconhecido no login";
                            erroAutenticacaoLiveData.postValue(erro);
                        }
                    }
                });
    }

    // --- Método de Cadastro ---
    /**
     * Tenta criar um novo usuário no Firebase Authentication.
     * Atualiza os LiveData com o resultado da operação (sucesso ou falha).
     */
    public void cadastrar(String email, String senha) {

        // Limpa o estado de erro/sucesso anterior antes de uma nova tentativa
        erroAutenticacaoLiveData.postValue(null);
        cadastroSucessoLiveData.postValue(false);

        firebaseAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // SUCESSO! O usuário foi criado no Firebase Auth.

                            // 1. Atualiza o LiveData com o novo usuário logado
                            usuarioLogadoLiveData.postValue(firebaseAuth.getCurrentUser());

                            // 2. Avisa o ViewModel que esta etapa (Auth) foi concluída
                            cadastroSucessoLiveData.postValue(true);
                        } else {
                            // FALHA! (Ex: e-mail já em uso, senha fraca)

                            // 1. Envia a mensagem de erro para o ViewModel
                            String erro = task.getException() != null ? task.getException().getMessage() : "Erro desconhecido no cadastro";
                            erroAutenticacaoLiveData.postValue(erro);
                        }
                    }
                });
    }

    // --- Método de Logout ---
    public void logout() {
        firebaseAuth.signOut();
        usuarioLogadoLiveData.postValue(null); // Avisa que o usuário deslogou
    }


    // Getters para as viewModels
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