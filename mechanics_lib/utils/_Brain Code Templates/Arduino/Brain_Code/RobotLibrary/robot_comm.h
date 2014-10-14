
#ifndef INCL_COMM
#define INCL_COMM

#include "OneWireSerial.h"
#include "arduino.h"

// Define commands to use
//   Should all fit in first 4 bits (each <= 15)
#define CMND_NULL	(uint8_t)0
#define CMND_NOOP       (uint8_t)1
#define CMND_SET_HIGH	(uint8_t)2  // Upper half of byte should specify which pin
#define CMND_SET_LOW	(uint8_t)3  // Upper half of byte should specify which pin
#define CMND_SET_PWM	(uint8_t)4  // Upper half of byte should specify which pin
			   // Should be followed by a byte that specifies the duty cycle
#define CMND_GET_DATA	(uint8_t)5  // Upper half of byte should specify which pin
#define CMND_CONFIG	(uint8_t)6  // Should be followed by a byte that specifies the type of each pin
#define RETURN_SUCCESS  (uint8_t)7  // Return this to master on success of a command
#define RETURN_ERROR    (uint8_t)8  // Return this to master on failure of a command

// Define pin constants
#define D_O (uint8_t)0   // Digital output
#define D_I (uint8_t)1   // Digital input
#define A_O (uint8_t)2   // Analog output
#define A_I (uint8_t)3   // Analog Input
#define N_C (uint8_t)4	// Not connected (will be set to Digital Input)

#define MAX_ATTEMPTS  20		     // Number of times to retry a failed configuration

#define BAUD_RATE 9600

// Bluetooth timing constants
#define BT_SEND_DELAY 10
#define BT_RECEIVE_DELAY 1
#define BT_TIMEOUT 5000
#define BT_TIMEOUT_START_COMMAND 1000
#define BT_TIMEOUT_GET_COMMAND 500
#endif