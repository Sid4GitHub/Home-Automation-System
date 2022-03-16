#include "MyBluetooth.h"

void MyBluetooth::begin(QueueHandle_t* readQueue_, QueueHandle_t* bluetoothWriteQueue_){
  Serial2.begin(9600);
  readQueue = readQueue_; 
  bluetoothWriteQueue = bluetoothWriteQueue_;
}  


bool MyBluetooth::bluetoothRead() {
  if (Serial2.available()) {      
      String input = Serial2.readString();
      int pos_start = 0;
      int pos_end = -1;
      int i = 0;
      int intStrLen = input.length();
      while(i < intStrLen){
        int e =  input.indexOf (";", i);
        if(e <= 0) break;
        String subStr = input.substring (i, e);
        ReadQueueElement* const readQueueElement = buildReadQueueElement(subStr);          
        readQueueElement -> source = Event_Queue_INPUT_SOURCE_BLUETOOTH;       
        if(xQueueSend(*readQueue, &readQueueElement , READ_QUEUE_PUSH_WAIT_TIME) != pdPASS){
            LOG("Read Event Failed Queue is Full");
            freeReadQueueElement(readQueueElement);
            vTaskDelay(BLUETOOTH_QUEUE_MSG_PUSH_DELAY + 100 / portTICK_PERIOD_MS);        
        }else{
          i = e + 1;
        }
        vTaskDelay(BLUETOOTH_QUEUE_MSG_PUSH_DELAY / portTICK_PERIOD_MS);        
      }
      return true;
  }
  return false;
}


void MyBluetooth::bluetoothWriteEventQueueHandler() {
  char* str;
  xQueueReceive(*bluetoothWriteQueue, &str, portMAX_DELAY); 
  if(strcmp(str, WRITE_COMMAND_SUCCESS_STATUS) == 0){
    Serial2.print("SUCCESS;");
  }else if(strcmp(str, WRITE_COMMAND_FAILURE_STATUS) == 0){
    Serial2.print("FAILURE;");
  }else{
    Serial2.print(str);
  }
  vPortFree(str);
}


bool MyBluetooth::writeCommandStatus(bool status) {
  char* strToSend = (char *) pvPortMalloc(2 * sizeof(char));
  strToSend[0] = status ? WRITE_COMMAND_SUCCESS_STATUS[0] : WRITE_COMMAND_FAILURE_STATUS[0];
  strToSend[1] = '\0'; 
  if(xQueueSend(*bluetoothWriteQueue, &strToSend, 10) == pdPASS){
    return true;
  }
  Serial.println("BluetoothWriteQueue is Full");
  return false;
}

bool MyBluetooth::writeString(String str){  
   char* strToSend = (char *) pvPortMalloc((str.length() + 2) * sizeof(char));
   for(int i = 0; i < str.length(); i++){
     strToSend[i] = str.charAt(i);
   }
   strToSend[str.length()] = ';';
   strToSend[str.length() + 1] = '\0';
   
   if(xQueueSend(*bluetoothWriteQueue, &strToSend, 10) == pdPASS){
    return true;
  }
  Serial.println("BluetoothWriteQueue is Full");
  return false;
}
