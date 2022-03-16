#include "Util.h"

ReadQueueElement* buildReadQueueElement(String data){
  int nextColIdx = data.indexOf(":");
  uint8_t header = nextColIdx > 0 ? data.substring(0, nextColIdx).toInt(): 0;
  ReadQueueElement* const readQueueElement = (ReadQueueElement *) pvPortMalloc(sizeof(ReadQueueElement));
  readQueueElement -> type = header;
  readQueueElement -> rgbLedColPtr = NULL;
  readQueueElement -> str = NULL;
  switch(header){
  	case Event_Queue_Handler_TYPE_REMOTE_CODE:
    	  {
      		String hexString = "0x" + data;
      		uint32_t rmtCode = 0;
      		sscanf(hexString.c_str(), "%x", &rmtCode);
      		readQueueElement -> uint32Number = rmtCode;
    	  }
  	    break;
        
  	case Event_Queue_Handler_TYPE_RGB_COLOR:
    	  {
      		RGBLedCol* const ledPtr = (RGBLedCol *) pvPortMalloc(sizeof(RGBLedCol));
      		uint8_t nextColIdx_ = data.indexOf("-", nextColIdx + 1);
      		ledPtr -> red = data.substring(nextColIdx + 1, nextColIdx_).toInt();
      		nextColIdx = nextColIdx_;
      		nextColIdx_ = data.indexOf("-", nextColIdx + 1);
      		ledPtr -> green = data.substring(nextColIdx + 1, nextColIdx_).toInt();
      		nextColIdx = nextColIdx_;
      		ledPtr -> blue = data.substring(nextColIdx + 1).toInt();
      		readQueueElement -> rgbLedColPtr = ledPtr;
    	  }
  	    break;
        
  	case Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION:   
    	  {
          uint8_t nextColIdx_ = data.indexOf("-", nextColIdx + 1);
          readQueueElement -> uint8Number = data.substring(nextColIdx + 1, nextColIdx_).toInt();
          nextColIdx = nextColIdx_;
          nextColIdx_ = data.indexOf("-", nextColIdx + 1);
          readQueueElement -> uint32Number = strtoull(data.substring(nextColIdx + 1, nextColIdx_).c_str(), NULL, 10);
          nextColIdx = nextColIdx_;
          readQueueElement -> uint16Number = strtoull(data.substring(nextColIdx + 1).c_str(), NULL, 10);          
    	  }     
    	  break;
        
  	case Event_Queue_Handler_TYPE_ALARM_SET:        
    	  {
    		  uint32_t alarmTiming = strtoull(data.substring(nextColIdx + 1).c_str(), NULL, 10); 
          readQueueElement -> uint32Number = alarmTiming;
    	  }
        break;
        
    case Event_Queue_Handler_TYPE_TIME_SYNC:        
        {      
          uint32_t syncTime = strtoull(data.substring(nextColIdx + 1).c_str(), NULL, 10); 
          readQueueElement -> uint32Number = syncTime;
        }
    	  break;
        
    case Event_Queue_Handler_TYPE_WIFI_SSID_SET:
    case Event_Queue_Handler_TYPE_WIFI_PASSWORD_SET:
        {
          String str = data.substring(nextColIdx + 1);
          char* receivedStr = (char *) pvPortMalloc((str.length() + 1) * sizeof(char));
          for(int i = 0; i < str.length(); i++){
            receivedStr[i] = str.charAt(i);
          }
          receivedStr[str.length()] = '\0';
          readQueueElement -> str = (const char*)receivedStr; 
        }
        break;
     case Event_Queue_Handler_TYPE_SCHEDULE_RELAY_ON_OFF:
        { 
          uint8_t nextColIdx_ = data.indexOf("-", nextColIdx + 1);
          readQueueElement -> uint8Number = data.substring(nextColIdx + 1, nextColIdx_).toInt();
          nextColIdx = nextColIdx_;
          nextColIdx_ = data.indexOf("-", nextColIdx + 1);
          readQueueElement -> uint32Number = strtoull(data.substring(nextColIdx + 1, nextColIdx_).c_str(), NULL, 10);
          nextColIdx = nextColIdx_;          
          readQueueElement -> uint8Number2 = data.substring(nextColIdx + 1).toInt();
        }
        break;
        
     case Event_Queue_Handler_TYPE_RESET_SCHEDULE_RELAY:
     case Event_Queue_Handler_TYPE_DEVICE_SHARE_CURRENT_RELAY_SCHEDULE:
        {
          uint8_t nextColIdx_ = data.indexOf("-", nextColIdx + 1);
          readQueueElement -> uint8Number = data.substring(nextColIdx + 1, nextColIdx_).toInt();
        }
        break;
  }
  return readQueueElement;
}

