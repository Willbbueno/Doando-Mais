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
    private LiveData<FirebaseUser> usuarioLogadoLiveData; // Info do Auth (UID, email)
    private LiveData<String> erroAutenticacaoLiveData;
    private LiveData<Boolean> cadastroSucessoLiveData;
    private LiveData<Boolean> salvamentoUsuarioSucessoLiveData;


    private LiveData<Usuario> dadosUsuarioLiveData; // Info do Firestore (Nome, CPF, etc.)

    // Guarda o usuário que está sendo cadastrado
    private Usuario usuarioPendente;

    public AuthViewModel(@NonNull Application application) {
        super(application);

        this.authRepository = new AuthRepository();
        this.userRepository = new UserRepository();

        // Conecta o LiveData da ViewModel com o LiveData do Repositório
        this.usuarioLogadoLiveData = authRepository.getUsuarioLogadoLiveData();
        this.erroAutenticacaoLiveData = authRepository.getErroAutenticacaoLiveData();
        this.salvamentoUsuarioSucessoLiveData = userRepository.getSalvamentoSucessoLiveData();
        // Conecta o LiveData de dados do usuário
        this.dadosUsuarioLiveData = userRepository.getUsuarioLiveData();

        // ORQUESTRAÇÃO DO CADASTRO
        // Ouve o SUCESSO DO AUTH para então SALVAR NO FIRESTORE
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

        // ORQUESTRAÇÃO DE LOGIN / LOGOUT
        // Ouve o status do Firebase Auth (usuárioLogadoLiveData)
        this.usuarioLogadoLiveData.observeForever(firebaseUser -> {
            if (firebaseUser != null) {
                // Usuário LOGOU (ou app iniciou logado)
                // Passo 2 (do Login): Buscar os dados completos do Firestore
                userRepository.buscarUsuario(firebaseUser.getUid());
            } else {
                // Usuário LOGOUT
                // Limpa os dados do usuário que estavam na memória
                userRepository.getUsuarioLiveData().postValue(null);
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

    public void login(String email, String senha) {
        // Inicia o Passo 1 (Login no Auth).
        authRepository.login(email, senha);
    }

    public void logout() {
        // O AuthRepository vai setar o usuarioLogadoLiveData para null.
        // O novo observador no construtor cuidará de limpar os dados do usuário.
        authRepository.logout();
    }

    //GETTERS

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
    /**
     * Expõe os dados completos do usuário (Nome, CPF, etc.) vindos do Firestore.
     * Os Fragments (como CriarCampanha) vão observar este LiveData.
     */
    public LiveData<Usuario> getDadosUsuarioLiveData() {
        return dadosUsuarioLiveData;
    }
}