
#include <WiFi.h>
#include <ezTime.h>
#include <RTClib.h>
#include <queue>
#include <Firebase_ESP_Client.h>

#include "Logger.h"

#include "StructType.h"
#include "RGBLed.h"
#include "MyClock.h"
#include "MyBluetooth.h"
#include "MyIRRemote.h"
#include "MyFirebase.h"
#include "StructType.h"
#include "Util.h"
#include "Constant.h"
#include "MyPreference.h"
#include "MyRelayArray.h"

RGBLed rgbLed;
MyClock myClock;
MyFirebase myFirebase;
MyBluetooth myBluetooth;
MyIRRemote myIRRemote;
FirebaseAuth auth;
FirebaseConfig config;
MyPreference pref(ROM1);
MyRelayArray relays;

TaskHandle_t Task1;
TaskHandle_t alarmLookUpTaskHandle;
TaskHandle_t rgbLedAnimationType1TaskHandle;
TaskHandle_t rgbLedAnimationType2TaskHandle; 
TaskHandle_t setNextRGBLedColorTaskHandle;
TaskHandle_t setPrevRGBLedColorTaskHandle;
QueueHandle_t readQueue = NULL;
QueueHandle_t bluetoothWriteQueue = NULL;
QueueHandle_t firebaseWriteQueue = NULL;




//Core0 is configured for RF communication; Don't keep time intensive task like read stream or http connection
void core0Task(void * pvParameters) {
  Serial.println("Task1 running on core: " + xPortGetCoreID());
  for (;;) {
    //To reset internal watch dog;
    vTaskDelay(1 / portTICK_PERIOD_MS);
  }
}

