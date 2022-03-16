#include <ezTime.h>
#include <RTClib.h>
#include <WiFi.h>
#include "StructType.h"
#include "Util.h"
#include "Constant.h"

#ifndef MyClock_H

  #define MyClock_H
  
  #define CLOCK_WIRE_TRANSACTION_DELAY 1

  class MyClock{
  	time_t time;
  	RTC_DS3231 rtc;
  	uint32_t lastNtpSync = 0;
  	#define ntpSyncInterval 900000 //15 * 60 * 1000;
  	#define ntpSyncRetryInterval 300000 //5 * 60 * 1000;
  	bool firstNTPSyncDone = false;
    uint32_t alarmTime = UINT_32_T_MAX_VALUE;
    
    private:
    time_t utcToIst(time_t t) __attribute__((always_inline));
    
    public:
    void begin(){
      pinMode(ALARM_BUZZER_PIN, OUTPUT);
      digitalWrite(ALARM_BUZZER_PIN, LOW);
      rtc.begin();  
      syncWithNtp();
    }
  
    public:
    bool syncWithNtp(){
      if(WiFi.status() == WL_CONNECTED && (!firstNTPSyncDone || ( millis() - lastNtpSync > ntpSyncInterval || (ezt::timeStatus() != 2 && millis() - lastNtpSync > ntpSyncRetryInterval)))){      
        ezt::updateNTP();
        if(ezt::timeStatus() == 2){
            rtc.adjust(ezt::now());
            lastNtpSync = millis();            
            return firstNTPSyncDone = true;
        } 
      }else if(WiFi.status() != WL_CONNECTED && (millis() - lastNtpSync > ntpSyncInterval)){
        ezt::setTime(rtc.now().unixtime()); 
        lastNtpSync = millis();     
      }
      
      if(ezt::timeStatus() == 0){
        ezt::setTime(rtc.now().unixtime()); 
      }
      
      return false;
    }
    
    public:
    void setAlarm(uint32_t time){
      alarmTime = time;
    }

    void resetAlarm(){
      digitalWrite(ALARM_BUZZER_PIN, LOW);
      setAlarm(UINT_32_T_MAX_VALUE);
    }
    
    bool alarmLookUpService(){
      if(alarmTime <= now()){
        digitalWrite(ALARM_BUZZER_PIN, HIGH);
        return true;
      }
      return false;
    }

    bool isAlarmOn(){
        return alarmTime <= now();
    }

    uint32_t getAlarmSetTime(){
      return alarmTime; 
    }
    
    String dateTimeStr(){
      String timeStr = ezt::dateTime(utcToIst(now()));
      uint8_t len = timeStr.length();
      timeStr.setCharAt(len - 3, 'I');
      timeStr.setCharAt(len - 2, 'S');
      timeStr.setCharAt(len - 1, 'T');     
      return timeStr;
    }   

    public:
    void syncRTCTime(uint32_t t){
      rtc.adjust(t);
      ezt::setTime(t);
    }
    
    public:
    time_t now(){
      return time = ezt::now();
    }
    
  };
  
  time_t MyClock::utcToIst(time_t t){
      return t + (time_t) 19800; //(5 * 60 + 30) * 60;
  }

#endif
