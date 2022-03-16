
#include "StructType.h"
#include "Util.h"
#include "Logger.h"
#include "Constant.h"

#ifndef MyIRRemote_H

  #define MyIRRemote_H
  
  class MyIRRemote{    
    QueueHandle_t* readQueue = NULL;
    
    uint32_t validRemoteCode[21] = {
    3977412480, 3843719040, 3776872320, 4261511040, 4244799360, 4228087680, 4211376000,
    4177952640, 4194664320, 4161240960, 4144529280, 4127817600, 4111105920, 3827007360,
    3760160640, 4077682560, 4060970880, 4044259200, 4278222720, 4027547520, 3860430720};
    
    public:
      void begin(QueueHandle_t* readQueue_);
      bool remoteCommandRead();
      uint32_t getRemoteCode(uint8_t i);
      
    private:
      bool isVaildRemoteCode(uint32_t code);
   };
   
#endif
