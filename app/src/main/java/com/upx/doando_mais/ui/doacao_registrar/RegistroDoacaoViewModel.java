package com.upx.doando_mais.ui.doacao_registrar;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.upx.doando_mais.data.model.Doacao;
import com.upx.doando_mais.repository.DoacaoRepository;

public class RegistroDoacaoViewModel extends AndroidViewModel {

    private DoacaoRepository repository;
    private MutableLiveData<Boolean> sucessoLiveData;
    private MutableLiveData<String> erroLiveData;
    private MutableLiveData<Boolean> carregandoLiveData;

    public RegistroDoacaoViewModel(@NonNull Application application) {
        super(application);
        repository = new DoacaoRepository();

        this.sucessoLiveData = repository.getSucessoRegistroLiveData();
        this.erroLiveData = repository.getErroLiveData();
        this.carregandoLiveData = new MutableLiveData<>(false);
    }

    public void registrarDoacao(String local, String data, String tipoSanguineo, Uri uriComprovante) {

        if (TextUtils.isEmpty(local)) {
            erroLiveData.setValue("Por favor, informe o local da doação.");
            return;
        }
        if (TextUtils.isEmpty(data)) {
            erroLiveData.setValue("Por favor, informe a data da doação.");
            return;
        }
        if (TextUtils.isEmpty(tipoSanguineo)) {
            erroLiveData.setValue("Selecione o tipo sanguíneo doado.");
            return;
        }


        // 2. Pega o usuário atual (para vincular a doação a ele)
        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uidUsuario == null) {
            erroLiveData.setValue("Erro: Usuário não está logado.");
            return;
        }

        // 3. Cria o objeto
        carregandoLiveData.setValue(true); // Mostra loading

        Doacao novaDoacao = new Doacao();
        novaDoacao.setIdUsuario(uidUsuario);
        novaDoacao.setLocal(local);
        novaDoacao.setDataDoacao(data);
        novaDoacao.setTipoSanguineo(tipoSanguineo);

        // 4. Manda salvar
        repository.registrarDoacao(novaDoacao, uriComprovante);
    }

    public LiveData<Boolean> getSucessoLiveData() {
        return sucessoLiveData;
    }

    public LiveData<String> getErroLiveData() {
        return erroLiveData;
    }

    public LiveData<Boolean> getCarregandoLiveData() {
        return carregandoLiveData;
    }
}