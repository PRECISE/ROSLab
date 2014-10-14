#include "robot_functions.h"
#include "helper_functions.h"
#include <EEPROM.h>
#include <Servo.h>

// Initialize variables which are auto-generated
int `chainPins`[NUM_CHAINS] = @{12}@;
int `chainTypes`[NUM_CHAINS] = @{MODULE}@;
int `numChips`[NUM_CHAINS] = @{3}@;
int `pinTypes`[NUM_CHIPS][3] = @{
	{A_O, A_O, A_O}, {A_O, A_O, A_O}, {A_O, A_O, A_O}}@;
int `jointPinsAuto`[] = @{1, 4}@;
int `motorPinsAuto`[] = @{1, 4}@;
int `gripperPin` = @7@;

// Other initialization variables
int* jointPins = jointPinsAuto;
int numJoints = sizeof(jointPinsAuto)/sizeof(int);
Servo* jointServos = new Servo[numJoints];
boolean gripperClosed = false;
int* motorPins = motorPinsAuto;
int numMotors = sizeof(motorPinsAuto)/sizeof(int);
Servo* motorServos = new Servo[numMotors];

OneWireSerial slave(55); // Declare with dummy pin for now (will be reset by setcurrentchain when actually used)
#ifdef BLUETOOTH
  OneWireSerial btSerial(BT_TX, BT_RX, false);
#endif

/************************************************************************/
// Constructor for Robot class
// Configures all chips on robot
/************************************************************************/
Robot::Robot()
{
  haveErrorLED = 0;
}

/************************************************************************/
// Uses given pin as an error LED indicator 
//	Unless pin is specified as a chain pin - then it is assumed it is a chain not an LED
/************************************************************************/
void Robot::setErrorLEDPin(int inErrorLEDPin)
{
  int goodLEDPin = 1;
  for(unsigned int nextPin = 0; nextPin < NUM_CHAINS && goodLEDPin; nextPin++)
    if(chainPins[nextPin] == inErrorLEDPin)
      goodLEDPin = 0;
  if(goodLEDPin)
  {
    haveErrorLED = 1;
    errorLED = inErrorLEDPin;
  }
  else
    haveErrorLED = 0;
}

