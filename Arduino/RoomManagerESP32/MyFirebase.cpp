
#include "MyFirebase.h"

void MyFirebase::begin(QueueHandle_t* readQueue_, QueueHandle_t* firebaseWriteQueue_, uint32_t lastCmdReceived_, FirebaseConfig* config, FirebaseAuth* auth){
    readQueue = readQueue_;  
    firebaseWriteQueue = firebaseWriteQueue_;
    Firebase.reconnectWiFi(true);
    Firebase.begin(config, auth);
      lastCmdReceived = lastCmdReceived_;
}  


bool MyFirebase::firebaseReadEvent(){
    if (Firebase.RTDB.getString(&fbdo, DEVICE_CMD_SYNC_TS_ADDRESS)) {
      uint32_t remoteTs = strtoull(fbdo.stringData().c_str(), NULL, 10);
      if(remoteTs > lastCmdReceived){
          lastCmdReceived = remoteTs;
          if (Firebase.RTDB.getString(&fbdo, DEVICE_CMD_SYNC_CMD_ADDRESS)) {
            ReadQueueElement* const readQueueElement = buildReadQueueElement(fbdo.stringData()); 
            readQueueElement -> source = Event_Queue_INPUT_SOURCE_FIREBASE;
            if(xQueueSend(*readQueue, &readQueueElement , READ_QUEUE_PUSH_WAIT_TIME) == pdPASS){
                return true;          
            }else{
                LOG("Read Event Queue is Full");
            }
          }
      }
    }
    return false;
}
/*
 * Single poll 
bool MyFirebase::fireBaseReadEvent(){
    if (Firebase.RTDB.getJSON(&fbdo, "/device/device_1/cmdSyn")) {
      FirebaseJson &json = fbdo.to<FirebaseJson>();
      json.get(result, "ts");
      uint32_t remoteTs = result.to<uint32_t>();
      //Serial.println(fbdo.to<String>() +" :: "+ String(remoteTs, DEC) + " :: " +String(lastCmdReceived, DEC)  );
      if(remoteTs > lastCmdReceived){
          lastCmdReceived = remoteTs;
          json.get(result, "cmd");
          ReadQueueElement* const readQueueElement = buildReadQueueElement(result.to<String>());   
          if(xQueueSend(*readQueue, &readQueueElement , READ_QUEUE_PUSH_WAIT_TIME) == pdPASS){
              return true;          
          }else{
              LOG("Read Event Queue is Full");
          }          
      }
    }
    return false;
}
*/

bool MyFirebase::firebaseWriteEventQueueHandler(){
  FirebaseWriteCommandStatus* firebaseWriteCommandStatus;
  xQueueReceive(*firebaseWriteQueue, &firebaseWriteCommandStatus, portMAX_DELAY); 
  
  if(strcmp(firebaseWriteCommandStatus -> str, WRITE_COMMAND_SUCCESS_STATUS) == 0){
    json.set(DEVICE_OUTPUT_RES_ADDRESS, "SUCCESS;"); 
  }else if(strcmp(firebaseWriteCommandStatus -> str, WRITE_COMMAND_FAILURE_STATUS) == 0){
    json.set(DEVICE_OUTPUT_RES_ADDRESS, "FAILURE;"); 
  }else{
     json.set(DEVICE_OUTPUT_RES_ADDRESS, firebaseWriteCommandStatus -> str);
  }

  json.set(DEVICE_OUTPUT_TS_ADDRESS, firebaseWriteCommandStatus -> time);
  
  //json.toString(Serial, true);
  
  
 //Serial.printf("Set json... %s\n", Firebase.RTDB.set(&fbdoWrite, F("/device/device_1/show"), &json) ? "ok" : fbdoWrite.errorReason().c_str());

  bool status = false;//Firebase.RTDB.set(&fbdoWrite, F("/device/device_1/show"), &json);
  
  vPortFree(firebaseWriteCommandStatus -> str);
  vPortFree(firebaseWriteCommandStatus);
  
  return status;
}

bool MyFirebase::writeCommandStatus(bool status, uint32_t time) {
  FirebaseWriteCommandStatus* firebaseWriteCommandStatus = (FirebaseWriteCommandStatus *) pvPortMalloc(sizeof(FirebaseWriteCommandStatus));
  char* strToSend = (char *) pvPortMalloc(2 * sizeof(char));
  strToSend[0] = status ? WRITE_COMMAND_SUCCESS_STATUS[0] : WRITE_COMMAND_FAILURE_STATUS[0];
  strToSend[1] = '\0'; 
  firebaseWriteCommandStatus -> str = strToSend;
  firebaseWriteCommandStatus -> time = time;  
  
  if(xQueueSend(*firebaseWriteQueue, &firebaseWriteCommandStatus, 10) == pdPASS){
    return true;
  }
  LOG("FirebaseWriteQueue is Full");
  return false;
}

bool MyFirebase::writeString(String str , uint32_t time){  
   FirebaseWriteCommandStatus* firebaseWriteCommandStatus = (FirebaseWriteCommandStatus *) pvPortMalloc(sizeof(FirebaseWriteCommandStatus));
   char* strToSend = (char *) pvPortMalloc((str.length() + 2) * sizeof(char));
   for(int i = 0; i < str.length(); i++){
     strToSend[i] = str.charAt(i);
   }
   strToSend[str.length()] = ';';
   strToSend[str.length() + 1] = '\0';
   firebaseWriteCommandStatus -> str = strToSend;
   firebaseWriteCommandStatus -> time = time;  
   
   if(xQueueSend(*firebaseWriteQueue, &firebaseWriteCommandStatus, 10) == pdPASS){
    return true;
  }
  LOG("FirebaseWriteQueue is Full");
  return false;
}
