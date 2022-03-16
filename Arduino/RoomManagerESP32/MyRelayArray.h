#include <Arduino.h>
#include "Constant.h"
#include "StructType.h"

#ifndef MyRelayArray_H
  
  #define MyRelayArray_H

  #define RELAY_SIZE 8
  #define RELAY_STATE_CHANGE_TO_OFF 0
  #define RELAY_STATE_CHANGE_TO_ON 1
  #define RELAY_STATE_CHANGE_TO_TOGGLE 2

  class MyRelayArray{
    uint8_t* relayPins = NULL;  
    bool* relayState = NULL;
    uint32_t* relayStateChangeSchedule = NULL;
    uint8_t* relayStateChangeTo = NULL;
    bool isPowerOffVar;
    
    public:
      void begin();
      bool setRelayState(uint8_t i, uint8_t state);
      bool getRelayState(uint8_t i);
      bool toggleRelayState(uint8_t i);
      void scheduleRelayStateChange(uint8_t i, uint32_t scheduleAt, uint8_t changeTo);
      void resetScheduleForRelayStateChange(uint8_t i);
      RelaySchedule* getRelaySchedule(uint8_t relayIdx);
      void relayScheduleLookUp(uint32_t curTime);
      void powerOnOffToggle();
      bool isPowerOff();
  };

#endif