/************************************************************************/
// Send configuration commands to all chips to set up their pin modes
/************************************************************************/
int Robot::robotSetup()
{        
  debugSerialBegin();
  debugPrintln("Starting robot setup");
  delay(100);
  // Initialize variables
  inData = 0;
  receivedBytes = 0;

  // Set error LED pin if there is one
  if(haveErrorLED)
  {
    pinMode(errorLED, OUTPUT);
    digitalWrite(errorLED, LOW);
  }
  // Keep track of attempts (in case of error on a chain)
  int numAttempts = 0;

  // Perform configuration setting/checking on chips
  int totalChipNum = 0;      // Will be index into first dimension of pinTypes array
  int inDataArr[NUM_CHIPS];  // Array of data we will get back from chips
  int pinType = 0;           // The type of the next pin we are configuring
  int toSend[NUM_CHIPS];     // Array of configurations to send (store them to check they come back correctly)
  // Could repeatedly use sendCommandToPin, but this writes commands in batch instead (faster)
  // Process one chain of chips at a time
  for(unsigned int nextChain = 0; nextChain < NUM_CHAINS && numAttempts < MAX_ATTEMPTS; nextChain++)
  {
	if(chainTypes[nextChain] == DEVICE)
	{
	  pinType = pinTypes[totalChipNum][0];
	  int pinNum = chainPins[nextChain];
	  debugPrint("Set pin "); debugPrint(pinNum); debugPrint(" as "); 
	  switch(pinType)
	  {
        case D_I: pinMode(pinNum, INPUT); debugPrintln("D_I"); break;
		case D_O: pinMode(pinNum, OUTPUT); digitalWrite(pinNum, LOW); debugPrintln("D_O"); break;
		case A_I: debugPrintln("A_I"); break;
		case A_O: pinMode(pinNum, OUTPUT); analogWrite(pinNum, 0); debugPrintln("A_O"); break;
		default: debugPrintln("UNKNOWN"); break;
	  }
	  totalChipNum++;
	}
	else
	{
		numAttempts++;
		// Set data direction on this chain's pin
		setCurrentChain(nextChain);
		// Send commands down the line
		//  Each configuration byte uses 2 bits per pin type (so pin0 is specified by 2 lowest bits, etc.)
		for(unsigned int nextChip = 0; nextChip < numChips[nextChain]; nextChip++)
		{
		  toSend[nextChip] = 0;
		  for(unsigned int pin = 0; pin < 3; pin++)
		  {
			pinType = pinTypes[totalChipNum][pin];
			if(pinType == N_C)
			  toSend[nextChip] |= (D_I << (pin*2));
			else
			  toSend[nextChip] |= (pinType << (pin*2));
		  }
		  // Send command (two-byte command)
		  slave.write(CMND_CONFIG);
		  slave.write(toSend[nextChip]);
		  debugPrint("\tSending: "); debugPrintln(toSend[nextChip]);
		  totalChipNum++;
		}
		// Send NULL command to terminate
		slave.write(CMND_NULL);
		// Get data back
		unsigned int allCorrect = 1;
		receivedBytes = 0;
		unsigned long startTime = 0;
		digitalWrite(errorLED, HIGH);
		for(unsigned int i = 0; i < numChips[nextChain] && allCorrect; i++)
		{
		  startTime = millis();
		  while(!slave.available() && millis() - startTime < 2000);
		  if(slave.available())
		  {
			inDataArr[receivedBytes++] = slave.read();
			debugPrint("\tFrom chip: "); debugPrintln(inDataArr[receivedBytes-1]);
		  }
		  else
		  {
			debugPrintln("\tTimeout!");
			allCorrect = 0;
		  }
		}
		digitalWrite(errorLED, LOW);
		// Check that they were all received correctly
		//  Note that inDataArr is in reverse order from toSend
		for(unsigned int i = 0; i < numChips[nextChain] && allCorrect; i++)
		  if(inDataArr[numChips[nextChain] - 1 - i] != toSend[i])
			allCorrect = 0;
		// If there was a mistake, re-send everything to this chain
		// TODO: Only re-send to chips that had a problem?
		if(!allCorrect)
		{
		  totalChipNum -= numChips[nextChain];
		  nextChain--;
		}
		else
		  numAttempts = 0;
		debugPrintln();
	  }
  }

  // Make sure everything has settled on slave chips regarding configuration commands
  // Probably not necessary, just feels nice for now
  delay(1);

  // Check whether configuration was successful
  config_error = (numAttempts == MAX_ATTEMPTS);
  // Set errorLED if appropriate
  if(haveErrorLED && config_error)
    digitalWrite(errorLED, HIGH);
  else if (haveErrorLED)
    digitalWrite(errorLED, LOW);

  // Set up bluetooth if defined
  #ifdef BLUETOOTH
  bluetoothSetup();
  #endif
  // Set up motors and joints
  getMotorOffsets();
  for(int i = 0; i < numMotors; i++)
  {
    if(chainTypes[getChainIndex(motorPins[i])] == DEVICE)
      motorServos[i].attach(chainPins[getChainIndex(motorPins[i])]);
    setMotorSpeed(i, 0);
  }
  for(int i = 0; i < numJoints; i++)
  {
    if(chainTypes[getChainIndex(jointPins[i])] == DEVICE)
      jointServos[i].attach(chainPins[getChainIndex(jointPins[i])]);
    setJointAngle(i, 0);
  }

  debugPrint("Robot setup "); debugPrintln(config_error == 1 ? "failed!" : "succeeded!");
  delay(100); // Some tests with serial printing seemed to freeze up without this
  return (config_error == 1 ? 0 : 1);
}

/************************************************************************/
// Reads the value of requested digital input pin
// pinIndex: index of pin to read
// Returns LOW if requested pin is not correct type or is out of bounds, HIGH or LOW otherwise
// If there was an error and haveLED is set, sets errorLED and delays 50 ms before returning
/************************************************************************/
int Robot::digitalReadR(int pinIndex)
{
  // Send the command
  int success = sendCommandToPin(pinIndex, CMND_GET_DATA);
  // Return the data
  return (success == -1 ? -1 : inData);
}

