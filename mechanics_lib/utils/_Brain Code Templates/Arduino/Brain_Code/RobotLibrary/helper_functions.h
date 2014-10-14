
#ifndef INCL_HELPER_FUNCTIONS
#define INCL_HELPER_FUNCTIONS

#include "arduino.h"
#include "robot_functions.h"
#include <EEPROM.h>

inline int servoAngleToDuty(int angle);

inline void debugSerialBegin();
inline void debugPrint(const char* message);
inline void debugPrintln(const char* message);
inline void debugPrint(int number);
inline void debugPrintln(int number);
inline void debugPrintln();

inline int length(const char* source);
inline int indexOf(char* source, char* target);
inline void substring(char* source, int startIndex, int endIndex, char* dest);
inline void substring(char* source, int startIndex, char* dest);
inline void substring(char* source, int startIndex, int endIndex, char* dest, int destLength);
inline void trim(char* source, int sourceLength);
inline boolean equals(char* first, char* second);
inline boolean equalsIgnoreCase(char* first, char* second);
inline void strcpy(char* source, char* dest, int destLength);
inline void concat(char* first, const char* second, char* dest, int destLength);
inline void concatInt(char* first, int second, char* dest, int destLength);

inline void eepromWriteInt(int address, int value);
inline int eepromReadInt(int address);







/************************************************************************/
/* Convenience method for writing an angle (-90 to 90) to a servo       */
/* Assumes that PWM clock for all outputs is 8MHz/256 */
/************************************************************************/
int servoAngleToDuty(int angle)
{
  // 255 => ~8ms
  // want to map 1.5ms to 0, 1ms to -90 and 2ms to 90
  //int duration = (255 * 15 + 255*5*angle/90)/81;
  
  int contribution = 255*angle;
  return (255 * 15 + (((255*angle)/90)*5))/81;
  
  //double duty = ((double)angle)/180.0 + 1.5;
}


//============================================================================================
// Gets the length of the character array
//============================================================================================
int length(const char* source)
{
  int length = 0;
  for(; source[length] != '\0'; length++);
  return length;
}

//============================================================================================
// Gets the index of the given string, or -1 if not found
//============================================================================================
int indexOf(char* source, char* target)
{
  int targetLength = length(target);
  int sourceLength = length(source);
  int index = -1;
  for(int i = 0; i <= sourceLength - targetLength && index == -1; i++)
  {
    boolean foundTarget = true;
    for(int n = 0; n < targetLength && i+n < sourceLength; n++)
    {
      if(source[i+n] != target[n])
        foundTarget = false;
    }
    if(foundTarget)
      index = i;
  }
  return index;
}

//============================================================================================
// Returns a substring of the character array
// First is inclusive, second is exclusive
// Returns itself if bounds are bad
// Assumes beginning / end if either bound is negative
//============================================================================================
void substring(char* source, int startIndex, int endIndex, char* dest)
{
  substring(source, startIndex, endIndex, dest, 20);
}

//============================================================================================
// Returns a substring of the character array
// First is inclusive, second is exclusive
// Returns itself if bounds are bad
// Assumes beginning / end if either bound is negative
//============================================================================================
void substring(char* source, int startIndex, char* dest)
{
  substring(source, startIndex, length(source), dest, 30);
}

//============================================================================================
// Returns a substring of the character array
// First is inclusive, second is exclusive
// Returns itself if bounds are bad
// Assumes beginning / end if either bound is negative
//============================================================================================
void substring(char* source, int startIndex, int endIndex, char* dest, int destLength)
{
  char temp[destLength];
  if(startIndex < 0)
    startIndex == 0;
  if(endIndex < 0)
    endIndex == 0;
  if(endIndex < startIndex)
  {
    dest[0] = '\0';
    return;
  }
  if(endIndex >= length(source))
    endIndex = length(source);
  if(destLength < endIndex - startIndex + 1)
  {
    dest[0] = '\0';
    return;
  }
  for(int i = 0; i < endIndex - startIndex; i++)
  {
    temp[i] = source[startIndex + i];
  }
  for(int i = 0; i < endIndex - startIndex; i++)
    dest[i] = temp[i];
  dest[endIndex - startIndex] = '\0';
}

