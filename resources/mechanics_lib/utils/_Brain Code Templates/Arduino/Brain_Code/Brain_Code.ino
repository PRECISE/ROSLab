/*
 * Brain Code
 *
 *  Modifed: 01/24/2012 
 *  Author: Joseph DelPreto
 *
 * User can use the following commands on robot's virtual pins:
 *   Robot::digitalWriteR(pin, value)
 *   Robot::digitalReadR(pin)
 *   Robot::analogReadR(pin)
 *   Robot::analogWriteR(pin, duty)
 *
 * NOTE: If compiling yields an error similar to "'Robot' does not name a type", you must import the Robot library:
 *  Select Sketch > Import Library > Add Library, browse into the RobotLibrary folder, and select Open
 *    If "Add Library" is not an option, manually copy the folder RobotLibrary to your Arduino Library Directory (see below for location)
 *      You will then need to re-launch the Arduino software 
 *  After doing this, Arduino will copy those files to your Arduino Library Directory
 *    If you need to edit the library files, make sure to edit the ones in this new location
 *      This location can be found under "Sketchbook location" in File > Preferences
 *      You may (in rare cases) also need to delete the .o files before recompiling after changing a library file
 *        On Windows, these will be in a directory like C:\Users\<User>\AppData\Local\Temp\build<bignumber>.tmp\RobotLibrary 
 *          You can just delete this Temp RobotLibrary folder to be safe
 */ 

#include "robot_functions.h"
#include "helper_functions.h"
#include <EEPROM.h>
#include <Servo.h>

boolean Robot::serialDebug = true; // Set this to false to suppress debug messages

// Declare global variables here 
Robot bot;



/************************************************************************/
// Setup routine (performed once at beginning)
// For example, initialize variables and check/set initial conditions
/************************************************************************/
void setup()
{
  // Use pin 13 as error LED
  //   Note setErrorLEDPin should be called prior to robotSetup to be most useful
  bot.setErrorLEDPin(13);
  // robotSetup MUST be called before using the robot object
  bot.robotSetup();
}

/************************************************************************/
// Main control loop (loops indefinitely)
/************************************************************************/
void loop()
{
  // Insert your code here (example for a robot with Bluetooth is below):
  
  // Note that a call to isBluetoothConnected() takes 500ms to respond true
  // and probably only takes a few ms to respond false
  if(!bot.isBluetoothConnected())
  {
    // Do something here without bluetooth
  }
  else
  {
    // Don't keep checking isBluetoothConnected() since it will now block for 500ms
    while(1) 
    {
      // Do something here with bluetooth
      bot.getBluetoothCommand(); // Wait until command is received and then read it
    
      // Now have parsed Bluetooth command, so the following are updated:
      //   bot.bluetoothCommandName: The name of the command
      //   bot.bluetoothCommandArg1: The first argument of the command
      //   bot.bluetoothCommandArg2: The second argument of the command
      
      // React to various commands 
      //   Robot::executeBluetoothCommand() automatically deals with some
      //   such as writing/reading pins or setting joint angles
      //   It returns true if it was a command it could process
      if(!bot.executeBluetoothCommand()) 
      {
        // Below are commands which are not automatically processed
        //  by the Robot::executeBluetoothCommand() function
        if(equals(bot.bluetoothCommandName, "FORWARD"))
        {
          bot.sendToBluetooth("{OK}");
        }
        else if(equals(bot.bluetoothCommandName, "REVERSE"))
        {
          bot.sendToBluetooth("{OK}");
        }
        else if(equals(bot.bluetoothCommandName, "LEFT"))
        {
          bot.sendToBluetooth("{OK}");
        }
        else if(equals(bot.bluetoothCommandName, "RIGHT"))
        {
          bot.sendToBluetooth("{OK}");
        }
        else if(equals(bot.bluetoothCommandName, "STOP"))
        {
          bot.sendToBluetooth("{OK}");
        }
        else if(equals(bot.bluetoothCommandName, "POS"))
        {
          int arg1 = atof(bot.bluetoothCommandArg1); // X coordinate or radius of touch location
          int arg2 = atof(bot.bluetoothCommandArg2); // Y coordinate or theta of touch location
          bot.sendToBluetooth("{OK}");
        }
      }
    }
  }
}




























