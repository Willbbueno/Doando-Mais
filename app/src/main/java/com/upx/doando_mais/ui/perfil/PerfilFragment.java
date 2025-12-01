package com.upx.doando_mais.ui.perfil;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.upx.doando_mais.R;
import com.upx.doando_mais.data.model.Usuario;
import com.upx.doando_mais.databinding.FragmentPerfilBinding;
import com.upx.doando_mais.ui.auth.AuthViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private AuthViewModel authViewModel;
    private NavController navController;

    private final ActivityResultLauncher<String> getContentLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    Toast.makeText(getContext(), "Fazendo upload da foto...", Toast.LENGTH_SHORT).show();
                    Glide.with(this)
                            .load(uri)
                            .circleCrop()
                            .into(binding.ivPerfilFoto);
                    authViewModel.uploadFotoPerfil(uri);
                }
            });


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        configurarObservadores();
        configurarCliques();
    }

    private void configurarObservadores() {
        authViewModel.getDadosUsuarioLiveData().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                preencherDadosDoUsuario(usuario);
            }
        });

        authViewModel.getUsuarioLogadoLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser == null) {
                // Logout
                navController.navigate(R.id.loginFragment, null,
                        new androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.nav_graph, true)
                                .build()
                );
            } else {
                // Logado
                preencherDataCriacao(firebaseUser);
            }
        });

        authViewModel.getErroLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                Toast.makeText(getContext(), "Erro: " + erro, Toast.LENGTH_LONG).show();
                // CORREÇÃO: Pede ao ViewModel para limpar o erro
                authViewModel.limparErro();
            }
        });
    }

    private void configurarCliques() {
        binding.btnSair.setOnClickListener(v -> {
            authViewModel.logout();
        });

        // ⬇️ LÓGICA ADICIONADA AQUI ⬇️
        binding.btnHistoricoDoacoes.setOnClickListener(v -> {
            // Certifique-se de ter criado o fragment com id 'minhasDoacoesFragment' no nav_graph.xml
            navController.navigate(R.id.minhasDoacoesFragment);
        });

        binding.btnAtualizarCadastro.setOnClickListener(v -> {
            navController.navigate(R.id.action_perfilFragment_to_atualizarCadastroFragment);
        });

        binding.btnAlterarSenha.setOnClickListener(v -> {
            navController.navigate(R.id.action_perfilFragment_to_alterarSenhaFragment);
        });

        binding.btnSobreApp.setOnClickListener(v -> {
            navController.navigate(R.id.action_perfilFragment_to_sobreAppFragment);
        });

        binding.btnExcluirConta.setOnClickListener(v -> {
            navController.navigate(R.id.action_perfilFragment_to_excluirContaFragment);
        });

        binding.fabEditarFoto.setOnClickListener(v -> {
            getContentLauncher.launch("image/*");
        });
    }

    private void preencherDadosDoUsuario(Usuario usuario) {
        binding.tvPerfilNome.setText(usuario.getNomeCompleto());
        binding.tvPerfilEmail.setText(usuario.getEmail());

        binding.tvPerfilTelefone.setText(usuario.getTelefone() != null ? usuario.getTelefone() : "Não informado");
        binding.tvPerfilCpf.setText(usuario.getCpf() != null ? usuario.getCpf() : "Não informado");
        binding.tvPerfilDataNasc.setText(usuario.getDataNascimento() != null ? usuario.getDataNascimento() : "Não informado");
        binding.tvPerfilSexo.setText(usuario.getSexo() != null ? usuario.getSexo() : "Não informado");

        String local = (usuario.getCidade() != null ? usuario.getCidade() : "") +
                (usuario.getEstado() != null ? " - " + usuario.getEstado() : "");
        binding.tvPerfilLocalizacao.setText(local.isEmpty() ? "Não informado" : local);

        // Lógica do Glide para carregar a foto
        if (usuario.getUrlFotoPerfil() != null && !usuario.getUrlFotoPerfil().isEmpty()) {
            Glide.with(this)
                    .load(usuario.getUrlFotoPerfil())
                    .circleCrop()
                    .placeholder(R.drawable.ic_perfil_placeholder)
                    .error(R.drawable.ic_perfil_placeholder)
                    .into(binding.ivPerfilFoto);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_perfil_placeholder)
                    .circleCrop()
                    .into(binding.ivPerfilFoto);
        }
    }

    private void preencherDataCriacao(FirebaseUser firebaseUser) {
        if (firebaseUser.getMetadata() != null) {
            long timestamp = firebaseUser.getMetadata().getCreationTimestamp();
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM, yyyy", new Locale("pt", "BR"));
            binding.tvPerfilMembroDesde.setText(sdf.format(new java.util.Date(timestamp)));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}