/************************************************************************/
// Set a digital output pin to output HIGH or LOW
// pinIndex: index of pin to control
// outputVal: either HIGH or LOW
// Returns 0 if requested pin is not correct type or is out of bounds, 1 otherwise
// If there was an error and haveLED is set, sets errorLED and delays 50 ms before returning
/************************************************************************/
int Robot::digitalWriteR(int pinIndex, int outputValue)
{
  return sendCommandToPin(pinIndex, outputValue == HIGH ? CMND_SET_HIGH : CMND_SET_LOW);
}

/************************************************************************/
// Reads the value of requested analog input pin
// pinIndex: index of pin to read
// Returns LOW if requested pin is not correct type or is out of bounds, the data otherwise
// If there was an error and haveLED is set, sets errorLED and delays 50 ms before returning
/************************************************************************/
int Robot::analogReadR(int pinIndex)
{
  // Send the command
  int success = sendCommandToPin(pinIndex, CMND_GET_DATA);
  // Return the data
  return (success == -1 ? -1 : inData);
}

/************************************************************************/
// Set a PWM output pin to desired duty cycle
// pinIndex: index of pin to control
// duty: a number from 0 (0% duty) to 255 (100% duty)
// Returns 0 if requested pin is not correct type or is out of bounds, 1 otherwise
// If there was an error and haveLED is set, sets errorLED and delays 50 ms before returning
/************************************************************************/
int Robot::analogWriteR(int pinIndex, int duty)
{
  return sendCommandsToPin(pinIndex, CMND_SET_PWM, duty, 1);
}

/************************************************************************/
// Sends a command to a particular pin (determines which chain/chip is requested)
// Works with any command that is sent in format (pin << 4 | command)
// Waits for data to come back if any is expected
// All other chips on that chain receive NOOP commands
// pinIndex: index of pin to control
// Returns 0 if requested pin is not correct type or is out of bounds, 1 otherwise
/************************************************************************/
int Robot::sendCommandToPin(int pinIndex, int command1)
{
  return sendCommandsToPin(pinIndex, command1, CMND_NOOP, 0);
}

