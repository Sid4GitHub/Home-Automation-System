#include <IRremote.h>

#include "MyIRRemote.h"

void MyIRRemote::begin(QueueHandle_t* readQueue_){    
  readQueue = readQueue_; 
  IrReceiver.begin(IR_RECEIVER_PIN, DISABLE_LED_FEEDBACK);
}  


bool MyIRRemote::remoteCommandRead() {
    bool res = false;
    if (IrReceiver.decode()) {   
      if(IrReceiver.decodedIRData.decodedRawData != 0 && isVaildRemoteCode(IrReceiver.decodedIRData.decodedRawData)){  
        
        ReadQueueElement* const readQueueElement = (ReadQueueElement *) pvPortMalloc(sizeof(ReadQueueElement));
        readQueueElement -> source = Event_Queue_INPUT_SOURCE_IR_REMOTE;      
        readQueueElement -> type = 0;
        readQueueElement -> uint32Number = IrReceiver.decodedIRData.decodedRawData;
        readQueueElement -> rgbLedColPtr = NULL;
        readQueueElement -> str = NULL;
        if(xQueueSend(*readQueue, &readQueueElement , READ_QUEUE_PUSH_WAIT_TIME) == pdPASS){
          res = true;            
        }else{
          Serial.println("Read Event Failed Queue is Full");
        }
      }        
      IrReceiver.resume(); 
    }
  return res;
}

uint32_t MyIRRemote::getRemoteCode(uint8_t i){
  return validRemoteCode[i];
}

bool MyIRRemote::isVaildRemoteCode(uint32_t code){
  for(int i = 0; i < 21; i++){
    if(validRemoteCode[i] == code){
      return true;
    }
  }
  return false;
}
