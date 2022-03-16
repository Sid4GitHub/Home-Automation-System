#include "RGBLed.h"

void RGBLed::begin(){
  ledcAttachPin(PIN_RED, CHANNEL_RED);
  ledcAttachPin(PIN_GREEN, CHANNEL_GREEN);
  ledcAttachPin(PIN_BLUE, CHANNEL_BLUE);

  ledcSetup(CHANNEL_RED, RGB_LED_FREQUENCY, RGB_LED_RESOLUTION);
  ledcSetup(CHANNEL_GREEN, RGB_LED_FREQUENCY, RGB_LED_RESOLUTION);
  ledcSetup(CHANNEL_BLUE, RGB_LED_FREQUENCY, RGB_LED_RESOLUTION);
  
  ledcWrite(CHANNEL_RED, 0);
  ledcWrite(CHANNEL_GREEN, 0);
  ledcWrite(CHANNEL_BLUE, 0);

  ledAnimationInterval = LED_ANIMATION_INTERVAL;
  rgbLedTransitionEffectTime = RGB_LED_TRANSITION_EFFECT_TIME;
  isPowerKeyOffVar = false;
  isOn = false;
  nextTransitionMode = Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION_TYPE_1;
  isRunningFadingTask_ = false;
  i = 0;
  prevColorCount = 0;
  red = (uint8_t *) pvPortMalloc(RGB_LED_COLOR_HISTORY * sizeof(uint8_t));
  green = (uint8_t *) pvPortMalloc(RGB_LED_COLOR_HISTORY * sizeof(uint8_t));
  blue = (uint8_t *) pvPortMalloc(RGB_LED_COLOR_HISTORY * sizeof(uint8_t));
  for(uint8_t j = 0; j < RGB_LED_COLOR_HISTORY; j++){
    red[j] = 0;
    green[j] = 0;
    blue[j] = 0;
  }
}


void RGBLed::setColor(int r, int g, int b){
  if(!isPowerKeyOffVar){
    isOn = true;
    red[i] = r;
    green[i] = g;
    blue[i] = b;
   
    ledcWrite(CHANNEL_RED, r);
    ledcWrite(CHANNEL_GREEN, g);
    ledcWrite(CHANNEL_BLUE, b);
  
    i = (i + 1) % RGB_LED_COLOR_HISTORY;
  }
}

void RGBLed::setColorWithFading(int r, int g, int b){
  if(!isRunningFadingTask_){
    isRunningFadingTask_ = true;
    uint8_t curI = (RGB_LED_COLOR_HISTORY + i - 1) % RGB_LED_COLOR_HISTORY;
    uint8_t curRed = red[curI];
    uint8_t curGreen = green[curI];
    uint8_t curBlue = blue[curI];
    
    //RGB_LED_TRANSITION_EFFECT_TIME
    for(uint8_t j = 0; j < 255 && isOn; j++){
      if(curRed > r) curRed--;
      else if(curRed < r) curRed++;
  
      if(curGreen > g) curGreen--;
      else if(curGreen < g) curGreen++;
  
      if(curBlue > b) curBlue--;
      else if(curBlue < b) curBlue++;
      
      setColor(curRed, curGreen, curBlue);
      vTaskDelay(rgbLedTransitionEffectTime/ portTICK_PERIOD_MS);
    }
    isRunningFadingTask_ = false;
  }
}

void RGBLed::setRandomColor(){
  if(!isPowerKeyOff()){   
    isOn = true;
    setColor(random(256), random(256), random(256));
  }
}

void RGBLed::setRandomColorWithTransitionEffect(){
  if(!isPowerKeyOff()){   
    isOn = true;
    uint8_t newRed = random(256);
    uint8_t newGreen = random(256);
    uint8_t newBlue = random(256);
    setColorWithFading(newRed, newGreen, newBlue);
  }
}


void RGBLed::setNextColor() {  
  if(!isPowerKeyOffVar){
    if(prevColorCount > 0){
      prevColorCount--;    
      if(nextTransitionMode == Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION_TYPE_1){
        setColor(red[i], green[i], blue[i]);
      }else{
        setColorWithFading(red[i], green[i], blue[i]);
      }
    }else{
      if(nextTransitionMode == Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION_TYPE_1){
        setRandomColor();
      }else{
        setRandomColorWithTransitionEffect();
      }
    }
    isOn = true;
  }
}

void RGBLed::setPrevColor() {
  if(!isPowerKeyOffVar){
    if(prevColorCount < RGB_LED_COLOR_HISTORY - 1){
      prevColorCount++;
      i = (RGB_LED_COLOR_HISTORY + i - 2) % RGB_LED_COLOR_HISTORY;
      if(nextTransitionMode == Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION_TYPE_1){
        setColor(red[i], green[i], blue[i]);
      }else{
        setColorWithFading(red[i], green[i], blue[i]);
      }
    }
    isOn = true;
  }
}

void RGBLed::off(){
  if(!isPowerKeyOffVar){
    setColor(0, 0, 0);
    isOn = false;
  }
}

void RGBLed::onOffToggle(){
  if(!isPowerKeyOffVar){
    isOn = !isOn;
    if(isOn){
      i = (RGB_LED_COLOR_HISTORY + i - 1) % RGB_LED_COLOR_HISTORY;
      setColor(red[i], green[i], blue[i]);
    }else{
      ledcWrite(CHANNEL_RED, 0);
      ledcWrite(CHANNEL_GREEN, 0);
      ledcWrite(CHANNEL_BLUE, 0);
    }
  }
}

void RGBLed::powerKeyPressed(){
  isPowerKeyOffVar = !isPowerKeyOffVar;
  if(isPowerKeyOffVar){
    ledcWrite(CHANNEL_RED, 0);
    ledcWrite(CHANNEL_GREEN, 0);
    ledcWrite(CHANNEL_BLUE, 0);
  }else{
    if(isOn){
      i = (RGB_LED_COLOR_HISTORY + i - 1) % RGB_LED_COLOR_HISTORY;
      setColor(red[i], green[i], blue[i]);
    }
  }
}

bool RGBLed::isPowerKeyOff(){
  return isPowerKeyOffVar;
}

uint16_t RGBLed::getLedAnimationInterval(){
  return ledAnimationInterval;
}

void RGBLed::setLedAnimationInterval(uint16_t t){
  if(!isPowerKeyOffVar){
    ledAnimationInterval = t;
  }
}

void RGBLed::incrementLedAnimationIntervalInSecond(uint8_t t){
  if(!isPowerKeyOffVar){
    ledAnimationInterval += t * 1000;  
  }
}

void RGBLed::decrementLedAnimationIntervalInSecond(uint8_t t){
  if(!isPowerKeyOffVar){
    if(ledAnimationInterval - t * 1000 > 0){
      ledAnimationInterval -= t * 1000;
    }
  }
}

void RGBLed::setRgbLedTransitionEffectTime(uint16_t t){
  rgbLedTransitionEffectTime = t;
}

uint16_t RGBLed::getRgbLedTransitionEffectTime(){
  return rgbLedTransitionEffectTime;
}

void RGBLed::setNextTransitionMode(uint8_t mode){
  nextTransitionMode = mode;
}

uint8_t RGBLed::getNextTransitionMode(){
   return nextTransitionMode;
}

bool RGBLed::isRunningFadingTask(){
  return isRunningFadingTask_;
}
void  RGBLed::setRunningFadingTask(bool flag){
  isRunningFadingTask_ = flag;
}