void readCommandEventQueueHandler(void * pvParameters) {
  for (;;) {
    ReadQueueElement* readQueueElement;
    xQueueReceive(readQueue, &readQueueElement, (TickType_t) portMAX_DELAY); 
    Serial.println("Received Command From queue: " + toString(readQueueElement));
    uint8_t commandStatus = 0;
    switch(readQueueElement -> type){
      case Event_Queue_Handler_TYPE_REMOTE_CODE:
        {
            if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(0)){
              relays.powerOnOffToggle(); //power
              rgbLed.powerKeyPressed();
            }else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(1)){//mode
              if(rgbLed.getNextTransitionMode() == Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION_TYPE_1){
                rgbLed.setNextTransitionMode(Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION_TYPE_2);
              }else{ 
                rgbLed.setNextTransitionMode(Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION_TYPE_1);
              }
              
            }else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(2)){//mute              
              if(myClock.isAlarmOn()){
                 pref.alarmOff();
                 vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
                 if(!pref.isAlarmSet()){
                    vTaskSuspend(alarmLookUpTaskHandle);
                    myClock.resetAlarm(); 
                 }               
              }
            } else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(12)){
              relays.toggleRelayState(0); //1
            } else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(13)){
                relays.toggleRelayState(1);//2
            }else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(14)){
                relays.toggleRelayState(2);//3 
            } else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(15)){
                relays.toggleRelayState(3);//4 
            } else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(16)) {
                relays.toggleRelayState(4);//5 
            } else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(17)){
                relays.toggleRelayState(5);//6 
            } else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(18)){
                relays.toggleRelayState(6);//7 
            } else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(19)){
                relays.toggleRelayState(7);//8 
            } else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(20)){              
                relays.toggleRelayState(8);//9 
            } else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(9)){//0           
                if(!rgbLed.isPowerKeyOff()){  
                  vTaskSuspend(rgbLedAnimationType1TaskHandle);
                  vTaskSuspend(rgbLedAnimationType2TaskHandle);
                  rgbLed.onOffToggle();
                }              
            } else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(4)){ //pre  
                if(!rgbLed.isPowerKeyOff()){  
                  vTaskSuspend(rgbLedAnimationType1TaskHandle);
                  vTaskSuspend(rgbLedAnimationType2TaskHandle);
                  xTaskNotifyGive(setPrevRGBLedColorTaskHandle);
              }
            } else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(5)){//next              
                if(!rgbLed.isPowerKeyOff()){     
                  vTaskSuspend(rgbLedAnimationType1TaskHandle);
                  vTaskSuspend(rgbLedAnimationType2TaskHandle);
                  xTaskNotifyGive(setNextRGBLedColorTaskHandle);
                }              
            }else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(6)){//eq      
                if(!rgbLed.isPowerKeyOff()){        
                  //https://prusa3d.github.io/Prusa-Firmware-Buddy-Doc/db/da4/task_8h.html
                  eTaskState animation1 = eTaskGetState(rgbLedAnimationType1TaskHandle);
                  eTaskState animation2 = eTaskGetState(rgbLedAnimationType2TaskHandle); 
                  if(animation1 == 3 && animation2 == 3){
                    vTaskResume(rgbLedAnimationType1TaskHandle);
                  }else if(animation1 != 3 && animation2 == 3){
                    vTaskSuspend(rgbLedAnimationType1TaskHandle);
                    vTaskResume(rgbLedAnimationType2TaskHandle);
                  }else{
                    if(animation1 != 3) vTaskSuspend(rgbLedAnimationType1TaskHandle);
                    if(animation2 != 3) vTaskSuspend(rgbLedAnimationType2TaskHandle);
                    rgbLed.off();
                  }
                }
            }else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(7)){//vol+                
                rgbLed.incrementLedAnimationIntervalInSecond(1);              
            }else if(readQueueElement -> uint32Number == myIRRemote.getRemoteCode(8)){//vol-              
                rgbLed.decrementLedAnimationIntervalInSecond(1);              
            }
                          
        }
        
        break;
        
      case Event_Queue_Handler_TYPE_RGB_COLOR: 
        {
          if(!rgbLed.isPowerKeyOff()){              
            vTaskSuspend(rgbLedAnimationType1TaskHandle);
            vTaskSuspend(rgbLedAnimationType2TaskHandle);
            rgbLed.setColor(readQueueElement -> rgbLedColPtr -> red, readQueueElement -> rgbLedColPtr -> green, readQueueElement -> rgbLedColPtr -> blue);
          }
          commandStatus = 1;
        }
        break;

      case Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION: 
        {
          if(!rgbLed.isPowerKeyOff()){                 
            rgbLed.setLedAnimationInterval(readQueueElement -> uint32Number);
            rgbLed.setRgbLedTransitionEffectTime(readQueueElement -> uint16Number);
            if(readQueueElement -> uint8Number == Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION_TYPE_1){
              vTaskSuspend(rgbLedAnimationType2TaskHandle);
              vTaskResume(rgbLedAnimationType1TaskHandle);  
            }else if(readQueueElement -> uint8Number == Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION_TYPE_2){          
              vTaskSuspend(rgbLedAnimationType1TaskHandle);
              vTaskResume(rgbLedAnimationType2TaskHandle);  
            }
          }
        }
        break;
        
      case Event_Queue_Handler_TYPE_ALARM_SET:
        {
          vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
          pref.setAlarm(readQueueElement -> uint32Number);
          myClock.setAlarm(readQueueElement -> uint32Number);
          vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
          uint32_t* alarmDetails = (uint32_t *) pvPortMalloc(2 * sizeof(uint32_t));
          vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
          pref.getAlarm(alarmDetails);
          if(alarmDetails[0] == 1 && alarmDetails[1] == readQueueElement -> uint32Number){ 
            commandStatus = 1;
            vTaskResume(alarmLookUpTaskHandle);
          }else{ 
            commandStatus = 2;
          }
          vPortFree(alarmDetails);          
        }
        break;
        
      case Event_Queue_Handler_TYPE_ALARM_RESET:
        {
           pref.alarmOff();
           vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
           if(!pref.isAlarmSet()){
             vTaskSuspend(alarmLookUpTaskHandle);
             myClock.resetAlarm(); 
             commandStatus = 1;
           }else{
            commandStatus = 2;
           }
        }
        break;
        
      case Event_Queue_Handler_TYPE_DEVICE_SHARE_CURRENT_ALARM_CONFIG:
        {
          uint32_t* alarmDetails = (uint32_t *) pvPortMalloc(2 * sizeof(uint32_t));
          vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
          pref.getAlarm(alarmDetails);
          vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
          
          String outputStr = "";
          if(alarmDetails[0] == 0){
            outputStr = "No Alarm is Set";
          }else{
            outputStr = RESPONSE_QUEUE_TYPE_CURRENT_ALARM_SETTINGS_SHARE + String(alarmDetails[1], DEC);
          }
          if(readQueueElement -> source == Event_Queue_INPUT_SOURCE_BLUETOOTH){
              myBluetooth.writeString(outputStr);
          }else if(readQueueElement -> source == Event_Queue_INPUT_SOURCE_FIREBASE){
              myFirebase.writeString(outputStr, myClock.now());
          }
          
          vPortFree(alarmDetails); 
        }
        break;
        
      case Event_Queue_Handler_TYPE_TIME_SYNC:
        {
          myClock.syncRTCTime(readQueueElement -> uint32Number);
          commandStatus = 1;
        }
        break;
        
      case Event_Queue_Handler_TYPE_WIFI_SSID_SET:
        {
          vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS); 
          pref.setWifiSSID(readQueueElement -> str); 
          vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);          
        }
        break;
        
      case Event_Queue_Handler_TYPE_WIFI_PASSWORD_SET:
        {
          vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS); 
          pref.setWifiPassword(readQueueElement -> str); 
          vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);          
        }
        break;
        
      case Event_Queue_Handler_TYPE_DEVICE_RESET:
        {
          ESP.restart(); 
        }
        break;
        
      case Event_Queue_Handler_TYPE_DEVICE_SHARE_CURRENT_WIFI_SETTINGS:
        {
          String ssidDetails = "SSID: '" +  pref.getWifiSSID() + "'";
          if(readQueueElement -> source == Event_Queue_INPUT_SOURCE_BLUETOOTH){
              myBluetooth.writeString(ssidDetails);
          }else if(readQueueElement -> source == Event_Queue_INPUT_SOURCE_FIREBASE){
              myFirebase.writeString(ssidDetails, myClock.now());
          }
          
          vTaskDelay(500 / portTICK_PERIOD_MS);
          
          String wifiPassword = "Password: '" +  pref.getWifiPassword() + "'";        
          if(readQueueElement -> source == Event_Queue_INPUT_SOURCE_BLUETOOTH){
              myBluetooth.writeString(wifiPassword);
          }/*else if(readQueueElement -> source == Event_Queue_INPUT_SOURCE_FIREBASE){
              myFirebase.writeString(wifiPassword, myClock.now());
          }*/        
          commandStatus = 0;
        }
        break;

        case Event_Queue_Handler_TYPE_DEVICE_SHARE_CURRENT_WIFI_STATUS:
        {                
          String outputStr = "";
          if(WiFi.status() == WL_CONNECTED){
            outputStr = "WIFI Connected";
          }else{
            outputStr = "WIFI Not Connected: " + String(WiFi.status(), DEC);
          }
          if(readQueueElement -> source == Event_Queue_INPUT_SOURCE_BLUETOOTH){
              myBluetooth.writeString(outputStr);
          }else if(readQueueElement -> source == Event_Queue_INPUT_SOURCE_FIREBASE){
              myFirebase.writeString(outputStr, myClock.now());
          }
          commandStatus = 0;
        }
        break;

        case Event_Queue_Handler_TYPE_SCHEDULE_RELAY_ON_OFF:
        {                
          relays.scheduleRelayStateChange(readQueueElement -> uint8Number - 1, readQueueElement -> uint32Number, readQueueElement -> uint8Number2);
          commandStatus = 1;
        }
        break;

        case Event_Queue_Handler_TYPE_RESET_SCHEDULE_RELAY:
        {                
          relays.resetScheduleForRelayStateChange(readQueueElement -> uint8Number - 1);
          commandStatus = 1;
        }
        break;

        case Event_Queue_Handler_TYPE_DEVICE_SHARE_CURRENT_RELAY_SCHEDULE:
        {                
          RelaySchedule* relaySchedule = relays.getRelaySchedule(readQueueElement -> uint8Number - 1);
          if(relaySchedule -> scheduleAt == UINT_32_T_MAX_VALUE){
            myBluetooth.writeString("No Schedule for Id: " + String(readQueueElement -> uint8Number, DEC) + ", Current State: " + String(relaySchedule -> currentState, DEC));
          }else{
            String outputStr = String(RESPONSE_QUEUE_TYPE_CURRENT_RELAY_SCHEDULE_SHARE) + "Id: " + String(readQueueElement -> uint8Number, DEC) + ", State: " + String(relaySchedule -> currentState, DEC) + ", At: " + String(relaySchedule -> scheduleAt, DEC) + ", New State: " + String(relaySchedule -> newState, DEC);
            if(readQueueElement -> source == Event_Queue_INPUT_SOURCE_BLUETOOTH){
              myBluetooth.writeString(outputStr);
            }else if(readQueueElement -> source == Event_Queue_INPUT_SOURCE_FIREBASE){
              myFirebase.writeString(outputStr, myClock.now());
            }
          }
          vPortFree(relaySchedule);
          commandStatus = 0;
        }
        break;
        
    }
    
    if(readQueueElement -> source == Event_Queue_INPUT_SOURCE_BLUETOOTH){
      if(commandStatus == 1){
        myBluetooth.writeCommandStatus(true);   
      } else if(commandStatus == 2){
        myBluetooth.writeCommandStatus(false);   
      }
    }/*else if(readQueueElement -> source == Event_Queue_INPUT_SOURCE_FIREBASE){
      if(commandStatus == 1){
        myFirebase.writeCommandStatus(true, myClock.now());   
      } else if(commandStatus == 2){
        myFirebase.writeCommandStatus(false, myClock.now());   
      }
    }*/
    
    freeReadQueueElement(readQueueElement); 
    vTaskDelay(1 / portTICK_PERIOD_MS);
  }
}

