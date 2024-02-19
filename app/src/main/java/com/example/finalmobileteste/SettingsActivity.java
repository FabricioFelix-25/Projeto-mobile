package com.example.finalmobileteste;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup radioGroupExercicio, radioGroupVelocidade, radioGroupMapa, radioGroupTipoMapa;
    private Button buttonSalvarConfiguracoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_settings);

        // Inicialize as Views
        radioGroupExercicio = findViewById(R.id.radioGroupExercicio);
        radioGroupVelocidade = findViewById(R.id.radioGroupVelocidade);
        radioGroupMapa = findViewById(R.id.radioGroupMapa);
        radioGroupTipoMapa = findViewById(R.id.radioGroupTipoMapa);
        buttonSalvarConfiguracoes = findViewById(R.id.buttonSalvarConfiguracoes);

        // Carregue as configurações salvas, se existirem
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        int exercicioSelecionado = sharedPreferences.getInt("exercicio", -1);
        int velocidadeSelecionada = sharedPreferences.getInt("velocidade", -1);
        int mapaSelecionado = sharedPreferences.getInt("mapa", -1);
        int tipoMapaSelecionado = sharedPreferences.getInt("tipo_mapa", -1);

        // Preencha os campos com as configurações carregadas
        if (exercicioSelecionado != -1) {
            RadioButton radioButtonExercicio = findViewById(exercicioSelecionado);
            radioButtonExercicio.setChecked(true);
        }

        if (velocidadeSelecionada != -1) {
            RadioButton radioButtonVelocidade = findViewById(velocidadeSelecionada);
            radioButtonVelocidade.setChecked(true);
        }

        if (mapaSelecionado != -1) {
            RadioButton radioButtonMapa = findViewById(mapaSelecionado);
            radioButtonMapa.setChecked(true);
        }

        if (tipoMapaSelecionado != -1) {
            RadioButton radioButtonTipoMapa = findViewById(tipoMapaSelecionado);
            radioButtonTipoMapa.setChecked(true);
        }

        buttonSalvarConfiguracoes.setOnClickListener(view -> {
            // Obtenha o ID dos RadioGroups selecionados
            int idExercicioSelecionado = radioGroupExercicio.getCheckedRadioButtonId();
            int idVelocidadeSelecionada = radioGroupVelocidade.getCheckedRadioButtonId();
            int idMapaSelecionado = radioGroupMapa.getCheckedRadioButtonId();
            int idTipoMapaSelecionado = radioGroupTipoMapa.getCheckedRadioButtonId();

            // Salve as configurações usando SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("exercicio", idExercicioSelecionado);
            editor.putInt("velocidade", idVelocidadeSelecionada);
            editor.putInt("mapa", idMapaSelecionado);
            editor.putInt("tipo_mapa", idTipoMapaSelecionado);
            editor.apply();


            RadioButton selectedVelocidadeRadioButton = findViewById(idVelocidadeSelecionada);
            int selectedVelocidadeId = selectedVelocidadeRadioButton.getId();


            RadioButton selectedTipoMapaRadioButton = findViewById(idTipoMapaSelecionado);
            int selectedTipoMapaId = selectedTipoMapaRadioButton.getId();

            RadioButton selectedMapaRadioButton = findViewById(idMapaSelecionado);
            int selectedMapaId = selectedMapaRadioButton.getId();

            Intent intent = new Intent(SettingsActivity.this, MapsActivity.class);
            intent.putExtra("velocidade_id", selectedVelocidadeId);
            intent.putExtra("tipo_mapa_id", selectedTipoMapaId);
            intent.putExtra("mapa_id", selectedMapaId);

            startActivity(intent);
        });


        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else if (item.getItemId() == R.id.bottom_settings) {
                return true;
            } else if (item.getItemId() == R.id.bottom_map) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            } else if (item.getItemId() == R.id.bottom_about) {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            } else if (item.getItemId() == R.id.bottom_user_profile) {
                startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
            }

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
            return true;
        });
    }
}