#include "MyRelayArray.h"

void MyRelayArray::begin(){
  isPowerOffVar = false;
  relayPins = (uint8_t *) pvPortMalloc(RELAY_SIZE * sizeof(uint8_t));
  relayState = (bool *) pvPortMalloc(RELAY_SIZE * sizeof(bool));
  relayStateChangeSchedule = (uint32_t *) pvPortMalloc(RELAY_SIZE * sizeof(uint32_t));
  relayStateChangeTo = (uint8_t *) pvPortMalloc(RELAY_SIZE * sizeof(uint8_t));
  
  relayPins[0] = RELAY_0_PIN; 
  relayPins[1] = RELAY_1_PIN; 
  relayPins[2] = RELAY_2_PIN; 
  relayPins[3] = RELAY_3_PIN; 
  relayPins[4] = RELAY_4_PIN; 
  relayPins[5] = RELAY_5_PIN;  
  relayPins[6] = RELAY_6_PIN;  
  relayPins[7] = RELAY_7_PIN;
  
  for(int i = 0; i < RELAY_SIZE; i++){
    relayState[i] = LOW;
    pinMode(relayPins[i], OUTPUT);
    digitalWrite(relayPins[i], relayState[i]);
    relayStateChangeSchedule[i] = UINT_32_T_MAX_VALUE;
    relayStateChangeTo[i] = RELAY_STATE_CHANGE_TO_OFF;
  }
}

bool MyRelayArray::setRelayState(uint8_t i, uint8_t state) {
  if(!isPowerOff()) {
    relayState[i] = (bool) state;
    digitalWrite(relayPins[i], relayState[i]);
    return true;
  }
  return false;
}

bool MyRelayArray::getRelayState(uint8_t i){
  if(isPowerOff()) return false;
  return relayState[i];
}

bool MyRelayArray::toggleRelayState(uint8_t i){
  if(!isPowerOff()) {
    relayState[i] = !relayState[i];
    digitalWrite(relayPins[i], relayState[i]);
    return true;
  }
  return false;
}

void MyRelayArray::scheduleRelayStateChange(uint8_t i, uint32_t scheduleAt, uint8_t changeTo) {
   relayStateChangeSchedule[i] = scheduleAt;
   relayStateChangeTo[i] = changeTo;
}

void MyRelayArray::resetScheduleForRelayStateChange(uint8_t i) {
  relayStateChangeSchedule[i] = UINT_32_T_MAX_VALUE;
}

RelaySchedule* MyRelayArray::getRelaySchedule(uint8_t i){
  RelaySchedule* relaySchedule = (RelaySchedule*) pvPortMalloc(sizeof(RelaySchedule));
  relaySchedule -> currentState = isPowerOff() ? false : relayState[i];
  relaySchedule -> scheduleAt = relayStateChangeSchedule[i];
  relaySchedule -> newState = relayStateChangeTo[i];
  return relaySchedule;
}

void MyRelayArray::relayScheduleLookUp(uint32_t curTime){
  for(int i = 0; i < RELAY_SIZE && !isPowerOff(); i++){
    if(curTime >= relayStateChangeSchedule[i]){
      relayStateChangeSchedule[i] = UINT_32_T_MAX_VALUE;
      if (relayStateChangeTo[i] == RELAY_STATE_CHANGE_TO_OFF){
         setRelayState(i, LOW);
      }else if (relayStateChangeTo[i] == RELAY_STATE_CHANGE_TO_ON){ 
         setRelayState(i, HIGH);
      }else{
        toggleRelayState(i);
      }      
    }
    vTaskDelay(1 / portTICK_PERIOD_MS);    
  }
}

void MyRelayArray::powerOnOffToggle(){
  isPowerOffVar = !isPowerOffVar;
  for(int i = 0; i < RELAY_SIZE; i++){
    if(isPowerOffVar){
      digitalWrite(relayPins[i], LOW);
    }else{
      digitalWrite(relayPins[i], relayState[i]);
    }
  }
}

bool MyRelayArray::isPowerOff(){
  return isPowerOffVar;
}
