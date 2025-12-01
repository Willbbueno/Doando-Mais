package com.upx.doando_mais.ui.auth;

import android.app.Application;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.upx.doando_mais.data.model.Usuario;
import com.upx.doando_mais.repository.AuthRepository;
import com.upx.doando_mais.repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel {

    private AuthRepository authRepository;
    private UserRepository userRepository;

    // LiveData Internos
    private LiveData<FirebaseUser> usuarioLogadoLiveData;
    private LiveData<Boolean> cadastroSucessoLiveData;
    private LiveData<String> erroAuthRepoLiveData;
    private LiveData<String> erroUserRepoLiveData;

    // LiveData Expostos para a UI
    private MutableLiveData<String> erroLiveData;
    private LiveData<Boolean> salvamentoUsuarioSucessoLiveData;
    private LiveData<Usuario> dadosUsuarioLiveData;

    // Observadores
    private Observer<Boolean> cadastroSucessoObserver;
    private Observer<FirebaseUser> usuarioLogadoObserver;
    private Observer<String> authErrorObserver;
    private Observer<String> userErrorObserver;

    // Estado
    private Usuario usuarioPendente;

    public AuthViewModel(@NonNull Application application) {
        super(application);

        this.authRepository = new AuthRepository();
        this.userRepository = new UserRepository();

        this.erroLiveData = new MutableLiveData<>();
        this.usuarioLogadoLiveData = authRepository.getUsuarioLogadoLiveData();
        this.cadastroSucessoLiveData = authRepository.getCadastroSucessoLiveData();
        this.salvamentoUsuarioSucessoLiveData = userRepository.getSalvamentoUsuarioSucessoLiveData();
        this.dadosUsuarioLiveData = userRepository.getDadosUsuarioLiveData();
        this.erroAuthRepoLiveData = authRepository.getErroAutenticacaoLiveData();
        this.erroUserRepoLiveData = userRepository.getErroLiveData();

        setupObservers();
    }

    private void setupObservers() {
        // ORQUESTRAÇÃO DE CADASTRO (Auth -> Firestore)
        cadastroSucessoObserver = sucesso -> {
            if (sucesso) {
                FirebaseUser user = usuarioLogadoLiveData.getValue();
                if (user != null && usuarioPendente != null) {
                    usuarioPendente.setUid(user.getUid());
                    userRepository.salvarUsuarioAdicional(user.getUid(), usuarioPendente);
                    usuarioPendente = null;
                }
            }
        };
        cadastroSucessoLiveData.observeForever(cadastroSucessoObserver);

        // ORQUESTRAÇÃO DE LOGIN / LOGOUT (Auth -> Firestore)
        usuarioLogadoObserver = firebaseUser -> {
            if (firebaseUser != null) {
                userRepository.buscarUsuario(firebaseUser.getUid());
            } else {
                ((MutableLiveData<Usuario>) dadosUsuarioLiveData).postValue(null);
            }
        };
        usuarioLogadoLiveData.observeForever(usuarioLogadoObserver);

        // ORQUESTRAÇÃO DE ERROS (Unifica erros)
        authErrorObserver = erro -> {
            if (erro != null) erroLiveData.postValue(erro);
        };
        userErrorObserver = erro -> {
            if (erro != null) erroLiveData.postValue(erro);
        };
        erroAuthRepoLiveData.observeForever(authErrorObserver);
        erroUserRepoLiveData.observeForever(userErrorObserver);
    }

    public void cadastrar(Usuario novoUsuario, String senha) {
        this.usuarioPendente = novoUsuario;
        authRepository.cadastrar(novoUsuario.getEmail(), senha);
    }

    public void login(String email, String senha) {
        authRepository.login(email, senha);
    }

    public void logout() {
        authRepository.logout();
    }

    public void atualizarDadosUsuario(Usuario usuarioAtualizado) {
        String uid = getUsuarioLogadoUid();
        if (uid != null) {
            usuarioAtualizado.setUid(uid);
            if(usuarioLogadoLiveData.getValue() != null) {
                usuarioAtualizado.setEmail(usuarioLogadoLiveData.getValue().getEmail());
            }
            userRepository.salvarUsuarioAdicional(uid, usuarioAtualizado);
        } else {
            erroLiveData.postValue("Usuário não está logado. Não foi possível salvar.");
        }
    }

    public void uploadFotoPerfil(Uri imageUri) {
        String uid = getUsuarioLogadoUid();
        if (uid != null && imageUri != null) {
            userRepository.uploadFotoPerfil(uid, imageUri);
        } else {
            erroLiveData.postValue("Usuário não está logado. Não é possível enviar a foto.");
        }
    }

    private String getUsuarioLogadoUid() {
        FirebaseUser user = usuarioLogadoLiveData.getValue();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    // --- GETTERS PARA A UI OBSERVAR ---

    public LiveData<FirebaseUser> getUsuarioLogadoLiveData() {
        return usuarioLogadoLiveData;
    }

    public LiveData<Boolean> getSalvamentoUsuarioSucessoLiveData() {
        return salvamentoUsuarioSucessoLiveData;
    }

    public LiveData<Usuario> getDadosUsuarioLiveData() {
        return dadosUsuarioLiveData;
    }

    public LiveData<String> getErroLiveData() {
        return erroLiveData;
    }

    public void limparErro() {
        erroLiveData.setValue(null);
    }

    // ⬇️ --- CORREÇÃO DO ERRO 2 (Parte A) --- ⬇️
    /**
     * Limpa o status de sucesso do salvamento.
     */
    public void limparStatusSalvamento() {
        // Pede ao repositório para limpar seu LiveData
        userRepository.limparStatusSalvamento();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        cadastroSucessoLiveData.removeObserver(cadastroSucessoObserver);
        usuarioLogadoLiveData.removeObserver(usuarioLogadoObserver);
        erroAuthRepoLiveData.removeObserver(authErrorObserver);
        erroUserRepoLiveData.removeObserver(userErrorObserver);
    }

    public void recarregarUsuario() {
        FirebaseUser user = usuarioLogadoLiveData.getValue();
        if (user != null) {
            userRepository.buscarUsuario(user.getUid());
        }
    }
}