//============================================================================================
// Removing leading and trailing spaces or new lines
//============================================================================================
void trim(char* source, int sourceLength)
{
  char temp[sourceLength];
  int startIndex = 0;
  int endIndex = length(source)-1;
  for(; startIndex < length(source) && (source[startIndex] == ' ' || source[startIndex] == '\n' || source[startIndex] == '\t'); startIndex++);
  for(; endIndex >= 0 && (source[endIndex] == ' ' || source[endIndex] == '\n' || source[startIndex] == '\t'); endIndex--);
  endIndex++;  
  substring(source, startIndex, endIndex, temp, sizeof(temp)-1);
  strcpy(temp, source, sourceLength-1);
}

//============================================================================================
// Tests if two character arrays are equal
//============================================================================================
boolean equals(char* first, char* second)
{
  if(length(first) != length(second))
    return false;
  for(int i = 0; i < length(first); i++)
  {
    if(first[i] != second[i])
      return false;
  }
  return true;
}

//============================================================================================
// Tests if two character arrays are equal, ignoring case
//============================================================================================
boolean equalsIgnoreCase(char* first, char* second)
{
  if(length(first) != length(second))
    return false;
  for(int i = 0; i < length(first); i++)
  {
    int firstChar = first[i];
    int secondChar = second[i];
    // Make them lowercase
    if(firstChar >= 'A' && firstChar <= 'Z')
      firstChar += 'a' - 'A';
    if(secondChar >= 'A' && secondChar <= 'Z')
      secondChar += 'a' - 'A';
    if(firstChar != secondChar)
      return false;
  }
  return true;
}

//============================================================================================
// Copies one array into the other
//============================================================================================
void strcpy(char* source, char* dest, int destLength)
{
  if(destLength < length(source) + 1)
  {
    dest[0] = '\0';
    return;
  }
  for(int i = 0; i < length(source); i++)
  {
    dest[i] = source[i];
  }
  dest[length(source)] = '\0';
}

//============================================================================================
// Concatenates two character arrays
//============================================================================================
void concat(char* first, const char* second, char* dest, int destLength)
{
  char temp[destLength];
  if(destLength < length(first) + length(second) + 1)
  {
    dest[0] = '\0';
    return;
  }
  for(int i = 0; i < length(first); i++)
    temp[i] = first[i];
  for(int i = 0; i < length(second); i++)
    temp[i + length(first)] = second[i];
  temp[length(second) + length(first)] = '\0';  
  for(int i = 0; i < length(second) + length(first); i++)
    dest[i] = temp[i];
  dest[length(second) + length(first)] = '\0';  
}

//============================================================================================
// Concatenates a character array with an integer
//============================================================================================
void concatInt(char* first, int second, char* dest, int destLength)
{
  char secondChar[20];
  sprintf (secondChar, "%i", second);
  concat(first, secondChar, dest, destLength);  
}


//============================================================================================
// Serial functions
//============================================================================================
void debugSerialBegin()
{
  if(Robot::serialDebug)
  {
    Serial.begin(9600);
  }
}

void debugPrint(const char* message)
{
  if(Robot::serialDebug)
  {
    Serial.print(message);
  }
}

void debugPrintln(const char* message)
{
  if(Robot::serialDebug)
  {
    Serial.println(message);
  }
}

void debugPrint(int number)
{
  if(Robot::serialDebug)
  {
    Serial.print(number);
  }
}

void debugPrintln(int number)
{
  if(Robot::serialDebug)
  {
    Serial.println(number);
  }
}

void debugPrintln()
{
  if(Robot::serialDebug)
  {
    Serial.println();
  }
}


/*******************************************************************
 * A way to write an 'int' (2 Bytes) to EEPROM 
 * EEPROM library natively supports only bytes. 
 * Note it takes around 8ms to write an int to EEPROM 
 *******************************************************************/
void eepromWriteInt(int address, int value)
{
  union u_tag 
  {
    byte b[2];        //assumes 2 bytes in an int
    int INTtime;
  } 
  time;
  time.INTtime=value;

  EEPROM.write(address  , time.b[0]); 
  EEPROM.write(address+1, time.b[1]); 
}

/********************************************************
 * A way to read int (2 Bytes)from EEPROM 
 * EEPROM library natively supports only bytes
 ********************************************************/
int eepromReadInt(int address)
{
  union u_tag 
  {
    byte b[2];
    int INTtime;
  } 
  time;
  time.b[0] = EEPROM.read(address);
  time.b[1] = EEPROM.read(address+1);
  return time.INTtime;
}

#endif






