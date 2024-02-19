package com.example.finalmobileteste;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserProfileActivity extends AppCompatActivity {

    private RadioGroup radioGroupSexo;
    private EditText editTextPeso, editTextAltura, editTextDataNascimento;
    private Button buttonSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Inicialize as Views
        radioGroupSexo = findViewById(R.id.radioGroupSexo);
        editTextPeso = findViewById(R.id.editTextPeso);
        editTextAltura = findViewById(R.id.editTextAltura);
        editTextDataNascimento = findViewById(R.id.editTextDataNascimento);
        buttonSalvar = findViewById(R.id.buttonSalvar);

        // Carregue os dados salvos, se existirem
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int sexoSelecionado = sharedPreferences.getInt("sexo", -1);
        String pesoSalvo = sharedPreferences.getString("peso", "");
        String alturaSalva = sharedPreferences.getString("altura", "");
        String dataNascimentoSalva = sharedPreferences.getString("data_nascimento", "");

        // Preencha os campos com os dados carregados
        if (sexoSelecionado != -1) {
            RadioButton radioButton = findViewById(sexoSelecionado);
            radioButton.setChecked(true);
        }
        editTextPeso.setText(pesoSalvo);
        editTextAltura.setText(alturaSalva);
        editTextDataNascimento.setText(dataNascimentoSalva);

        // Configure um ouvinte de clique para o botão "Salvar"
        buttonSalvar.setOnClickListener(view -> {
            // Obtenha o ID do RadioButton selecionado
            int idSexoSelecionado = radioGroupSexo.getCheckedRadioButtonId();

            // Obtenha os valores dos outros campos
            String peso = editTextPeso.getText().toString();
            String altura = editTextAltura.getText().toString();
            String dataNascimento = editTextDataNascimento.getText().toString();

            // Salve os dados usando SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("sexo", idSexoSelecionado);
            editor.putString("peso", peso);
            editor.putString("altura", altura);
            editor.putString("data_nascimento", dataNascimento);
            editor.apply();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_user_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else if (item.getItemId() == R.id.bottom_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            } else if (item.getItemId() == R.id.bottom_map) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            } else if (item.getItemId() == R.id.bottom_about) {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            } else if (item.getItemId() == R.id.bottom_user_profile) {
                return true;
            }

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
            return true;
        });
    }
}