void remoteCommandRead(void * pvParameters) {
  for (;;) {
    myIRRemote.remoteCommandRead();

    //All task in core-0 should have a delay to reset internal watch-dog
    vTaskDelay(1 / portTICK_PERIOD_MS);
  }
}

void bluetoothReadListener(void * pvParameters){
  for(;;){
    myBluetooth.bluetoothRead();  
  }
}

void bluetoothWriteEventQueueHandler(void * pvParameters){
  for(;;){
    myBluetooth.bluetoothWriteEventQueueHandler();
  }
}

void firebaseReadEventListener(void * pvParameters){
  for(;;){
    if(WiFi.status() == WL_CONNECTED){
      myFirebase.firebaseReadEvent();
    }else{
      vTaskDelay(NO_NETWORK_LISTENER_SERVICE_STOP/ portTICK_PERIOD_MS);
    }
  }
}

void firebaseWriteEventQueueHandler(void * pvParameters){
  for(;;){
    if(WiFi.status() == WL_CONNECTED){
      myFirebase.firebaseWriteEventQueueHandler();
    }else{
      vTaskDelay(NO_NETWORK_LISTENER_SERVICE_STOP/ portTICK_PERIOD_MS);
    }
  }
}

void ntpSyncTask(void * pvParameters){
  for(;;){    
    myClock.syncWithNtp();
    vTaskDelay(TIME_SYNC_UP_SERVICE_DELAY/ portTICK_PERIOD_MS);
  }
}