/************************************************************************/
// Sends two commands to a particular virtual pin (determines which chain/chip is requested)
// First command is sent in format (pin << 4 | command)
// Second command will not be sent at all if sendBoth is 0
// Works with PWM-setting commands - set_pwm should be command1, and duty should be command2
// Waits for data to come back if any is expected
// All upstream chips on that chain receive NOOP commands
// pinIndex: index of pin to control
// Returns 0 if requested pin is not correct type or is out of bounds, or if config_error is set, 1 otherwise
// If there was an error and haveLED is set, sets errorLED and delays 50 ms before returning
/************************************************************************/
int Robot::sendCommandsToPin(int pinIndex, int command1, int command2, int sendBoth)
{
  // If chips were not configured, don't even try to communicate
  if(config_error)
    return -1;
  // Determine which chain, chip, and pin the desired virtual pin refers to
  int pinIndexTemp = -1;
  int chip = 0;
  int pin = 0;
  int chain = 0;
  int chipNum = 0;	// Overall chip index (index into pinTypes array)
  // Step through pinTypes array until desired index is found
  for(chain = 0; chain < NUM_CHAINS && pinIndexTemp < pinIndex; chain++)
  {
    for(chip = 0; chip < numChips[chain] && pinIndexTemp < pinIndex; chip++)
    {
      for(pin = 0; pin < 3 && pinIndexTemp < pinIndex; pin++)
      {
        // If this pin is connected to something, we found another virtual pin
        if(pinTypes[chipNum][pin] != N_C)
          pinIndexTemp++;
      }
      chipNum++;
    }
  }
  // chipnum, chain, chip, and pin are now 1 higher than they should be
  pin--;
  chip--;
  chain--;
  chipNum--;

  // If dealing with Arduino analog input pin, do that now and exit
  if(pinIndexTemp < pinIndex && command1 == CMND_GET_DATA)
  {
	  int pinNum = 0;
	  while(pinIndexTemp + pinNum + 1 < pinIndex)
		  pinNum++;
	  inData = analogRead(pinNum)/4;
	  return 1;
  }
  // If it was an invalid pin, exit
  if(pinIndex < 0 || pinIndexTemp < pinIndex)
    return -1;

  // If pin is not correct type, return 0
  if(command1 == CMND_SET_HIGH && pinTypes[chipNum][pin] != D_O) return -1;
  if(command1 == CMND_SET_LOW && pinTypes[chipNum][pin] != D_O) return -1;
  if(command1 == CMND_SET_PWM && pinTypes[chipNum][pin] != A_O) return -1;
  if(command1 == CMND_GET_DATA && !(pinTypes[chipNum][pin] == A_I || pinTypes[chipNum][pin] == D_I)) return -1;

  if(chainTypes[chain] == DEVICE)
  {
	  int pinNum = chainPins[chain];
	  switch(command1)
	  {
		  case CMND_SET_HIGH: digitalWrite(pinNum, HIGH); break;
		  case CMND_SET_LOW: digitalWrite(pinNum, LOW); break;
		  case CMND_SET_PWM: analogWrite(pinNum, command2); break;
		  case CMND_GET_DATA: inData = (digitalRead(pinNum) == HIGH ? 255 : 0); break;
	  }
	  return 1;
  }
  // Set data chain
  setCurrentChain(chain);
  // Send NOOP to all chips upstream of our chip
  for(int nextChip = chip; nextChip > 0; nextChip--)
    slave.write(CMND_NOOP);
  // Send  commands to our chip
  slave.write((pin << 4) | command1);
  if(sendBoth)
    slave.write(command2);
  // Send NULL command to terminate
  slave.write(CMND_NULL);

  // If we expect any data to come back, wait for it
  int error = 0;
  if(command1 != CMND_NOOP)
  {
    unsigned long startTime = millis();
    while(!slave.available() && millis() - startTime < 1000);
    if(slave.available())
    {
    	inData = slave.read();
	//debugPrint("\tGot data: "); debugPrintln(inData);
    }
    else
    {
    	error = 1;
	debugPrintln("\tTimeout waiting for chip data");
    }
  }
  // TODO: Also look to whether we got RETURN_SUCCESS or RETURN_ERROR ?
  //	Note this only applies if command was not a data request since in that case data may happen to be one of these indicators

  // Set/clear error LED if using one
  if(haveErrorLED)
  {
    if(error)
    {
      digitalWrite(errorLED, HIGH);
      delay(50);
    }
    else
      digitalWrite(errorLED, LOW);
  } 
  // Return whether there was success (note if we got data, inData is now updated with data)
  return (error == 1 ? -1 : 1);
}

/************************************************************************/
// Sets the current chain to use
// inChain: index of chain to use
// dir: DOWN or UP
// Sets the data direction, as well as the variable curChainPin (used in Interrupt routine)
/************************************************************************/
void Robot::setCurrentChain(int inChain)
{
  curChainPin = chainPins[inChain];
  slave.end();
  slave = OneWireSerial(curChainPin);
  slave.begin(BAUD_RATE);
}

/************************************************************************/
// Sets the given joint to the given angle
// jointNum is the index of the joint (not a virtual pin number)
/************************************************************************/
void Robot::setJointAngle(int jointNum, int angle)
{
  if(jointNum < 0 || jointNum > numJoints)
    return;
  if(angle < MIN_JOINT_ANGLE)
    angle = MIN_JOINT_ANGLE;
  if(angle > MAX_JOINT_ANGLE)
    angle = MAX_JOINT_ANGLE;

  int duty = servoAngleToDuty(angle);
  if(duty > 255)
    duty = 255;
  if(duty < 0)
    duty = 0;
    
  if(chainTypes[getChainIndex(jointPins[jointNum])] == MODULE)
  {
    analogWriteR(jointPins[jointNum], duty);
  }
  else
  {
    jointServos[jointNum].write(angle);
  }
}

