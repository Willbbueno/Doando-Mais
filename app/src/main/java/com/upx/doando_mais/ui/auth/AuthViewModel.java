package com.upx.doando_mais.ui.auth;
// Este pacote delega o trabalho para os repositórios

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.upx.doando_mais.data.model.Usuario;
import com.upx.doando_mais.repository.AuthRepository;
import com.upx.doando_mais.repository.UserRepository;

import com.google.firebase.auth.FirebaseUser;
import androidx.lifecycle.Observer;

public class AuthViewModel extends AndroidViewModel {

    private AuthRepository authRepository;
    private UserRepository userRepository;

    // LiveData
    private LiveData<FirebaseUser> usuarioLogadoLiveData;
    private LiveData<String> erroAutenticacaoLiveData;
    private LiveData<Boolean> cadastroSucessoLiveData;
    private LiveData<Boolean> salvamentoUsuarioSucessoLiveData;

    // Guarda o usuário que está sendo cadastrado
    private Usuario usuarioPendente;

    public AuthViewModel(@NonNull Application application) {
        super(application);

        this.authRepository = new AuthRepository();
        this.userRepository = new UserRepository();

        // Conecta o LiveData da ViewModel com o LiveData do Repositório
        this.usuarioLogadoLiveData = authRepository.getUsuarioLogadoLiveData();
        this.erroAutenticacaoLiveData = authRepository.getErroAutenticacaoLiveData();
        this.salvamentoUsuarioSucessoLiveData = userRepository.getSalvamentoUsuarioSucessoLiveData();

        // --- ORQUESTRAÇÃO DO CADASTRO ---
        // Aqui está a mágica: A ViewModel "ouve" o AuthRepository.
        this.cadastroSucessoLiveData = authRepository.getCadastroSucessoLiveData();
        cadastroSucessoLiveData.observeForever(sucesso -> {
            if (sucesso) {
                // Passo 2: Auth foi criado com sucesso!
                FirebaseUser user = usuarioLogadoLiveData.getValue();
                if (user != null && usuarioPendente != null) {
                    // Atualiza o UID no objeto pendente
                    usuarioPendente.setUid(user.getUid());

                    // Passo 3: Salva os dados no Firestore
                    userRepository.salvarUsuarioAdicional(user.getUid(), usuarioPendente);
                    usuarioPendente = null; // Limpa o usuário pendente
                }
            }
        });
    }

    /**
     * Inicia o processo de cadastro (Passo 1).
     * Recebe todos os dados da View.
     */
    public void cadastrar(String email, String senha, String nome, String cpf, String sexo,
                          String dataNasc, String cidade, String estado,
                          String tipoSanguineo, String perfil) {

        // Cria o objeto Usuario para salvar no Firestore (ainda sem o UID)
        this.usuarioPendente = new Usuario(
                null, nome, email, cpf, sexo, dataNasc, cidade, estado,
                tipoSanguineo, perfil, 0, null
        );

        // Inicia o Passo 1: Criação no Firebase Auth
        authRepository.cadastrar(email, senha);
    }

    // ... (restante dos métodos: login, logout, getters...)
    // ...

    public void login(String email, String senha) {
        authRepository.login(email, senha);
    }

    public void logout() {
        authRepository.logout();
    }

    public LiveData<FirebaseUser> getUsuarioLogadoLiveData() {
        return usuarioLogadoLiveData;
    }

    public LiveData<String> getErroAutenticacaoLiveData() {
        return erroAutenticacaoLiveData;
    }

    public LiveData<Boolean> getCadastroSucessoLiveData() {
        // A View não precisa mais observar este, mas não tem problema
        return cadastroSucessoLiveData;
    }

    public LiveData<Boolean> getSalvamentoUsuarioSucessoLiveData() {
        return salvamentoUsuarioSucessoLiveData;
    }
}