void alarmLookUpTask(void * pvParameters){
  for(;;){
     myClock.alarmLookUpService();
     vTaskDelay(ALARM_LOOKUP_SERVICE_MONITOR_DELAY / portTICK_PERIOD_MS);    
  }
}


void relayScheduleLookUpTask(void * pvParameters){
  for(;;){
     relays.relayScheduleLookUp(myClock.now());
     vTaskDelay(RELAY_LOOKUP_SERVICE_MONITOR_DELAY / portTICK_PERIOD_MS);    
  }
}

void rgbLedAnimationType1Task(void * pvParameters){
  for(;;){     
     rgbLed.setRandomColor();
     vTaskDelay(rgbLed.getLedAnimationInterval()/ portTICK_PERIOD_MS); 
     vTaskDelay(1/ portTICK_PERIOD_MS); 
  }
}

void rgbLedAnimationType2Task(void * pvParameters){
  for(;;){     
    rgbLed.setRandomColorWithTransitionEffect();
    vTaskDelay(rgbLed.getLedAnimationInterval()/ portTICK_PERIOD_MS);
    vTaskDelay(1/ portTICK_PERIOD_MS); 
  }
}

void setNextRGBLedColorTask(void * pvParameters){
  for(;;){     
    ulTaskNotifyTake(pdTRUE, portMAX_DELAY);
    rgbLed.setNextColor(); 
    vTaskDelay(1/ portTICK_PERIOD_MS); 
  }
}

