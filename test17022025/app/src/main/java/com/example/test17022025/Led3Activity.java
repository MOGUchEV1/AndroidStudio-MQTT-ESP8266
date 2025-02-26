package com.example.test17022025;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Led3Activity extends AppCompatActivity {

    private Button btnD6, btnD1;
    private MqttClient mqttClient;
    private boolean isD6On = false, isD1On = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led3);

        btnD6 = findViewById(R.id.btnD6);
        btnD1 = findViewById(R.id.btnD1);
        setupMqtt();

        // Кнопка для D6 (топик led/control5)
        btnD6.setOnClickListener(v -> {
            isD6On = !isD6On;
            publish("led/control5", isD6On);
            btnD6.setText(isD6On ? "Выключить синий свет" : "Включить синий свет");
        });

        // Кнопка для D1 (топик led/control6)
        btnD1.setOnClickListener(v -> {
            isD1On = !isD1On;
            publish("led/control6", isD1On);
            btnD1.setText(isD1On ? "Выключить зелёный свет" : "Включить зелёный свет");
        });
    }
    private void setupMqtt() {
        try {
            mqttClient = new MqttClient("tcp://mqtt.eclipseprojects.io:1883",
                    MqttClient.generateClientId(), null);
            mqttClient.connect();
        } catch (MqttException e) {
            Toast.makeText(this, "Ошибка подключения!", Toast.LENGTH_SHORT).show();
        }
    }

    private void publish(String topic, boolean state) {
        try {
            String message = state ? "ON" : "OFF";
            mqttClient.publish(topic, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            Toast.makeText(this, "Ошибка отправки!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}