/************************************************************************/
// Actuates the gripper (toggles whether it is open/closed)
/************************************************************************/
void Robot::actuateGripper()
{
  if(gripperClosed)
    openGripper();
  else
    closeGripper();
}

/************************************************************************/
// Opens the gripper
/************************************************************************/
void Robot::openGripper()
{
  int pinNum = gripperPin;
  int angle = OPEN_GRIPPER_ANGLE;
  int duty = servoAngleToDuty(angle);
    
  if(duty <= 255 && duty >= 0)
  { 
    int success = -1;
    for(int i = 0; i < 5 && success == -1; i++)
    {
      success = analogWriteR(pinNum, duty);
      delay(10);
    }
    if(success == 1)
      gripperClosed = false;
  }
}

/************************************************************************/
// Closes the gripper
/************************************************************************/
void Robot::closeGripper()
{
  int pinNum = gripperPin;
  int angle = CLOSE_GRIPPER_ANGLE;
  int duty = servoAngleToDuty(angle);
    
  if(duty <= 255 && duty >= 0)
  { 
    int success = -1;
    for(int i = 0; i < 5 && success == -1; i++)
    {
      success = analogWriteR(pinNum, duty);
      delay(10);
    }
    if(success == 1)
      gripperClosed = true;
  }
}

/************************************************************************/
// Sets up the robot's bluetooth if it has been defined
/************************************************************************/
void Robot::bluetoothSetup()
{
  #ifdef BLUETOOTH
  pinMode(BT_TX, OUTPUT); // BT TX pin (RX on arduino)
  pinMode(BT_RX, OUTPUT); // BT RX pin (TX on arduino)
  btSerial.begin(9600);
  #else
  debugPrintln("WARNING: BLUETOOTH METHOD CALLED BUT NO BLUETOOTH WAS CONNECTED");
  #endif
}

/************************************************************************/
// Checks if the bluetooth module is connected to a device
/************************************************************************/
boolean Robot::isBluetoothConnected()
{
  #ifdef BLUETOOTH
  boolean isConnected = true;
  btSerial.listen();
  sendToBluetooth("AT");
  startTime = millis();
  while(!btSerial.available() && millis() - startTime < 500)
  {
    delay(10);
  }
  while(btSerial.available())
  {
    isConnected = false;
    btSerial.read();
    delay(15);
  }
  return isConnected;
  #else
  debugPrintln("WARNING: BLUETOOTH METHOD CALLED BUT NO BLUETOOTH WAS CONNECTED");
  return false;
  #endif
}

/************************************************************************/
// Waits indefinitely for bluetooth data
/************************************************************************/
boolean Robot::getBluetoothCommand()
{
  #ifdef BLUETOOTH
  debugPrintln("Waiting for Bluetooth Command");
  btSerial.listen();
  while(!btSerial.available());
  return parseBluetoothCommand();
  #else
  debugPrintln("WARNING: BLUETOOTH METHOD CALLED BUT NO BLUETOOTH WAS CONNECTED");
  #endif
}

/************************************************************************/
// Waits until bluetooth data is received or timeout is reached
/************************************************************************/
boolean Robot::getBluetoothCommand(long timeout)
{
  #ifdef BLUETOOTH
  startTime = millis();
  btSerial.listen();
  while(!btSerial.available() && millis() - startTime < timeout);
  if(!btSerial.available())
    return false;
  return parseBluetoothCommand();
  #else
  debugPrintln("WARNING: BLUETOOTH METHOD CALLED BUT NO BLUETOOTH WAS CONNECTED");
  return false;
  #endif
}

/************************************************************************/
// Sends the character array over bluetooth
/************************************************************************/
void Robot::sendToBluetooth(const char* data)
{
  #ifdef BLUETOOTH
  for(int i = 0; data[i] != '\0'; i++)
  {
    sendToBluetooth(data[i]);
  }
  #else
  debugPrintln("WARNING: BLUETOOTH METHOD CALLED BUT NO BLUETOOTH WAS CONNECTED");
  #endif
}

