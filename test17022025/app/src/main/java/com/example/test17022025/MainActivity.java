package com.example.test17022025;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText passwordInput;
    private Button loginButton, selectLedButton;
    private Spinner ledSpinner;
    private TextView welcomeText, selectZoneText;
    private int selectedLed = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов интерфейса
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        ledSpinner = findViewById(R.id.ledSpinner);
        selectLedButton = findViewById(R.id.selectLedButton);
        welcomeText = findViewById(R.id.welcomeText);
        selectZoneText = findViewById(R.id.selectZoneText);

        // Настройка видимости элементов
        ledSpinner.setVisibility(View.GONE);
        selectLedButton.setVisibility(View.GONE);
        selectZoneText.setVisibility(View.GONE);

        // Обработка входа
        loginButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString();
            if (password.equals("12345")) {
                // Скрываем приветственный текст и поле ввода
                welcomeText.setVisibility(View.GONE);
                passwordInput.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);

                // Показываем элементы выбора светодиода
                selectZoneText.setVisibility(View.VISIBLE);
                ledSpinner.setVisibility(View.VISIBLE);
                selectLedButton.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Пароль верный!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Неверный пароль!", Toast.LENGTH_SHORT).show();
            }
        });

        // Обработка выбора светодиода в списке
        ledSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLed = position + 1; // 1, 2 или 3
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Обработка кнопки выбора светодиода
        selectLedButton.setOnClickListener(v -> {
            Intent intent;
            switch (selectedLed) {
                case 1:
                    intent = new Intent(MainActivity.this, Led1Activity.class);
                    break;
                case 2:
                    intent = new Intent(MainActivity.this, Led2Activity.class);
                    break;
                case 3:
                    intent = new Intent(MainActivity.this, Led3Activity.class);
                    break;
                default:
                    return;
            }
            startActivity(intent);
        });
    }
}