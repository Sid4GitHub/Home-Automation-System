#include "MyPreference.h"

MyPreference::MyPreference(int i2cRomAddress){
  deviceAddress = i2cRomAddress;
}

void MyPreference::begin(){
  Wire.begin();
  
}

void MyPreference::getAlarm(uint32_t* alarmDetails){ 
  byte* data = readEEPROM(ALARM_START_ADDRESS, 5);  
  alarmDetails[0] = (uint32_t) data[0];
  alarmDetails[1] = (uint32_t)((uint32_t) (data[1] << 24) | ((uint32_t)(data[2] << 16)) | ((uint32_t)(data[3] << 8)) | ((uint32_t)(data[4])));
  vPortFree(data);  
}

void MyPreference::setAlarm(uint32_t alarmTime){
  byte* data = (byte *) pvPortMalloc(5 * sizeof(byte));
  vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
  data[0] = 1;
  data[1] = (alarmTime >> 24) & 0xFF;
  data[2] = (alarmTime >> 16) & 0xFF;
  data[3] = (alarmTime >> 8) & 0xFF;
  data[4] = alarmTime & 0xFF;
  writeEEPROM(ALARM_START_ADDRESS, data, 5);
  vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
  vPortFree(data);
}

bool MyPreference::isAlarmSet(){
  return getByte(ALARM_START_ADDRESS) == 0 ? false : true;
}

void MyPreference::alarmOff(){
  byte* data =  (byte *) pvPortMalloc( sizeof(byte));
  vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
  data[0] = 0;
  writeEEPROM(ALARM_START_ADDRESS, data, 1);
  vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
  vPortFree(data);
}

void MyPreference::setWifiSSID(const char* ssid){
  setString(WIFI_SSID_ADDRESS, ssid);
}


String MyPreference::getWifiSSID(){
  uint16_t len = getUint16_t(WIFI_SSID_ADDRESS); 
  char *data = getString(WIFI_SSID_ADDRESS + 2, len);
  String res = String(data);
  vPortFree(data);
  return res;
}

void MyPreference::setWifiPassword(const char* password){
  setString(WIFI_PASSWORD_ADDRESS, password);
}


String MyPreference::getWifiPassword(){
  uint16_t len = getUint16_t(WIFI_PASSWORD_ADDRESS);
  char *data = getString(WIFI_PASSWORD_ADDRESS + 2, len);
  String res = String(data);
  vPortFree(data);
  return res;
}

void MyPreference::setFirebaseUrl(const char* url){
  setString(FIREBASE_URL_ADDRESS, url);
}
String MyPreference::getFirebaseUrl(){
  uint16_t len = getUint16_t(FIREBASE_URL_ADDRESS);
  char *data = getString(FIREBASE_URL_ADDRESS + 2, len);
  String res = String(data);
  vPortFree(data);
  return res;
}

void MyPreference::setFirebaseWebKey(const char* key){
  setString(FIREBASE_WEB_KEY_ADDRESS, key);
}

String MyPreference::getFirebaseWebKey(){
  uint16_t len = getUint16_t(FIREBASE_WEB_KEY_ADDRESS);
  char *data = getString(FIREBASE_WEB_KEY_ADDRESS + 2, len);
  String res = String(data);
  vPortFree(data);
  return res;
}

void MyPreference::setFirebaseLoginEmailId(const char* emailId){
  setString(FIREBASE_EMAIL_ID_ADDRESS, emailId);
}
String MyPreference::getFirebaseLoginEmailId(){
  uint16_t len = getUint16_t(FIREBASE_EMAIL_ID_ADDRESS);
  char *data = getString(FIREBASE_EMAIL_ID_ADDRESS + 2, len);
  String res = String(data);
  vPortFree(data);
  return res;
}

void MyPreference::MyPreference::setFirebaseLoginEmailPassword(const char* password){
  setString(FIREBASE_EMAIL_PASSWORD_ADDRESS, password);
}

String MyPreference::getFirebaseLoginEmailPassword(){
  uint16_t len = getUint16_t(FIREBASE_EMAIL_PASSWORD_ADDRESS);
  char *data = getString(FIREBASE_EMAIL_PASSWORD_ADDRESS + 2, len);
  String res = String(data);
  vPortFree(data);
  return res;
}

void MyPreference::setByte(uint16_t eeaddress, byte byt){
  byte* byteArr = (byte *) pvPortMalloc(sizeof(byte));
  vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
  byteArr[0] = byt;
  writeEEPROM(eeaddress, byteArr, 1);
  vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
  vPortFree(byteArr);
}

byte MyPreference::getByte(uint16_t eeaddress){
  byte* byteArr = readEEPROM(eeaddress, 1);
  byte res = byteArr[0];
  vPortFree(byteArr);
  return res;
}

void MyPreference::setString(uint16_t eeaddress, const char* string){
  uint16_t strLen = strlen(string) + 1;
  setUint16_t(eeaddress, strLen);
  eeaddress += 2;
  for(int i = 0; i < strLen; i++){
    setByte(eeaddress++, string[i]);
  }
}

char* MyPreference::getString(uint16_t eeaddress, uint16_t len){
  return (char*) readEEPROM(eeaddress, len);
}


//Little endian format
void MyPreference::setUint16_t(uint16_t eeaddress, uint16_t num){
  byte* byteArr = (byte *) pvPortMalloc(2 * sizeof(byte));
  vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
  byteArr[0] = (uint8_t)(num >> 8);
  byteArr[1] = (uint8_t)(num & 0xFF);
  writeEEPROM(eeaddress, byteArr, 2);
  vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
  vPortFree(byteArr);
}

uint16_t MyPreference::getUint16_t(uint16_t eeaddress){
  byte* byteArr = readEEPROM(eeaddress, 2);
  uint16_t res = (byteArr[0] << 8) | byteArr[1];
  vPortFree(byteArr); 
  return res;
}



void MyPreference::writeEEPROM(int eeaddress, byte* data, int dataLen) {
  Wire.beginTransmission(deviceAddress);
  Wire.write((uint8_t)(eeaddress >> 8));   // MSB
  Wire.write((uint8_t)(eeaddress & 0xFF)); // LSB
  Wire.write(data, dataLen);
  Wire.endTransmission(); 
}
   
byte* MyPreference::readEEPROM(int eeaddress, int dataLen) { 
    byte* rdata = (byte *) pvPortMalloc(dataLen * sizeof(byte));
    Wire.beginTransmission(deviceAddress);
    Wire.write((int)(eeaddress >> 8));   // MSB
    Wire.write((int)(eeaddress & 0xFF)); // LSB
    Wire.endTransmission();
    Wire.requestFrom(deviceAddress, dataLen); 
    int i = 0;
    while(Wire.available()){
      rdata[i++] = Wire.read(); 
    } 
  
  return rdata;
}
  
