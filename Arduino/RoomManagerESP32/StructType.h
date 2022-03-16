#include<Arduino.h>

#ifndef StructType_H

  #define StructType_H

	typedef struct{
    uint8_t red;
    uint8_t green;
    uint8_t blue;
  } RGBLedCol;
  
  typedef struct{
    char* str;
    uint32_t time;
  } FirebaseWriteCommandStatus;

  typedef struct{
    uint8_t currentState;
    uint32_t scheduleAt;
    uint8_t newState;
  } RelaySchedule;
  
  typedef struct{
    uint8_t source;
    uint8_t type;
    RGBLedCol* rgbLedColPtr;  
    uint32_t uint32Number;
    uint8_t uint8Number;
    uint8_t uint8Number2;
    uint16_t uint16Number;
    const char* str;
  } ReadQueueElement;
    
#endif
