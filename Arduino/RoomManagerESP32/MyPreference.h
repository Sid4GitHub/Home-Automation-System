#include <Wire.h>
#include <Arduino.h>
#include "StructType.h"
#include "Constant.h"
#ifndef MyPreference_H
  #define MyPreference_H

  #define ALARM_START_ADDRESS 0
  
  #define WIFI_SSID_ADDRESS 8
  #define WIFI_PASSWORD_ADDRESS 60
  
  #define FIREBASE_URL_ADDRESS 1200
  #define FIREBASE_WEB_KEY_ADDRESS 3500

  #define FIREBASE_EMAIL_ID_ADDRESS 450
  #define FIREBASE_EMAIL_PASSWORD_ADDRESS 750
  
  class MyPreference{
  
    int deviceAddress;
  
    public:
      MyPreference(int i2cRomAddress);
      void begin();
      void getAlarm(uint32_t* alarmDetails);
      void setAlarm(uint32_t alarmDetails);
      void alarmOff();
      bool isAlarmSet();
      
      void setWifiSSID(const char* ssid);
      String getWifiSSID();
      
      void setWifiPassword(const char* password);
      String getWifiPassword();
      
      void setFirebaseUrl(const char* url);
      String getFirebaseUrl();

      void setFirebaseWebKey(const char* key);
      String getFirebaseWebKey();

      void setFirebaseLoginEmailId(const char* emailId);
      String getFirebaseLoginEmailId();

      void setFirebaseLoginEmailPassword(const char* password);
      String getFirebaseLoginEmailPassword();
      
    private:
      void setUint16_t(uint16_t num, uint16_t eeaddress);
      uint16_t getUint16_t(uint16_t eeaddress);
      void setByte(uint16_t eeaddress, byte byt);
      byte getByte(uint16_t eeaddress);
      void setString(uint16_t eeaddress, const char* string);
      char* getString(uint16_t eeaddress,  uint16_t len);
      void writeEEPROM(int eeaddress, byte* data, int dataLen);
      byte* readEEPROM(int eeaddress, int dataLen);
  };
  
#endif