void setPrevRGBLedColorTask(void * pvParameters){
  for(;;){     
    ulTaskNotifyTake(pdTRUE, portMAX_DELAY);
    rgbLed.setPrevColor();  
    vTaskDelay(1/ portTICK_PERIOD_MS); 
  }
}

void setup() {
  Serial.begin(115200);
  Serial.println("The device started");

  relays.begin();
  
  pref.begin();
  
  String wifiName = pref.getWifiSSID();
  String wifiPassword = pref.getWifiPassword();
  Serial.println("wifi ssid: '" + wifiName +"'");
  //Serial.println("wifi password: '"+ wifiPassword + "'");
  wifiSetup(wifiName.c_str(), wifiPassword.c_str());

  
  myClock.begin();
  rgbLed.begin();
  
  readQueue = xQueueCreate(READ_QUEUE_SIZE, sizeof(char*));  
  if(readQueue == NULL){
    Serial.println("Error creating the ReadQueue");
    ESP.restart();
  }

  bluetoothWriteQueue = xQueueCreate(BLUETOOTH_WRITE_QUEUE_SIZE, sizeof(char*));  
  if(bluetoothWriteQueue == NULL){
    Serial.println("Error creating the BluetoothWriteQueue");
    ESP.restart();
  }

  firebaseWriteQueue = xQueueCreate(FIREBASE_WRITE_QUEUE_SIZE, sizeof(char*));  
  if(firebaseWriteQueue == NULL){
    Serial.println("Error creating the FirebaseWriteQueue");
    ESP.restart();
  }

  String  dbURL = pref.getFirebaseUrl();
  String apiKey = pref.getFirebaseWebKey();

  config.database_url = dbURL;
  config.api_key = apiKey;

  String emailId = pref.getFirebaseLoginEmailId();
  String password =  pref.getFirebaseLoginEmailPassword();

  auth.user.email = emailId;
  auth.user.password = password;
  
  Serial.println("DATABASE_URL: "+ dbURL);
  //Serial.println("WEB_API_KEY: " + apiKey);
  //Serial.println("emailId: "+ emailId);
  //Serial.println("password: "+ password);
  
  myFirebase.begin(&readQueue, &firebaseWriteQueue, myClock.now(), &config, &auth);
  myBluetooth.begin(&readQueue, &bluetoothWriteQueue);
  myIRRemote.begin(&readQueue);

  
  // Task function.
  // name of task. 
  // Stack size of task 
  // parameter of the task 
  // priority of the task
  // Task handle to keep track of created task
  // pin task to core 1
  
  vTaskDelay(50 / portTICK_PERIOD_MS);   
  
  
  xTaskCreatePinnedToCore(remoteCommandRead, "remoteCommandRead", CORE_0_TASK_STACK_SIZE, NULL, 1, NULL, 0);
  vTaskDelay(10 / portTICK_PERIOD_MS);   
  
  xTaskCreatePinnedToCore(alarmLookUpTask, "alarmLookUpTask", CORE_0_TASK_STACK_SIZE, NULL, 1, &alarmLookUpTaskHandle, 0);
  vTaskDelay(10 / portTICK_PERIOD_MS);

  uint32_t* alarmDetails = (uint32_t *) pvPortMalloc(2 * sizeof(uint32_t));
  vTaskDelay(WIRE_TRANSACTION_DELAY / portTICK_PERIOD_MS);
  pref.getAlarm(alarmDetails);
  if(alarmDetails[0] == 1){ 
    myClock.setAlarm(alarmDetails[1]);
  }else{
    vTaskSuspend(alarmLookUpTaskHandle);
  }
  vPortFree(alarmDetails);

  xTaskCreatePinnedToCore(rgbLedAnimationType1Task, "rgbLedAnimationType1Task", CORE_0_TASK_STACK_SIZE, NULL, 1, &rgbLedAnimationType1TaskHandle, 0);
  vTaskSuspend(rgbLedAnimationType1TaskHandle);

  xTaskCreatePinnedToCore(rgbLedAnimationType2Task, "rgbLedAnimationType1Task", CORE_0_TASK_STACK_SIZE, NULL, 1, &rgbLedAnimationType2TaskHandle, 0);
  vTaskSuspend(rgbLedAnimationType2TaskHandle);

  xTaskCreatePinnedToCore(setPrevRGBLedColorTask, "setPrevRGBLedColorTask", CORE_1_TASK_STACK_SIZE, NULL, 1, &setPrevRGBLedColorTaskHandle, 1);
  xTaskCreatePinnedToCore(setNextRGBLedColorTask, "setNextRGBLedColorTask", CORE_1_TASK_STACK_SIZE, NULL, 1, &setNextRGBLedColorTaskHandle, 1);

   
  xTaskCreatePinnedToCore(relayScheduleLookUpTask, "relayScheduleLookUpTask", CORE_0_TASK_STACK_SIZE, NULL, 1, NULL, 0);
  vTaskDelay(10 / portTICK_PERIOD_MS);   
  xTaskCreatePinnedToCore(readCommandEventQueueHandler, "readCommandEventQueueHandler", CORE_0_TASK_STACK_SIZE, NULL, 1, NULL, 0);


  vTaskDelay(10 / portTICK_PERIOD_MS);   
  xTaskCreatePinnedToCore(bluetoothReadListener, "bluetoothReadListener", CORE_1_TASK_STACK_SIZE, NULL, 1, NULL, 1);                      
  vTaskDelay(10 / portTICK_PERIOD_MS);   
  xTaskCreatePinnedToCore(bluetoothWriteEventQueueHandler, "bluetoothWriteEventQueueHandler", CORE_1_TASK_STACK_SIZE, NULL, 1, NULL, 1);    
  
  vTaskDelay(10 / portTICK_PERIOD_MS);   
  xTaskCreatePinnedToCore(firebaseReadEventListener, "firebaseReadEventListener", CORE_1_TASK_STACK_SIZE, NULL, 1, NULL, 1);
  //vTaskDelay(10 / portTICK_PERIOD_MS);   
  //xTaskCreatePinnedToCore(firebaseWriteEventQueueHandler, "firebaseWriteEventQueueHandler", CORE_1_TASK_STACK_SIZE, NULL, 1, NULL, 1);

  vTaskDelay(10 / portTICK_PERIOD_MS);   
  xTaskCreatePinnedToCore(ntpSyncTask, "ntpSyncTask", CORE_1_TASK_STACK_SIZE, NULL, 1, NULL, 1);
  
  vTaskDelay(50 / portTICK_PERIOD_MS);   
  Serial.println("***Setup completed***");
}

uint32_t prvTimeShown = 0;

//It will run on core 1
void loop() {
  vTaskDelete(NULL);
}
