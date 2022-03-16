#include <FreeRTOS.h>
#include <Arduino.h> 
#include "StructType.h"
#include "Util.h"
#include "Logger.h"
#include "Constant.h"

#ifndef MyBluetooth_H
  
  #define MyBluetooth_H
  #define BLUETOOTH_QUEUE_MSG_PUSH_DELAY 5
  
  class MyBluetooth{  
    QueueHandle_t* readQueue = NULL;
    QueueHandle_t* bluetoothWriteQueue = NULL;  
    public:
      void begin(QueueHandle_t* readQueue_, QueueHandle_t* bluetoothWriteQueue_);
      bool bluetoothRead();
      void bluetoothWriteEventQueueHandler();
      bool writeCommandStatus(bool status);
      bool writeString(String str);
  };
  
#endif
