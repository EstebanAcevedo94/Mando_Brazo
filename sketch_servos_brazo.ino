#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <Servo.h>
#include <FirebaseESP8266.h>

// Credenciales WiFi
const char* ssid = "SSID-WIFI";
const char* password = "PASSWORD-WIFI";

// Credenciales Firebase
#define API_KEY "API-KEY-FIREBASE"
#define DATABASE_URL "URL-Firebase"
#define USER_EMAIL "EMAIL-USUARIO-REGISTRADO-FIREBASE"
#define USER_PASSWORD "PASSWORD-USUARIO"

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

// Definir pines de servo
const int servoPines[] = {0, 5, 16, 12, 14, 4}; // D3, D1, D0, D6, D5, D2
Servo servos[6];
const int ledPin = 2;

const char* servoNames[] = {"Pinza", "Muneca", "Antebrazo", "Codo", "Hombro", "Base"};
                            // [0] ,   [1]   ,    [2]     ,   [3] ,   [4]   ,  [5]
void setup() {
  Serial.begin(115200);
  delay(10);
  pinMode(ledPin, OUTPUT);

  for (int i = 0; i < 6; i++) {
    servos[i].attach(servoPines[i]);
  }

  connectToWiFi();

  config.database_url = DATABASE_URL;
  config.api_key = API_KEY;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  Firebase.reconnectWiFi(true);
  Firebase.begin(&config, &auth);
}

void loop() {
  if(Firebase.get(fbdo, "/Servo/Manual")){
    if (fbdo.dataType() == "int") {
      int maValue = fbdo.intData();
      Serial.println("Valor de Manual: " + String(maValue));

      switch (maValue) {
        case 1:
          for (const char* servoName : servoNames) {
            readServoValue(servoName);
            }
            delay(500); // Espera 1 segundo antes de volver a verificar
          break;
        default:
          if (Firebase.get(fbdo, "/Servo/VA")) {
            if (fbdo.dataType() == "int") {
              int vaValue = fbdo.intData();
              Serial.println("Valor de VA: " + String(vaValue));
              switch (vaValue) {
                case 1:
                tomarPlastico();
                updateVAValue();// Actualiza el valor de VA a 0
                break;
                case 2:// Acción para VA = 2
                tomarPapel();
                updateVAValue();
                break;
                case 3:
                // Acción para VA = 3
                break;
                case 4:
                // Acción para VA = 4
                break;
                case 5:
                // Acción para VA = 5
                break;
                case 6:
                // Acción para VA = 6
                break;
                default:
                //servos[0].write(170);
                //servos[1].write(170);
                break;
                }
              } else {
                Serial.println("Error: El valor de VA no es un entero.");
                updateVAValue(); // Actualiza el valor de VA a 0
                }
              } else {
                Serial.println("No se pudo obtener el valor de VA desde Firebase.");
              }
              break;
              }
    } else {
      Serial.println("Error: El valor de VA no es un entero.");
      updateVAValue(); // Actualiza el valor de VA a 0
    }
  }

}

void updateVAValue(){
  String path = "/Servo/VA";
  Firebase.setInt(fbdo, path.c_str(), 0);
  Serial.println(" Valor: " + String(0));
}

void tomarPapel() {
  // Pinza aprieta
  servos[0].write(10);
  delay(1000);

  // Codo levantar
  servos[3].writeMicroseconds(1000);
  delay(1000);
  servos[3].writeMicroseconds(1500);
  delay(500);

  // Muñeca al centro
  servos[1].write(90);
  delay(500);

  // Base giro a la derecha
  servos[5].writeMicroseconds(1000);
  delay(600);
  servos[5].writeMicroseconds(1500);
  delay(200);

  // Codo abajo
  servos[3].writeMicroseconds(2000);
  delay(500);
  servos[3].writeMicroseconds(1500);
  delay(500);

  // Pinza abrir
  servos[0].write(170);
  delay(500);

  // Codo arriba
  servos[3].writeMicroseconds(1000);
  delay(500);
  servos[3].writeMicroseconds(1500);
  delay(500);

  // Base giro a la izquierda
  servos[5].writeMicroseconds(2000);
  delay(630);
  servos[5].writeMicroseconds(1500);
  delay(200);

  // Codo abajo
  servos[3].writeMicroseconds(2000);
  delay(600);
  servos[3].writeMicroseconds(1500);
  delay(500);
  // Pinza suelta
  servos[0].write(170);
  delay(1000);
  // Muñeca al arriba
  servos[1].write(170);
  delay(500);
}
void tomarPlastico() {
  // Pinza aprieta
  servos[0].write(10);
  delay(1000);

  // Muñeca abajo
  servos[1].write(90);
  delay(500);

  // Codo levantar
  servos[3].writeMicroseconds(1000);
  delay(1000);
  servos[3].writeMicroseconds(1500);
  delay(500);

  // Muñeca al centro
  servos[1].write(90);
  delay(500);

  // Base giro a la izquierda
  servos[5].writeMicroseconds(2000);
  delay(600);
  servos[5].writeMicroseconds(1500);
  delay(200);

  // Codo abajo
  servos[3].writeMicroseconds(2000);
  delay(500);
  servos[3].writeMicroseconds(1500);
  delay(500);

  // Pinza abrir
  servos[0].write(170);
  delay(500);

  // Codo arriba
  servos[3].writeMicroseconds(1000);
  delay(500);
  servos[3].writeMicroseconds(1500);
  delay(500);

  // Base giro a la derecha
  servos[5].writeMicroseconds(1000);
  delay(630);
  servos[5].writeMicroseconds(1500);
  delay(200);

  // Codo abajo
  servos[3].writeMicroseconds(2000);
  delay(600);
  servos[3].writeMicroseconds(1500);
  delay(500);
  // Pinza suelta
  servos[0].write(170);
  delay(1000);
  // Muñeca al arriba
  servos[1].write(170);
  delay(500);
}
void readServoValue(const char* servoName) {
  String path = "/Servo/" + String(servoName);
  if (Firebase.get(fbdo, path.c_str())) {
    if (fbdo.dataType() == "int") {
      int servoValue = fbdo.intData();
      updateServoValue(servoName, servoValue);
      updateServo(getServoByName(servoName), servoValue);
      Serial.println(String(servoName) + ": " + String(servoValue));
    }
    else {
      Serial.println("Error: El valor de " + String(servoName) + " no es un entero.");
    }
  } else {
    // Si no encuentra el valor, guarda 90 como valor predeterminado en Firebase
    updateServoValue(servoName, 90);
    // Imprime un mensaje
    Serial.println("Se guardó el valor predeterminado de 90 para " + String(servoName));
  }
}
Servo& getServoByName(const char* servoName) {
  for (int i = 0; i < 6; i++) {
    if (strcmp(servoName, servoNames[i]) == 0) {
      return servos[i];
    }
  }
  // Si el nombre del servo no coincide con ninguno, regresa el primer servo por defecto.
  return servos[0];
}
void updateServo(Servo& servo, int value) {
  servo.write(value);
}
void updateServoValue(const char* servoName, int servoValue){
  String path = "/Servo/" + String(servoName);
  Firebase.setInt(fbdo, path.c_str(), servoValue);
  Serial.println(String(servoName) + " Valor: " + String(servoValue));
}

void connectToWiFi() {
  Serial.print("Conectando a ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    digitalWrite(ledPin, LOW);
    delay(100);
    digitalWrite(ledPin, HIGH);
    delay(1000);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("Conectado a la red WiFi");
}
