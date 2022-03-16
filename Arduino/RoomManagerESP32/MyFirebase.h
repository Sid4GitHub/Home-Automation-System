#include <FreeRTOS.h>
#include <Arduino.h>
#include <Firebase_ESP_Client.h>
#include "StructType.h"
#include "Util.h"
#include "Constant.h"
#include "Logger.h"

#ifndef MyFirebase_H

  #define MyFirebase_H
  
  class MyFirebase{
    QueueHandle_t* readQueue = NULL;
    FirebaseData fbdo;
    FirebaseData fbdoWrite;
    FirebaseJson json;
    uint32_t lastCmdReceived;
    QueueHandle_t* firebaseWriteQueue = NULL;  
    
    public:
    void begin(QueueHandle_t* readQueue_, QueueHandle_t* firebaseWriteQueue_, uint32_t lastCmdReceived_, FirebaseConfig* config, FirebaseAuth* auth);
    bool firebaseReadEvent();
    bool firebaseWriteEventQueueHandler();
    bool writeCommandStatus(bool status, uint32_t time);
    bool writeString(String str, uint32_t time);
  };
  
#endif
