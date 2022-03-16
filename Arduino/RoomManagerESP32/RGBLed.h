#include <Arduino.h>
#include "StructType.h"
#include "Util.h"

#ifndef RGBLed_H

  #define RGBLed_H
  
  class RGBLed{
    uint8_t* red;
    uint8_t* green;
    uint8_t* blue;

    uint8_t i;
    uint8_t prevColorCount;
    uint16_t ledAnimationInterval;
    uint16_t rgbLedTransitionEffectTime;
    bool isPowerKeyOffVar;
    bool isOn;
    uint8_t nextTransitionMode;
    bool isRunningFadingTask_;
    
  	public:
  	 void begin();  
  	 void setColor(int r, int g, int b);
     void setRandomColor();
     void setRandomColorWithTransitionEffect();
     void setNextColor();
     void setPrevColor();
     void incrementLedAnimationIntervalInSecond(uint8_t t);
     void decrementLedAnimationIntervalInSecond(uint8_t t);
     void setLedAnimationInterval(uint16_t t);
     uint16_t getLedAnimationInterval();
     void setRgbLedTransitionEffectTime(uint16_t t);
     uint16_t getRgbLedTransitionEffectTime();
     void onOffToggle();
     void off();
     void powerKeyPressed();
     bool isPowerKeyOff();
     void setColorWithFading(int r, int g, int b);
     void setNextTransitionMode(uint8_t mode);
     uint8_t getNextTransitionMode();
     bool isRunningFadingTask();
     void setRunningFadingTask(bool flag);
  };
#endif
