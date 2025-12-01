package com.upx.doando_mais.ui.perfil;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.upx.doando_mais.data.model.Doacao;
import com.upx.doando_mais.repository.DoacaoRepository;
import java.util.List;

public class MinhasDoacoesViewModel extends AndroidViewModel {
    private DoacaoRepository repository;
    public MinhasDoacoesViewModel(@NonNull Application app) {
        super(app);
        repository = new DoacaoRepository();
    }
    public void carregarDoacoes() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid != null) repository.carregarDoacoes(uid);
    }
    public LiveData<List<Doacao>> getDoacoesLiveData() {
        return repository.getListaDoacoesLiveData();
    }
}