/************************************************************************/
// Sends the character over bluetooth
/************************************************************************/
void Robot::sendToBluetooth(char data)
{
  #ifdef BLUETOOTH
  btSerial.write(data);
  delay(BT_SEND_DELAY);
  #else
  debugPrintln("WARNING: BLUETOOTH METHOD CALLED BUT NO BLUETOOTH WAS CONNECTED");
  #endif
}

/************************************************************************/
// Gets a command from bluetooth (returns immediately if not available)
/************************************************************************/
boolean Robot::parseBluetoothCommand()
{
  #ifdef BLUETOOTH
  btSerial.listen();
  if(!btSerial.available())
    return false;
  char buffer[30];
  buffer[0] = '\0';
  bluetoothCommandName[0] = '\0';
  bluetoothCommandArg1[0] = '\0';
  bluetoothCommandArg2[0] = '\0';
    
  // Wait for start of a command or for timeout
  startTime = millis();
  boolean startedCommand = false;
  while(!startedCommand && (millis() - startTime <= BT_TIMEOUT_START_COMMAND))
  {
    char temp = btSerial.read();
    startedCommand = (temp == '{');
  }
  if(!startedCommand)
  {
    debugPrintln("Didn't start command!");
    return false;
  }
    
  // Read until end of command or timeout
  int count = 0;
  int next = '\0';
  boolean finishedCommand = false;
  startTime = millis();
  while(!finishedCommand && (millis() - startTime <= BT_TIMEOUT_GET_COMMAND))
  {
    next = btSerial.read();
    if(next != -1)
    {
      buffer[count] = (char)next;
      //debugPrint("got "); debugPrintln(buffer[count]);
      finishedCommand = (buffer[count] == '}');
      count++;
      startTime = millis();
    }
  }
  count--; // Don't include '}' in return string
  if(!finishedCommand)
  {
    debugPrintln("Didn't finish command!");
    return false;
  }
  
  char command[count+1];
  for(int i = 0; i < count; i++)
    command[i] = buffer[i];
  command[count] = '\0';
  
  //debugPrint("Got command: "); debugPrintln(command);    
  if(indexOf(command, "$") >= 0)
  {
    substring(command, 0, indexOf(command, "$"), bluetoothCommandName);
    substring(command, indexOf(command, "$") + 1, command);
    if(indexOf(command, "$") < 0)
      substring(command, 0, bluetoothCommandArg1);
  }
  else
  {
    substring(command, 0, bluetoothCommandName);
  }
  
  if(indexOf(command, "$") >=0)
  {
    substring(command, 0, indexOf(command, "$"), bluetoothCommandArg1);
    substring(command, indexOf(command, "$") + 1, command);
    substring(command, 0, bluetoothCommandArg2);
  }  
  
  debugPrint("\tGot bluetooth command: "); 
  debugPrint(bluetoothCommandName); 
  debugPrint(":"); debugPrint(bluetoothCommandArg1); 
  debugPrint(":"); debugPrintln(bluetoothCommandArg2);
  return true;
  #else
  bluetoothCommandName[0] = '\0';
  bluetoothCommandArg1[0] = '\0';
  bluetoothCommandArg2[0] = '\0';
  debugPrintln("WARNING: BLUETOOTH METHOD CALLED BUT NO BLUETOOTH WAS CONNECTED");
  return false;
  #endif
}


