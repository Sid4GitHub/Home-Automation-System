#include<Arduino.h>
#include <WiFi.h>

#include "StructType.h"
#include "Constant.h"

#ifndef Util_H

  #define Util_H

	String toString(ReadQueueElement* msgPointer);
  ReadQueueElement* buildReadQueueElement(String subStr);
  void freeReadQueueElement(ReadQueueElement* readQueueElement);
  void wifiSetup(const char *ssid, const char *password);
  void initializeRelayPins();
  
#endif
