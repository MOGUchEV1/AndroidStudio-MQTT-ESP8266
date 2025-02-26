package com.example.test17022025;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Led2Activity extends AppCompatActivity {

    private Button btnD7, btnD2;
    private MqttClient mqttClient;
    private boolean isD7On = false, isD2On = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led2);

        btnD7 = findViewById(R.id.btnD7);
        btnD2 = findViewById(R.id.btnD2);
        setupMqtt();

        // Кнопка для D7 (топик led/control3)
        btnD7.setOnClickListener(v -> {
            isD7On = !isD7On;
            publish("led/control3", isD7On);
            btnD7.setText(isD7On ? "Выключить синий свет" : "Включить синий свет");
        });

        // Кнопка для D2 (топик led/control4)
        btnD2.setOnClickListener(v -> {
            isD2On = !isD2On;
            publish("led/control4", isD2On);
            btnD2.setText(isD2On ? "Выключить зелёный свет" : "Включить зелёный свет");
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