/************************************************************************/
// Tries to execute the most recent bluetooth command (retrieved using getBluetoothCommand)
// Returns False if command is unknown
/************************************************************************/
boolean Robot::executeBluetoothCommand()
{
  #ifdef BLUETOOTH
  if(equals(bluetoothCommandName, "GET_PINS"))
  {
    sendToBluetooth(PINS_FOR_BT);
    debugPrintln("\tSent pin descriptions over Bluetooth");
  }
  else if(equals(bluetoothCommandName, "GET_D_I"))
  {
    int pinNum = atof(bluetoothCommandArg1);     
    int data = digitalReadR(pinNum);
    for(int i = 0; i < 20 && data == -1; i++)
    {
      data = digitalReadR(pinNum);
      delay(5);
    }
    char toSend[10];
    toSend[0] = '{';
    toSend[1] = '\0';
    concatInt(toSend, pinNum, toSend, 10);
    concat(toSend, ":", toSend, 10);
    concat(toSend, (data == 1 ? "HIGH" : (data == 0 ? "LOW" : "?")), toSend, 10);
    concat(toSend, "}", toSend, 10);
    sendToBluetooth(toSend);
    debugPrint("\tSent: "); debugPrintln(toSend);
  }
  else if(equals(bluetoothCommandName, "GET_A_I"))
  {
    int pinNum = atof(bluetoothCommandArg1); 
    debugPrint("  pin "); debugPrint(pinNum);
    
    int data = analogReadR(pinNum);
    for(int i = 0; i < 10 && data == -1; i++)
    {
      data = analogReadR(pinNum);
      delay(1);
    }
    
    char toSend[10];
    toSend[0] = '{';
    toSend[1] = '\0';
    concatInt(toSend, pinNum, toSend, 10);
    concat(toSend, ":", toSend, 10);
    if(data == -1)
      concat(toSend, "?", toSend, 10);
    else
      concatInt(toSend, data, toSend, 10);
    concat(toSend, "}", toSend, 10);
    sendToBluetooth(toSend);
    debugPrint("\tSent: "); debugPrintln(toSend);
  }
  else if(equals(bluetoothCommandName, "SET_D_O"))
  {
    int pinNum = atof(bluetoothCommandArg1);
    int success = digitalWriteR(pinNum, equals(bluetoothCommandArg2, "HIGH") ? HIGH : LOW);
    for(int i = 0; i < 15 && success == -1; i++)
    {
      success = digitalWriteR(pinNum, equals(bluetoothCommandArg2, "HIGH") ? HIGH : LOW);
      delay(10);
    }
    sendToBluetooth("{OK}");
  }
  else if(equals(bluetoothCommandName, "SET_PWM"))
  {      
    int pinNum = atof(bluetoothCommandArg1);
    int duty = atof(bluetoothCommandArg2);
    int success = -1;
    for(int i = 0; i < 5 && success == -1; i++)
    {
      success = analogWriteR(pinNum, duty);
    }
    sendToBluetooth("{OK}");
  }
  else if(equals(bluetoothCommandName, "SET_SERVO"))
  {
    int pinNum = atof(bluetoothCommandArg1);
    int angle = atof(bluetoothCommandArg2) - 100;
    int duty = servoAngleToDuty(angle);
    
    if(duty <= 255 && duty >= 0)
    { 
      int success = -1;
      for(int i = 0; i < 5 && success == -1; i++)
      {
        success = analogWriteR(pinNum, duty);
        delay(10);
      }
    }
    sendToBluetooth("{OK}");
  }
  else if(equals(bluetoothCommandName, "JOINT"))
  {
    int jointNum = atof(bluetoothCommandArg1);
    int angle = -atof(bluetoothCommandArg2);
    setJointAngle(jointNum, angle);
    sendToBluetooth("{OK}");
  }
  else if(equals(bluetoothCommandName, "GRIPPER"))
  {
    actuateGripper();
    sendToBluetooth("{OK}");
  }
  else
  {
    return false;
  }
  return true;
  #else
  debugPrintln("WARNING: BLUETOOTH METHOD CALLED BUT NO BLUETOOTH WAS CONNECTED");
  return false;
  #endif
}


/************************************************************************/
// Sets which virtual pins are used as joints
// Overrides any auto-generated values
/************************************************************************/
void Robot::setJointPins(int inPins[], int inNumJoints)
{
  int newJointPins[inNumJoints];
  for(int i = 0; i < inNumJoints; i++)
  {
    newJointPins[i] = inPins[i];
  }
  jointPins = newJointPins;
  numJoints = inNumJoints;  
}

/************************************************************************/
// Sets which virtual pin is used as gripper
// Overrides any auto-generated value
/************************************************************************/
void Robot::setGripperPin(int inPin)
{
  gripperPin = inPin;
}

