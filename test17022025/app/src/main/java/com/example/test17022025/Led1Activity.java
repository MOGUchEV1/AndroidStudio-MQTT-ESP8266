package com.example.test17022025;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Led1Activity extends AppCompatActivity {

    private Button btnD8, btnD5;
    private MqttClient mqttClient;
    private boolean isD8On = false, isD5On = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led1);

        btnD8 = findViewById(R.id.btnD8);
        btnD5 = findViewById(R.id.btnD5);
        setupMqtt();

        // Кнопка для D8 (топик led/control1)
        btnD8.setOnClickListener(v -> {
            isD8On = !isD8On;
            publish("led/control1", isD8On);
            btnD8.setText(isD8On ? "Выключить синий свет" : "Включить синий свет");
        });

        // Кнопка для D5 (топик led/control2)
        btnD5.setOnClickListener(v -> {
            isD5On = !isD5On;
            publish("led/control2", isD5On);
            btnD5.setText(isD5On ? "Выключить зелёный свет" : "Включить зелёный свет");
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