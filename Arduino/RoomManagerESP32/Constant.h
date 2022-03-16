#ifndef Constant_H
  
  #define Constant_H

  //PINS
  #define RELAY_0_PIN 26
  #define RELAY_1_PIN 25
  #define RELAY_2_PIN 33
  #define RELAY_3_PIN 32
  #define RELAY_4_PIN  5
  #define RELAY_5_PIN 18
  #define RELAY_6_PIN 19
  #define RELAY_7_PIN 23
  #define ALARM_BUZZER_PIN 2

  #define IR_RECEIVER_PIN 27

  #define PIN_RED   12
  #define PIN_GREEN  13
  #define PIN_BLUE   4
  
  #define CHANNEL_RED    1
  #define CHANNEL_GREEN  2
  #define CHANNEL_BLUE   3

  #define RGB_LED_FREQUENCY 5000
  #define RGB_LED_RESOLUTION 8

  #define RGB_LED_COLOR_HISTORY 6
  #define RGB_LED_TRANSITION_EFFECT_TIME 10
  #define LED_ANIMATION_INTERVAL 2000

  
  
  #define CORE_0_TASK_STACK_SIZE 7000
  #define CORE_1_TASK_STACK_SIZE 7000

  #define UINT_32_T_MAX_VALUE 0xFFFFFFFF
  #define READ_QUEUE_PUSH_WAIT_TIME  3
 


  #define FIREBASE_WRITE_QUEUE_SIZE 5 
  #define DEVICE_CMD_SYNC_TS_ADDRESS "/device/device_1/cmdSyn/ts"
  #define DEVICE_CMD_SYNC_CMD_ADDRESS "/device/device_1/cmdSyn/cmd"
  #define DEVICE_OUTPUT_RES_ADDRESS "/res"
  #define DEVICE_OUTPUT_TS_ADDRESS "/ts"
  
  #define BLUETOOTH_WRITE_QUEUE_SIZE 5
  #define WRITE_COMMAND_SUCCESS_STATUS "t"
  #define WRITE_COMMAND_FAILURE_STATUS "f"
  #define READ_QUEUE_SIZE 20
  #define TIME_SYNC_UP_SERVICE_DELAY 2000
  #define ALARM_LOOKUP_SERVICE_MONITOR_DELAY 1000
  #define RELAY_LOOKUP_SERVICE_MONITOR_DELAY 1000
  #define NO_NETWORK_LISTENER_SERVICE_STOP 5000
  
  #define ROM1 0x57
  #define WIRE_TRANSACTION_DELAY 5

  #define Event_Queue_INPUT_SOURCE_IR_REMOTE 0
  #define Event_Queue_INPUT_SOURCE_BLUETOOTH 1
  #define Event_Queue_INPUT_SOURCE_FIREBASE 2
  
  #define Event_Queue_Handler_TYPE_REMOTE_CODE 0
  #define Event_Queue_Handler_TYPE_RGB_COLOR 1
  #define Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION 2
  #define Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION_TYPE_1 1
  #define Event_Queue_Handler_TYPE_RGB_COLOR_ANIMATION_TYPE_2 2
  
  #define Event_Queue_Handler_TYPE_ALARM_SET 3
  #define Event_Queue_Handler_TYPE_ALARM_RESET 4
  #define Event_Queue_Handler_TYPE_DEVICE_SHARE_CURRENT_ALARM_CONFIG 5
  #define Event_Queue_Handler_TYPE_TIME_SYNC 6
  #define RESPONSE_QUEUE_TYPE_CURRENT_ALARM_SETTINGS_SHARE "RT0:" 
  
  #define Event_Queue_Handler_TYPE_WIFI_SSID_SET 7
  #define Event_Queue_Handler_TYPE_WIFI_PASSWORD_SET 8
  #define Event_Queue_Handler_TYPE_DEVICE_RESET 9
  #define Event_Queue_Handler_TYPE_DEVICE_SHARE_CURRENT_WIFI_SETTINGS 10
  #define Event_Queue_Handler_TYPE_DEVICE_SHARE_CURRENT_WIFI_STATUS 11
  
  #define Event_Queue_Handler_TYPE_SCHEDULE_RELAY_ON_OFF 12
  #define Event_Queue_Handler_TYPE_RESET_SCHEDULE_RELAY 13
  #define Event_Queue_Handler_TYPE_DEVICE_SHARE_CURRENT_RELAY_SCHEDULE 14
  #define RESPONSE_QUEUE_TYPE_CURRENT_RELAY_SCHEDULE_SHARE "RT1:" 
 
#endif