String toString(ReadQueueElement* msgPointer){
    String sourceStr = "source= " + String(msgPointer -> source, DEC);
    uint8_t typeId = msgPointer -> type;
    String type = "type= " + String(typeId, DEC);
    String rgb = "";
    String ani = "";
    String alarm = "";
    String uint32Number = "";
    String str = "";
    String setResetScheduleOnOff = "";
    
    if(typeId == Event_Queue_Handler_TYPE_REMOTE_CODE) uint32Number = "remoteCode= " + String(msgPointer -> uint32Number, DEC);
    else if(typeId == Event_Queue_Handler_TYPE_TIME_SYNC) uint32Number = "syncRTC= " + String(msgPointer -> uint32Number, DEC);
    else if(typeId == Event_Queue_Handler_TYPE_RGB_COLOR) rgb = "rgbLedCol= RgbLedCol("+ String(msgPointer -> rgbLedColPtr -> red, DEC) +", " + String(msgPointer -> rgbLedColPtr -> green, DEC) + ", " + String(msgPointer -> rgbLedColPtr -> blue, DEC) +")";
    else if(typeId == Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION) ani = "ledAnimation= [type= "+ String(msgPointer -> uint8Number, DEC) +", intervalTime= " + String(msgPointer -> uint32Number, DEC) + ", transitionTime= " + String(msgPointer -> uint16Number, DEC) + "]";
    else if(typeId == Event_Queue_Handler_TYPE_ALARM_SET) alarm =  "alarm= [At: " + String(msgPointer -> uint32Number, DEC) + "]";
    else if(typeId == Event_Queue_Handler_TYPE_WIFI_SSID_SET || typeId == Event_Queue_Handler_TYPE_WIFI_PASSWORD_SET) str = "str= " + String(msgPointer -> str);
    else if(typeId == Event_Queue_Handler_TYPE_SCHEDULE_RELAY_ON_OFF) setResetScheduleOnOff = "scheduleOnOff = [Relay Idx[uint8Number]: " + String(msgPointer -> uint8Number, DEC) + ", At[uint32Number]: " + String(msgPointer -> uint32Number, DEC) + ", New State[uint8Number2]: " + String(msgPointer -> uint8Number2, DEC) + "]";
    else if(typeId == Event_Queue_Handler_TYPE_RESET_SCHEDULE_RELAY) setResetScheduleOnOff = "resetScheduleOnOff = [Relay Idx[uint8Number]: " + String(msgPointer -> uint8Number, DEC) + "]";
    
    return String( "ReadQueueElement("
    + sourceStr + ", " 
    + type + ", " 
    + rgb + ", " 
    + ani + ", " 
    + uint32Number + ", "
    + alarm + ", "
    + str + ", "
    + setResetScheduleOnOff
    +")"
    );
}

void freeReadQueueElement(ReadQueueElement* readQueueElement){
  vPortFree((char *)readQueueElement -> str);
  vPortFree(readQueueElement -> rgbLedColPtr);
  vPortFree(readQueueElement);
}

void wifiSetup(const char *ssid, const char *password) {
  
  WiFi.begin(ssid, password);
  Serial.print("Connecting to Wi-Fi");
  
  for(int i = 0; i < 20 && WiFi.status() != WL_CONNECTED; i++) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();
}
