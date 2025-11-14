package com.upx.doando_mais.ui.campanha_criar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.upx.doando_mais.R; // Importe o R para as ações de navegação
import com.upx.doando_mais.databinding.FragmentCriarCampanhaBinding;

/**
 * Esta tela agora funciona como um "hub" de seleção,
 * permitindo ao usuário escolher que tipo de campanha criar.
 */
public class CriarCampanhaFragment extends Fragment {

    private FragmentCriarCampanhaBinding binding;
    private NavController navController;
    // Removemos os ViewModels, eles não são mais necessários AQUI.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCriarCampanhaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializa o NavController
        navController = Navigation.findNavController(view);

        // 2. Configura os Listeners de clique para os cards
        configurarListenersDeClique();

        // (Toda a lógica antiga de 'configurarObservadores', 'tentarSalvarCampanha',
        // 'limparFormulario' e 'mostrarCarregando' foi REMOVIDA daqui)
    }

    private void configurarListenersDeClique() {
        // Clique no Card de Paciente
        binding.cardCriarPaciente.setOnClickListener(v -> {
            // Navega para o formulário de Paciente
            navController.navigate(R.id.action_criarCampanhaFragment_to_formPacienteFragment);
        });

        // Clique no Card de Campanha Pública
        binding.cardCriarPublica.setOnClickListener(v -> {
            // Navega para o formulário de Pública (usando o nome do seu arquivo 'FormAbertaFragment')
            navController.navigate(R.id.action_criarCampanhaFragment_to_formAbertaFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}