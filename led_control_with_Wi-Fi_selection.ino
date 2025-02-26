#include <ESP8266WiFi.h>
#include <EEPROM.h>
#include <GyverPortal.h>
#include <PubSubClient.h>

// Структура для хранения настроек WiFi
struct Settings {
  char ssid[32] = "";
  char pass[32] = "";
};

Settings settings;
GyverPortal portal;
WiFiClient espClient;
PubSubClient mqttClient(espClient);

// Константы
const char* AP_SSID = "VFS";          // Имя точки доступа
const char* AP_PASS = "12345678";     // Пароль точки доступа
const char* mqttServer = "mqtt.eclipseprojects.io";
const int mqttPort = 1883;

// Пины и топики
const int leds[] = {D8, D5, D7, D2, D6, D1}; // Порядок пинов
const char* topics[] = {
  "led/control1",  // D8
  "led/control2",  // D5
  "led/control3",  // D7
  "led/control4",  // D2
  "led/control5",  // D6
  "led/control6"   // D1
};

bool wifiConfigured = false;

// Загрузка настроек из EEPROM
void loadSettings() {
  EEPROM.begin(sizeof(Settings));
  EEPROM.get(0, settings);
  EEPROM.end();
}

// Сохранение настроек в EEPROM
void saveSettings() {
  EEPROM.begin(sizeof(Settings));
  EEPROM.put(0, settings);
  EEPROM.commit();
  EEPROM.end();
}

// Подключение к Wi-Fi
bool connectWiFi() {
  Serial.print("Подключение к ");
  Serial.println(settings.ssid);
  
  WiFi.begin(settings.ssid, settings.pass);
  
  int attempts = 0;
  while (WiFi.status() != WL_CONNECTED && attempts < 20) {
    delay(500);
    Serial.print(".");
    attempts++;
  }
  
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\nПодключено!");
    Serial.print("IP адрес: ");
    Serial.println(WiFi.localIP());
    return true;
  }
  Serial.println("\nОшибка подключения!");
  return false;
}

// Веб-интерфейс для настройки
void buildInterface() {
  GP.BUILD_BEGIN();
  GP.THEME(GP_DARK);
  GP.TITLE("Настройка Wi-Fi");
  GP.FORM_BEGIN("/save");
  GP.TEXT("ssid", "SSID", settings.ssid);
  GP.BREAK();
  GP.PASS("pass", "Пароль", settings.pass);
  GP.BREAK();
  GP.SUBMIT("Сохранить");
  GP.FORM_END();
  GP.BUILD_END();
}

// Обработчик действий
void action() {
  if (portal.form("/save")) {
    portal.copyStr("ssid", settings.ssid);
    portal.copyStr("pass", settings.pass);
    saveSettings();
    Serial.println("Настройки сохранены! Перезагрузка...");
    ESP.restart();
  }
}

// Подключение к MQTT
void setupMQTT() {
  mqttClient.setServer(mqttServer, mqttPort);
  mqttClient.setCallback([](char* topic, byte* payload, unsigned int length) {
    String message;
    for (int i = 0; i < length; i++) message += (char)payload[i];
    
    // Ищем совпадение топика
    for (int i = 0; i < 6; i++) {
      if (strcmp(topic, topics[i]) == 0) {
        digitalWrite(leds[i], message == "ON" ? HIGH : LOW);
        Serial.print("Изменен пин ");
        Serial.print(leds[i]);
        Serial.print(": ");
        Serial.println(message);
        break;
      }
    }
  });

  while (!mqttClient.connected()) {
    String clientId = "ESP-" + String(random(0xffff), HEX);
    if (mqttClient.connect(clientId.c_str())) {
      // Подписываемся на все топики
      for (int i = 0; i < 6; i++) {
        mqttClient.subscribe(topics[i]);
      }
      Serial.println("MQTT подключен!");
    } else {
      Serial.print("Ошибка MQTT: ");
      Serial.println(mqttClient.state());
      delay(5000);
    }
  }
}

void setup() {
  Serial.begin(115200);
  
  // Инициализация пинов
  for (int pin : leds) {
    pinMode(pin, OUTPUT);
    digitalWrite(pin, LOW);
  }
  
  // Инициализация памяти
  EEPROM.begin(sizeof(Settings));
  loadSettings();
  
  // Попытка подключения к Wi-Fi
  if (strlen(settings.ssid) > 0 && connectWiFi()) {
    wifiConfigured = true;
    setupMQTT();
  } 
  else {
    // Режим настройки
    WiFi.mode(WIFI_AP);
    WiFi.softAP(AP_SSID, AP_PASS);
    Serial.print("Точка доступа: ");
    Serial.println(WiFi.softAPIP());
    
    portal.attachBuild(buildInterface);
    portal.attach(action);
    portal.start();
  }
}

void loop() {
  if (wifiConfigured) {
    if (!mqttClient.connected()) setupMQTT();
    mqttClient.loop();
  } 
  else {
    portal.tick();
  }
}