/************************************************************************/
// Sets which virtual pins are used as motors
// Overrides any auto-generated values
/************************************************************************/
void Robot::setMotorPins(int inPins[], int inNumMotors)
{
  for(int i = 0; i < numMotors; i++)
  {
    if(motorServos[i].attached())
      motorServos[i].detach();
  }

  int newMotorPins[inNumMotors];
  motorServos = new Servo[inNumMotors];
  for(int i = 0; i < inNumMotors; i++)
  {
    newMotorPins[i] = inPins[i];
    motorServos[i].attach(chainPins[getChainIndex(inPins[i])]);
  }
  motorPins = newMotorPins;
  numMotors = inNumMotors;  
  getMotorOffsets();
}

/************************************************************************/
// Sets the motor to the given speed
/************************************************************************/
void Robot::setMotorSpeed(int motor, int speed)
{
  if(speed > 100)
    speed = 100;
  if(speed < -100)
    speed = -100;
  if(motor < 0 || motor >= numMotors)
    return;
  int angle = (speed * 90) / 100;
  angle += motorOffsets[motor];
  if(angle > 90)
    angle = 90;
  if(angle < -90)
    angle = -90;
  int pinIndex = motorPins[motor];
  int chain = getChainIndex(pinIndex);
  // Set the motor speed 
  if(chainTypes[chain] == DEVICE)
  {
    motorServos[motor].write(angle + 90);
  }
  else
  {
    analogWriteR(motorPins[motor], servoAngleToDuty(angle));
  }
}

/************************************************************************/
// Calibrates the motors
/************************************************************************/
void Robot::calibrateMotors()
{
  getMotorOffsets();
  int eepromAddress = 0;
  char input = 'x';
  Serial.begin(9600);
  Serial.println("----------------------------");
  Serial.println("Send \'+\' or \'.\' to increase the calibration value");
  Serial.println("Send \'-\' or \',\' to decrease the calibration value");
  Serial.println("Send \'s\' to save the calibration value");
  Serial.println("----------------------------");
  for(int i = 0; i < numMotors; i++)
  {
    Serial.print("Calibrating motor "); Serial.println(i);
    input = 'x';
    while(input != 's')
    {
      Serial.print("\tCalibration value: "); Serial.println(motorOffsets[i]);
      setMotorSpeed(i, 0);
      while(!Serial.available()) {}
      input = Serial.read();
      switch(input)
      {
        case '+':
        case '.': motorOffsets[i]++; break;
        case '-':
        case ',': motorOffsets[i]--; break;
        case 's': eepromWriteInt(2*i, motorOffsets[i]); break;
      }
    }  
    Serial.println("----------------------------");  
  }
  Serial.println("Calibration complete!");
}

void Robot::getMotorOffsets()
{
  motorOffsets = new int[numMotors];
  for(int i = 0; i < numMotors; i++)
  {
    motorOffsets[i] = eepromReadInt(2*i);
    if(motorOffsets[i] > 90 || motorOffsets[i] < -90)
      motorOffsets[i] = 0;
  }
}

int Robot::getChainIndex(int pinIndex)
{
  if(pinIndex < 0)
    return -1;
  int pinIndexTemp = -1;
  int chip = 0;
  int pin = 0;
  int chain = 0;
  int chipNum = 0;	// Overall chip index (index into pinTypes array)
  // Step through pinTypes array until desired index is found
  for(chain = 0; chain < NUM_CHAINS && pinIndexTemp < pinIndex; chain++)
  {
    for(chip = 0; chip < numChips[chain] && pinIndexTemp < pinIndex; chip++)
    {
      for(pin = 0; pin < 3 && pinIndexTemp < pinIndex; pin++)
      {
        // If this pin is connected to something, we found another virtual pin
        if(pinTypes[chipNum][pin] != N_C)
          pinIndexTemp++;
      }
      chipNum++;
    }
  }
  // chipnum, chain, chip, and pin are now 1 higher than they should be
  pin--;
  chip--;
  chain--;
  chipNum--;
  
  if(pinIndexTemp < pinIndex)
    return -1;
  return chain;
}































