package com.upx.doando_mais;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Garanta que este R.layout.activity_main está correto
        setContentView(R.layout.activity_main);

        // 2. Garanta que este R.id.bottom_nav_view existe no XML
        bottomNavView = findViewById(R.id.bottom_nav_view);

        // 3. Garanta que este R.id.nav_host_fragment existe no XML
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        // 4. Se o navHostFragment for nulo, o app vai travar.
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // 5. Conectar a Barra
            NavigationUI.setupWithNavController(bottomNavView, navController);

            // 6. Adicionar o Listener para esconder/mostrar
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();
                if (id == R.id.loginFragment || id == R.id.cadastroFragment) {
                    bottomNavView.setVisibility(View.GONE);
                } else {
                    bottomNavView.setVisibility(View.VISIBLE);
                }
            });
        }
        // else {
        //   Se for nulo, algo no seu XML (Passo 1 ou 2) está errado.
        // }
    }
}