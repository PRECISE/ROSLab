
#ifndef INCL_ROBOT_FUNCTIONS
#define INCL_ROBOT_FUNCTIONS

#include "arduino.h"
#include "robot_comm.h"
#include "OneWireSerial.h"

// The following relate to robot configuration - values are auto-generated
#define `NUM_CHIPS` @3@
#define `NUM_CHAINS` @1@

#define `BT_TX` @11@
#define `BT_RX` @10@

#define `PINS_FOR_BT` @"{Pin 0<A_O>: Analog LED (Shoulder)\nPin 1<SERVO>: Joint Servo (Shoulder)\nPin 2<A_O>: Analog LED (Shoulder)\nPin 3<A_O>: Analog LED (Elbow)\nPin 4<SERVO>: Joint Servo (Elbow)\nPin 5<A_O>: Analog LED (Elbow)\nPin 6<A_O>: Analog LED\nPin 7<SERVO>: Gripper Servo\nPin 8<A_O>: Analog LED\n}"@

#if BT_TX >= 0 || BT_RX >= 0
  #define BLUETOOTH
#else
  #undef BLUETOOTH
#endif

#define MAX_JOINT_ANGLE 120
#define MIN_JOINT_ANGLE -120
#define OPEN_GRIPPER_ANGLE 120
#define CLOSE_GRIPPER_ANGLE -120

#define MODULE 10
#define DEVICE 11

/************************************************************************/
// Define a class for interfacing with the robot
/************************************************************************/
class Robot 
{	
private:
  // Declare private variables
  int config_error;  // Whether configuration failed
  int haveErrorLED;
  int errorLED;
  int receivedBytes;
  int curChainPin;
  int inData;
  unsigned long startTime;
  int* motorOffsets;
  // Declare private methods
  int getChainIndex(int pinIndex);
  int sendCommandToPin(int pinIndex, int command1);
  int sendCommandsToPin(int pinIndex, int command1, int command2, int sendBoth);
  void setCurrentChain(int inChain);
  boolean parseBluetoothCommand();
  void sendToBluetooth(char data);
  void getMotorOffsets();

public:	
  // Public variables
  static boolean serialDebug;
  char bluetoothCommandName[10];
  char bluetoothCommandArg1[5];
  char bluetoothCommandArg2[5];

  // Public methods
  Robot();
  int robotSetup();
  void setErrorLEDPin(int inErrorLEDPin);
  int digitalReadR(int pinIndex);
  int digitalWriteR(int pinIndex, int outputValue);
  int analogReadR(int pinIndex);
  int analogWriteR(int pinIndex, int duty);

  void bluetoothSetup();
  boolean isBluetoothConnected();
  boolean getBluetoothCommand();
  boolean getBluetoothCommand(long timeout);
  void sendToBluetooth(const char* data);
  boolean executeBluetoothCommand();

  void setJointPins(int inPins[], int inNumJoints);
  void setGripperPin(int inPin);
  void setJointAngle(int jointNum, int angle);
  void actuateGripper();
  void openGripper();
  void closeGripper();
  void setMotorPins(int inPins[], int inNumMotors);\
  void calibrateMotors();
  void setMotorSpeed(int motor, int speed);
};

#endif

