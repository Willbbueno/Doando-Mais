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

public class AuthViewModel extends AndroidViewModel {

    // Trazendo os repositórios de autenticação
    private AuthRepository authRepository;
    private UserRepository userRepository;

    // LiveDatas que serão expostos para as Views Fragments
    private LiveData<FirebaseUser> usuarioLogadoLiveData;
    private LiveData<String> erroAutenticacaoLiveData;
    private LiveData<Boolean> cadastroSucessoLiveData;
    private LiveData<Boolean> salvamentoUsuarioSucessoLiveData;

    public AuthViewModel(@NonNull Application application) {
        super(application);

        // Instancia os repositórios
        this.authRepository = new AuthRepository();
        this.userRepository = new UserRepository();

        // Conecta o LiveData da ViewModel com o LiveData do Repositório
        this.usuarioLogadoLiveData = authRepository.getUsuarioLogadoLiveData();
        this.erroAutenticacaoLiveData = authRepository.getErroAutenticacaoLiveData();
        this.cadastroSucessoLiveData = authRepository.getCadastroSucessoLiveData();
        this.salvamentoUsuarioSucessoLiveData = userRepository.getSalvamentoSucessoLiveData();
    }

    // --- Métodos Públicos (Ações que a View pode chamar) ---

    /**
     * Tenta realizar o login de um usuário.
     * O resultado será postado no usuarioLogadoLiveData ou erroAutenticacaoLiveData.
     */
    public void login(String email, String senha) {
        authRepository.login(email, senha);
    }

    /**
     * Tenta cadastrar um novo usuário.
     * Este método orquestra os dois repositórios:
     * 1. Cria o usuário no Firebase Auth (AuthRepository)
     * 2. Se tiver sucesso, salva os dados adicionais no Firestore (UserRepository)
     */
    public void cadastrar(String email, String senha, String nome, String cpf, String sexo, String dataNasc, String cidade, String estado, String tipoSanguineo, String perfil) {

        // 1. Avisa o AuthRepository para criar o usuário
        authRepository.cadastrar(email, senha);

        // 2. Agora, precisamos "ouvir" o resultado.
        // A lógica real de "salvar no Firestore" será disparada
        // de dentro do Fragment/Activity, que estará observando
        // o 'cadastroSucessoLiveData'.

        // Quando o cadastroSucessoLiveData for 'true', a View chamará 
        // o método abaixo para salvar os dados restantes.
    }

    /**
     * Salva os dados adicionais do usuário no Firestore.
     * Isso deve ser chamado APÓS o Firebase Auth ter criado o usuário com sucesso.
     * @param uid O ID do Firebase Auth recém-criado.
     * @param usuario O objeto Usuario com todos os dados do formulário.
     */
    public void salvarDadosAdicionais(String uid, Usuario usuario) {
        userRepository.salvarUsuarioAdicional(uid, usuario);
    }


    public void logout() {
        authRepository.logout();
    }

    // Getters para a View observar o usuario

    public LiveData<FirebaseUser> getUsuarioLogadoLiveData() {
        return usuarioLogadoLiveData;
    }

    public LiveData<String> getErroAutenticacaoLiveData() {
        return erroAutenticacaoLiveData;
    }

    public LiveData<Boolean> getCadastroSucessoLiveData() {
        return cadastroSucessoLiveData;
    }

    public LiveData<Boolean> getSalvamentoUsuarioSucessoLiveData() {
        return salvamentoUsuarioSucessoLiveData;
    }
}