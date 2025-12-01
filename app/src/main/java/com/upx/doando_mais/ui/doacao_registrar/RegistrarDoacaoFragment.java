package com.upx.doando_mais.ui.doacao_registrar;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.upx.doando_mais.R;
import com.upx.doando_mais.databinding.FragmentRegistrarDoacaoBinding;

import java.util.Calendar;
import java.util.Locale;

public class RegistrarDoacaoFragment extends Fragment {

    private FragmentRegistrarDoacaoBinding binding;
    private RegistroDoacaoViewModel viewModel;
    private NavController navController;
    private Uri uriComprovanteSelecionado = null;

    // Lançador da Galeria (igual ao do Perfil)
    private final ActivityResultLauncher<String> getContentLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    uriComprovanteSelecionado = uri;

                    // Mostra a prévia da imagem
                    binding.ivPreviewComprovante.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                    binding.ivPreviewComprovante.setImageTintList(null); // Tira a cor cinza do ícone
                    binding.tvToqueParaAnexar.setText("Comprovante selecionado!");

                    Glide.with(this)
                            .load(uri)
                            .into(binding.ivPreviewComprovante);
                }
            });

    public RegistrarDoacaoFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegistrarDoacaoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(this).get(RegistroDoacaoViewModel.class);

        configurarInterface();
        configurarObservadores();
    }

    private void configurarInterface() {
        // 1. Configura o Spinner de Tipo Sanguíneo
        String[] tipos = getResources().getStringArray(R.array.array_tipo_sanguineo);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, tipos);
        binding.spinnerTipoSanguineoDoacao.setAdapter(adapter);

        // 2. Configura o campo de DATA (Abre Calendário)
        binding.etDataDoacao.setOnClickListener(v -> abrirCalendario());
        binding.tilDataDoacao.setEndIconOnClickListener(v -> abrirCalendario());

        // 3. Configura o seletor de FOTO
        binding.cardComprovante.setOnClickListener(v -> {
            getContentLauncher.launch("image/*");
        });

        // 4. Botão SALVAR
        binding.btnSalvarDoacao.setOnClickListener(v -> {
            String local = binding.etLocalDoacao.getText().toString();
            String data = binding.etDataDoacao.getText().toString();
            String tipo = binding.spinnerTipoSanguineoDoacao.getText().toString();

            viewModel.registrarDoacao(local, data, tipo, uriComprovanteSelecionado);
        });
    }

    private void configurarObservadores() {
        // Sucesso
        viewModel.getSucessoLiveData().observe(getViewLifecycleOwner(), sucesso -> {
            if (sucesso) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), "Doação registrada com sucesso! Parabéns!", Toast.LENGTH_LONG).show();
                navController.popBackStack();
            }
        });

        // Erro
        viewModel.getErroLiveData().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) {
                mostrarCarregando(false);
                Toast.makeText(getContext(), erro, Toast.LENGTH_LONG).show();
            }
        });

        // Loading
        viewModel.getCarregandoLiveData().observe(getViewLifecycleOwner(), this::mostrarCarregando);
    }

    private void abrirCalendario() {
        final Calendar c = Calendar.getInstance();
        int ano = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    // Formata a data para DD/MM/AAAA
                    String dataFormatada = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                    binding.etDataDoacao.setText(dataFormatada);
                }, ano, mes, dia);
        datePickerDialog.show();
    }

    private void mostrarCarregando(boolean carregando) {
        if (carregando) {
            binding.progressBarDoacao.setVisibility(View.VISIBLE);
            binding.btnSalvarDoacao.setEnabled(false);
            binding.btnSalvarDoacao.setText("Salvando...");
        } else {
            binding.progressBarDoacao.setVisibility(View.GONE);
            binding.btnSalvarDoacao.setEnabled(true);
            binding.btnSalvarDoacao.setText("Confirmar Doação");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}