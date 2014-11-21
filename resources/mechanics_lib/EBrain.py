
from api.EComponent import EComponent
from EDevice import EDevice
from EModule import EModule
from ELibrary import *
import os
import shutil

class EBrain(EComponent):
    """
    A class for representing the brain of the robot.
    Stores the EModules (chains) directly attached to it
    as well as any devices connected directly to the brain.
    Computes the virtual pin assignments of all devices
    and generates code for uploading to the brain.

    @cvar chipTypes: The types of chips the brain can be.
    Arduino is preferred and has been tested much more.
    Arduino will work with the Pro Mini, and should also work for Mega and Uno.
    ATtiny will work with the ATtiny85.
    Many methods have been added since the last ATtiny test, so no guarantees!

    @ivar devices: A list of devices attached to the brain.
    The index of an element in this list corresponds to its pin number on the brain.
    @ivar analogInputs: Same as L{devices} but for the analog input pins of the brain.
    @ivar numPins: The number of pins on the brain (excluding analog inputs).
    @ivar numChainPins: The number of pins on the brain that can support chains.
    @ivar possibleComponentPins: A list of booleans indicating which pins support chains and devices.
    @ivar possibleAnalogOutPins: A list of booleans indicating which pins support PWM outputs.
    @ivar possibleAnalogInPins: A list of booleans indicating which pins support analog inputs.
    @ivar possibleTXPins: A list of booleans indicating which pins support Bluetooth TX
    @ivar possibleRXPins: A list of booleans indicating which pins support Bluetooth RX
    """

    chipTypes = ["Arduino", "ATtiny"]
    currentChain = 0
    jointPins = []
    jointPinsStr = ""
    gripperPin = -1
    curChainPin = -1
    motorPins = []
    motorPinsStr = ""
    
    def __init__(self, name = None, inChipType = None):
        """
        Initialize the brain.

        @param name: The name of the brain (default used if none provided).
        Names must be at least one character long.
        @type name: String

        @param inChipType: The type of chip being used for the Brain.
        Must be a value specified by L{chipTypes}.
        The default type is Arduino.
        @type inChipType: String
        """
        EComponent.__init__(self, name)
        if inChipType is None or not self.setChipType(inChipType):
            self.setChipType("Arduino")
        
        self.clearDevices()

    def setChipType(self, inChipType):
        """
        Sets the type of chip being used for the brain.
        This must be called before assigning/adding any chains or modules to the brain.

        @param inChipType: The type of chip being used for the Brain.
        Must be a value specified by L{chipTypes}.
        @type inChipType: String

        @return: False if not a valid chip type, True otherwise.
        @rtype: boolean
        """
        if inChipType not in self.chipTypes:
            self._printError("An invalid chip type was specified. Allowable types are " + str(chipTypes))
            return False
        if inChipType == "Arduino":
            # Set pins available for use with devices or chains
            self.possibleComponentPins = [False]*14 # Arduino Uno has pins 0-13
            self.possibleComponentPins[2:10] = [True]*8 # Current PCBs use pins 2-9

            # Set which pins supprt PWM outputs
            #   Will be intersected with above list of available pins
            #   See http://arduino.cc/en/Main/arduinoBoardUno
            self.possibleAnalogOutPins = [False]*14
            self.possibleAnalogOutPins[3] = True
            self.possibleAnalogOutPins[5] = True
            self.possibleAnalogOutPins[6] = True
            self.possibleAnalogOutPins[9] = True
            self.possibleAnalogOutPins[10] = True
            self.possibleAnalogOutPins[11] = True

            # Set which pins are available for analog inputs
            #   These pins are separate from above digital pins
            self.possibleAnalogInPins = [False]*6
            self.possibleAnalogInPins[0:1] = [True]*2 # Current PCBs give access to A0 and A1

            # Set which pins can be used as bluetooth TX/RX
            self.possibleTXPins = [False]*14
            self.possibleTXPins[11] = True
            self.possibleRXPins = [False]*14
            self.possibleRXPins[10] = True
            
        elif inChipType == "ATtiny":
            self.possibleComponentPins = [True, False, True, True, True, False, False, False] # Reserve PORTB1 for error LED for now
        self.numPins = len(self.possibleComponentPins)
        self.numChainPins = sum(self.possibleComponentPins)
        self.chipType = inChipType

    def clearDevices(self):
        """
        Deletes all attached devices.
        """
        self.devices = [None]*self.numPins
        self.analogInputs = [None]*len(self.possibleAnalogInPins)
        self.curChainPin = -1

    def setCurrentChain(self, inChain=None, inPin=None):
        """
        Sets the current chain to edit (for use with the L{append} function.

        @param inChain: The desired chain index to use.
        This is ignored if a valid inPin is specified.
        @type inChain: int

        @param inPin: The desired pin to use.
        @type inPin: int

        @return: Whether it was successful (whether specification was valid).
        @rtype: boolean
        """
        if inChain is not None and inPin is None:
            inPin = chainIndexToPinIndex(inChain)
        if inPin is not None:
            if not inPin in range(self.numPins):
                self._printError("Invalid pin specified when setting current chain")
                return False
            if not self.possibleComponentPins[inPin]:
                self._printError("Invalid pin specified when setting current chain")
                return False
            if self.devices[inPin] is not None:
                if isinstance(self.devices[inPin], EDevice):
                    pinType = self.devices[inPin].getPinTypes()
                    if isinstance(pinType, (list, tuple)):
                        if len(pinType) > 1:
                            pinType = pinType[0]
                    if not pinType == "A_I":
                        self._printError("Desired pin already has an EDevice attached to it (while setting current chain)")
                        return False # EDevice is at that pin
        self.curChainPin = inPin
        return True
        
    def chainIndexToPinIndex(self, inChain):
        """
        Finds the pin index corresponding to the given chain index.

        Chain index refers to the index of the ith possible chain
        (not necessarily the ith connected chain if chains were manually added to various pins).

        For example, if pin 0 does not support chains but pin 1 does, then
        the pin index of chain 0 is 1.
        
        Technically, this finds the index of the ith ocurrence of "True" in possibleComponentPins (which is thus an index into self.devices))

        @param inChain: The index of the desired chain (0-indexed).
        @type inChain: int

        @return: The pin index corresponding to the desired chain index.
        Returns -1 if inChain is out of bounds.
        @rtype: int
        """
        curChain = -1
        for nextPin in range(self.numPins):
            if self.possibleComponentPins[nextPin]:
                curChain += 1
                if curChain == inChain:
                    return nextPin
        return -1

    def addBluetooth(self, rxPin = 10, txPin = 11):
        """
        Adds a bluetooth serial module directly to the brain (no EModule is used).

        Only works with Arduino brain, and must be attached to pins allowed by L{possibleTXPins} and L{possibleRXPins}.

        @param rxPin: The RX pin of the Bluetooth module (TX of Arduino).
        @type rxPin: int

        @param txPin: The TX pin of the Bluetooth module (RX of Arduino).
        @type txPin: int

        @return: True is successfull, False if error which may be because
          - rxPin or txPin is not allowed by L{possibleTXPins} and L{possibleRXPins}
          - rxPin or txPin is already in use
          - another bluetooth module is already attached to the brain
        @rtype: boolean
        """
        if rxPin not in range(self.numPins):
            self._printError("Invalid RX pin while adding bluetooth")
            return False
        if not self.possibleRXPins[rxPin]:
            self._printError("Invalid RX pin while adding bluetooth")
            return False
        if txPin not in range(self.numPins):
            self._printError("Invalid TX pin while adding bluetooth")
            return False
        if not self.possibleTXPins[txPin]:
            self._printError("Invalid TX pin while adding bluetooth")
            return False
        if (self.devices[rxPin] is not None) or (self.devices[txPin] is not None):
            self._printError("RX or TX pin already in use (while adding bluetooth)")
            return False
        for nextDev in self.devices:
            if nextDev is not None:
                if ("Bluetooth RX" in nextDev.getName()) or ("Bluetooth TX" in nextDev.getName()):
                    self._printError("Cannot have multiple bluetooth modules")
                    return False
            
        self.devices[rxPin] = EDevice("Bluetooth RX")
        self.devices[txPin] = EDevice("Bluetooth TX")
        if self.curChainPin == rxPin or self.curChainPin == txPin:
            self.curChainPin = -1
        return True
        
    def addEDevice(self, inEDevice, inPin = None):
        """
        Adds the given EDevice directly to the Brain (NO MODULE IS USED).
        
        If inPin is omitted, the first available brain pin is used.

        Currently, only supports EDevices with ONE required pin.
        If an EDevice is given with multiple pins, only the first pin is considered.

        @param inEDevice: The device to add.
        @type inEDevice: L{EDevice}

        @param inPin: The pin to which the device should be connected (0-indexed).
        @type inPin: int

        @return: True if the addition was successfull.  Returns False (and does nothing) if
          - inEDevice is not a valid EDevice
          - inPin is invalid
          - inPin is already in use
          - addition was impossible (required pin types not available).
        
        """
        if not isinstance(inEDevice, EDevice):
            self._printError("Input given to addEDevice is not an EDevice")
            return False # input is not an EDevice
        
        pinType = inEDevice.getPinTypes()
        if isinstance(pinType, (list, tuple)):
            pinType = pinType[0]
            
        if (inPin is not None) and (pinType != "A_I") and (inPin not in range(self.numPins)):
            self._printError("Desired pin given to addEDevice is out of range (while adding " + inEDevice.getName() + ")")
            return False # invalid chain index
        if (inPin is not None) and (pinType == "A_I") and (inPin not in range(len(self.possibleAnalogInPins))):
            self._printError("Desired pin given to addEDevice is out of range (while adding " + inEDevice.getName() + ")")
            return False # invalid chain index
        
        if (inPin is not None) and (pinType != "A_I") and (self.devices[inPin] is not None):
            self._printError("Desired pin given to addEDevice is already in use (while adding " + inEDevice.getName() + ")")
            return False # Pin already used
        if (inPin is not None) and (pinType == "A_I") and (self.analogInputs[inPin] is not None):
            self._printError("Desired pin given to addEDevice is already in use (while adding " + inEDevice.getName() + ")")
            return False # Pin already used
        
        if pinType == "A_O":
            if inPin is not None and not self.possibleAnalogOutPins[inPin]:
                self._printError("Desired pin given to addEDevice does not support analog output (while adding " + inEDevice.getName() + ")")
                return False # pin doesn't support analog out
        if pinType == "A_I":
            if inPin is not None and not self.possibleAnalogInPins[inPin]:
                self._printError("Desired pin given to addEDevice does not support analog input (while adding " + inEDevice.getName() + ")")
                return False # pin doesn't support analog in
        
        if inPin is None:
            # find first available component pin
            if pinType != "A_I":
                for nextPin in range(self.numPins):
                    if self.devices[nextPin] is None and self.possibleComponentPins[nextPin]:
                        if (pinType == "A_O") and self.possibleAnalogOutPins[nextPin]:
                            inPin = nextPin
                            break
                        elif (pinType != "A_O"):
                            inPin = nextPin
                            break
            else:
                for nextPin in range(len(self.possibleAnalogInPins)):
                    if self.analogInputs[nextPin] is None:
                        inPin = nextPin
                        break
            
        if inPin is None:
            self._printError("No suitable pin was found (while adding " + inEDevice.getName() + ")")
            return False # no pin to use
        
        # Everything seems to be in order... make the addition
        if pinType != "A_I":
            self.devices[inPin] = inEDevice
            if inPin == self.curChainPin:
                self.curChainPin = -1
        else:
            self.analogInputs[inPin] = inEDevice
            
        return True
    
    def addEModule(self, inEModule, inChain = None, newChain=True, inPin=None):
        """
        Adds the given module to the Brain.

        If inPin is specified, inChain will be ignored.
        If inChain or inPin is specified,
          - module will be added to that chain or pin (creating one if empty or appending to chain if exists).
          - newChain will be ignored
        If newChain is False
          - appends to the current chain even if chain pins are still available
          - if current chain is not set, uses the last chain on the brain
        If newChain is True, tries to find a free pin for the chain (but if none exists, will append it to a chain).

        If addition is successful, the current chain is updated to the chain used.
        
        @param inEModule: The EModule to add.
        @type inEModule: L{EModule}
        
        @param inChain: The desired chain to use (0-indexed).
        @type inChain: int

        @param newChain: Whether it is preferred to use a free pin rather than appending to a chain.
        This is ignored if a valid inChain or inPin is provided.
        @type newChain: boolean

        @param inPin: The desired pin to use (0-indexed).
        If a valid inPin is provided, inChain will be ignored.
        @type inPin: int
        
        @return: Whether the addition was successful.  May return False if
          - inEModule is not a valid EModule
          - inChain or inPin is invalid
          - there is nowhere to place the module
            (this should be rare - probably, chip is defined to have no pins or all pins have EDevices attached)
        @rtype: boolean
        """
        if not isinstance(inEModule, EModule):
            self._printError("Input to addEModule is not an EModule")
            return False # input is not an EModule
        if inPin is not None:
            if not self.possibleComponentPins[inPin]:
                self._printError("Desired pin cannot be used as a chain (while adding " + inEModule.getName() + ")")
                return False # invalid chain index
            if self.devices[inPin] is not None:
                if isinstance(self.devices[inPin], EDevice):
                    pinType = self.devices[inPin].getPinTypes()
                    if isinstance(pinType, (list, tuple)):
                        pinType = pinType[0]
                    if not pinType == "A_I":
                        self._printError("Desired pin already has an EDevice attached to it (while adding " + inEModule.getName() + ")")
                        return False # EDevice is at that pin            
        
        if inChain is not None and inPin is None:
            if inChain not in range(self.numChainPins):
                self._printError("Desired chain given to addEModule is out of range (while adding " + inEModule.getName() + ")")
                return False # invalid chain index
            # Convert chain index to pin index
            inPin = self.chainIndexToPinIndex(inChain)
            if not self.possibleComponentPins[inPin]:
                self._printError("Desired pin cannot be used as a chain (while adding " + inEModule.getName() + ")")
                return False # invalid chain index
            if self.devices[inPin] is not None:
                if isinstance(self.devices[inPin], EDevice):
                    pinType = self.devices[inPin].getPinTypes()
                    if isinstance(pinType, (list, tuple)):
                        pinType = pinType[0]
                    if not pinType == "A_I":
                        self._printError("Desired pin already has an EDevice attached to it (while adding " + inEModule.getName() + ")")
                        return False # EDevice is at that chain
            
        if inPin is None:
            # find first available chain pin if desired
            if newChain:
                for nextPin in range(self.numPins):
                    if self.devices[nextPin] is None and self.possibleComponentPins[nextPin]:
                        inPin = nextPin
                        break
                    elif isinstance(self.devices[nextPin], EDevice):
                        pinType = self.devices[nextPin].getPinTypes()
                        if isinstance(pinType, (list, tuple)):
                            pinType = pinType[0]
                        if pinType == "A_I":
                            inPin = nextPin
                            break
            # if no available pins (or appending is desired), append to currnet chain or last chain
            if inPin is None:
                if self.curChainPin >= 0:
                    inPin = self.curChainPin
                else:
                    for nextPin in range(self.numPins):
                        if (self.devices[nextPin] is not None) and isinstance(self.devices[nextPin], EModule):
                            inPin = nextPin
            
        if inPin is None:
            error = "There's nowhere to place your module! (While adding " + inEModule.getName() + ")"
            if not newChain:
                error += "\n\t(Possibly because you are trying to append but no chains have been added)"
            self._printError(error)
            return False # no pin to use... something is very wrong (likely defined chip to have no possible pins)
        
        # Everything seems to be in order... make the addition
        if self.devices[inPin] is None:
            self.devices[inPin] = inEModule
            self.setCurrentChain(None, inPin)
        else:
            self.devices[inPin].appendToChain(inEModule)
            
        return True

    def append(self, toAdd, postfix=None, inChain=None, inPin=None):
        """
        Appends the given device to the brain using the current chain.
        The current chain is set using L{setCurrentChain} or by adding an EModule to the brain.
        
        This is the same as calling L{attach} with newChain=False except if toAdd is a single EDevice.
        If a single EDevice is provided here, an EModule will be created to hold it.
        (note that L{attach} defaults to creating a new chain if possible)

        @param toAdd: The device to add to the brain.
        If toAdd is a single EDevice, an EModule will be created to hold it.
        @type toAdd: L{EModule}, L{EDevice}, or List/Tuple of L{EDevice}s

        @param postfix: An optional postfix to give to the device's name.
        If postfix is provided, a clone of toAdd is added to the brain (with the postfix).
        @type postfix: String

        @param inChain: The desired chain to use (0-indexed).
        This will be ignored if inPin is specified.
        @type inChain: int

        @param inPin: The desired pin to use (0-indexed).
        If a valid inPin is provided, inChain will be ignored.
        This parameter is ignored when adding an EDevice.
        @type inPin: int
        
        @return: Whether the addition was successful.  May return False if
          - toAdd is an invalid type
          - toAdd is invalid
          - inChain is invalid (and no inPin specified)
          - inPin is invalid
          - there is nowhere to place the device
            (this should be rare for EModules - probably, chip is defined to have no pins or all pins have EDevices attached)
        @rtype: boolean 
        """
        if isinstance(toAdd, EDevice):
            device = toAdd.clone()
            toAdd = EModule()
            toAdd.addEDevice(device)
        return self.attach(toAdd, postfix, inChain, False, inPin)
        
    def attach(self, toAdd, postfix=None, position=None, newChain=True, inPin=None):
        """
        Adds the given device to the brain.
        Can accept L{EModule}s, L{EDevice}s, or a list of L{EDevice}s (defining an EModule).
                
        @param toAdd: The device to add to the brain.
        @type toAdd: L{EModule}, L{EDevice}, or List/Tuple of L{EDevice}s

        @param postfix: An optional postfix to give to the device's name.
        If postfix is provided, a clone of toAdd is added to the brain with the given postfix.
        If no postfix is provided and toAdd is an EModule, the name of toAdd will be used as a postfix.
        @type postfix: String

        @param position: The desired chain to use (0-indexed) if adding an EModule, or the desired
        pin to use if adding an EDevice.
        When adding an EModule, this will be ignored if a valid inPin is provided.
        @type position: int

        @param newChain: Whether it is preferred to use a free pin rather than appending to a chain.
        This is ignored if a valid inChain or inPin is provided.
        This is ignored if adding an EDevice.
        @type newChain: boolean

        @param inPin: The desired pin to use (0-indexed).
        If a valid inPin is provided, inChain will be ignored.
        This parameter is ignored when adding an EDevice.
        @type inPin: int
        
        @return: Whether the addition was successful.  May return False if
          - toAdd is invalid
          - inChain is invalid (and no inPin specified)
          - inPin is invalid
          - there is nowhere to place the device
            (this should be rare for EModules - probably, chip is defined to have no pins or all pins have EDevices attached)
        @rtype: boolean 
        """
        if isinstance(toAdd, EModule):
            return self.addEModuleWithPostfix(toAdd, postfix, position, newChain, inPin)
        elif isinstance(toAdd, (list, tuple)):
            return self.addEModuleFromEDevices(toAdd, postfix, position, newChain, inPin)
        elif isinstance(toAdd, EDevice):
            return self.addEDeviceWithPostfix(toAdd, postfix, position)

    def addEDeviceWithPostfix(self, inEDevice, postfix=None, inPin=None):
        """
        Adds the given EDevice directly to the Brain (NO MODULE IS USED).
        Uses the given postfix if one is provided.
        
        If inPin is omitted, the first available brain pin is used.

        If postfix is provided, a clone of inEDevice is added.
        
        Currently, only supports EDevices with ONE required pin.
        If an EDevice is given with multiple pins, only the first pin is considered.

        @param inEDevice: The device to add.
        @type inEDevice: L{EDevice}

        @param postfix: An optional postfix to give to the device's name.
        If postfix is provided, a clone of toAdd is added to the brain (with the postfix).
        @type postfix: String
        
        @param inPin: The pin to which the device should be connected (0-indexed).
        @type inPin: int
        
        @return: True if the addition was successfull.  Returns False (and does nothing) if
          - inEDevice is not a valid EDevice
          - inPin is invalid
          - inPin is already in use
          - addition was impossible (required pin types not available).
        @rtype: boolean 
        """
        if postfix is not None:
            inEDevice = inEDevice.clone(inEDevice.getName() + " (" + postfix + ")")
        return self.addEDevice(inEDevice, inPin)
    
    def addEModuleWithPostfix(self, inEModule, postfix=None, inChain=None, newChain=True, inPin=None):
        """
        Adds the given module to the Brain with the given postfix if provided.

        If inPin is specified, inChain will be ignored
        If inChain or inPin is specified,
          - module will be added to that chain or pin (creating one if empty or appending to chain if exists).
          - newChain will be ignored
        If newChain is False, appends to the last existing chain even if chain pins are still available
        Otherwise, tries to find a free pin for the chain (but if none exists, will append it to a chain).

        @param inEModule: The EModule to add.
        @type inEModule: L{EModule}

        @param postfix: An optional postfix to give to the device's name.
        If postfix is provided, a clone of toAdd is added to the brain (with the postfix).
        @type postfix: String
        
        @param inChain: The desired chain to use (0-indexed).
        This will be ignored if inPin is specified.
        @type inChain: int
        
        @param newChain: Whether it is preferred to use a free pin rather than appending to a chain.
        This is ignored if a valid inChain is provided.
        @type newChain: boolean

        @param inPin: The desired pin to use.
        @type inPin: int

        @return: Whether the addition was successful.  May return False if
          - inEModule is not a valid EModule
          - inChain is invalid
          - there is nowhere to place the module
            (this should be rare - probably, chip is defined to have no pins or all pins have EDevices attached)
        @rtype: boolean
        """
        if postfix is not None:
            inEModule = inEModule.clone(None, "(" + postfix + ")")
        return self.addEModule(inEModule, inChain, newChain, inPin)

    def addEModuleFromEDevices(self, inEDevices, postfix=None, inChain=None, newChain=True, inPin=None):
        """
        Creates a module from the given devices and adds it to the Brain.

        If inPin is specified, inChain will be ignored
        If inChain or inPin is specified,
          - module will be added to that chain or pin (creating one if empty or appending to chain if exists).
          - newChain will be ignored
        If newChain is False, appends to the last existing chain even if chain pins are still available
        Otherwise, tries to find a free pin for the chain (but if none exists, will append it to a chain).

        The new module will be called "Custom Module" with the given postfix if provided.
        
        @param inEDevices: The EDevices that should comprise the new L{EModule}.
        @type inEDevices: List or Tuple of L{EDevice}s or single L{EDevice}

        @param postfix: An optional postfix to give to the device's name.
        If postfix is provided, a clone of toAdd is added to the brain (with the postfix).
        @type postfix: String
        
        @param inChain: The desired chain to use (0-indexed).
        @type inChain: int

        @param newChain: Whether it is preferred to use a free pin rather than appending to a chain.
        This is ignored if a valid inChain is provided.
        @type newChain: boolean

        @param inPin: The desired pin to use.
        @type inPin: int
        
        @return: Whether the addition was successful.  May return False if
          - inEDevices is invalid
          - a module could not be created with the given devices
          - inChain is invalid
          - there is nowhere to place the module
            (this should be rare - probably, chip is defined to have no pins or all pins have EDevices attached)
        @rtype: boolean
        """
        toAdd = EModule("Custom Module")
        if not isinstance(inEDevices, (list, tuple)):
            inEDevices = [inEDevices]
        if not toAdd.setEDevices(inEDevices):
            self._printError("Desired custom module is impossible to configure")
            return False
        if postfix is not None:
            toAdd = toAdd.clone(None, "(" + postfix + ")")
        self.addEModule(toAdd, inChain, newChain, inPin)
        return True
        
        
    def removeEModule(self, inEModule):
        """
        Removes the first instance of the given EModule from the EBrain.

        @param inEModule: the EModule to delete.
        @type inEModule: L{EModule}

        @return: False if inEModule is not in the EBrain, True otherwise.
        @rtype: boolean
        """
        if inEModule not in self.devices:
            self._printError("Trying to remove a module that does not exist")
            return False
        self.devices[self.devices.index(inEModule)] = None
        return True

    def remove(self, inChain=None, inPin=None, analogPin=False):
        """
        Clears all devices from the specified brain position.

        If inChain and inPin are both omitted, the current chain will be cleared.
        
        @param inChain: The index of the chain to remove (0-indexed).
        Note that this refers to the ith possible chain of the brain,
        which is not necessarily the ith attached chain if chains were added manually to pins.
        This parameter is ignored if inPin is specified.
        @type inChain: int

        @param inPin: The pin to clear.
        @type inPin: int

        @param analogPin: Whether the given inPin refers to an analog input of the brain.
        This is only relevant if the desired device to remove is an EDevice.
        @type analogPin: boolean
        
        @return: Whether the removal was successfull (whether inChain/inPin/currentChain was valid).
        @rtype: boolean
        """
        if inChain is None and inPin is None:
            if self.curChainPin >= 0:
                self.devices[self.curChainPin] = None
                return True
            else:
                self._printError("No chain or pin was specified for removal (while in method 'remove')")
                return False
        if inChain is not None and inPin is None:
            inPin = self.chainIndexToPinIndex(inChain)
        if inPin is not None:
            if inPin not in range(self.numPins):
                self._printError("Invalid pin given to 'remove'")
                return False
            if analogPin:
                if inPin not in range(len(self.possibleAnlogInPins)):
                    self._printError("Specified pin is not a valid analog input (while in method 'remove')")
                    return False
                self.analogInputs[inPin] = None
                return True
            if not self.possibleComponentPins[inPin]:
                self._printError("Specified pin is not a valid component pin (while in method 'remove')")
                return False
            self.devices[inPin] = None
            return True
        if self.curChainPin >= 0:
            self.devices[curChainPin] = None
            return True
        return False

    def getVirtualPinsString(self):
        """
        Get a printable string describing what is connected to each virtual pin
        (the user of robot should have this).

        @return: A printable string describing what is connected to each virtual pin.
        It basically lists the virtual pins and the names/pins of the attached devices.
        @rtype: String
        """
        res = "Virtual Pins: \n"
        curVirtualPin = 0
        self.jointPins = []
        self.gripperPin = -1
        self.motorPins = []
        self.jointPinsStr = "List of Joints: \n"
        self.motorPinsStr = "List of Motors: \n"
        for chainNum in range(self.numChainPins):
            pinDescriptionsForChain = self.getPinDescriptionsForChain(chainNum)
            # Step through each chip in the chain
            for chipDescriptions in pinDescriptionsForChain:
                # Step through each pin on the chip
                for pinDescription in chipDescriptions:
                    if pinDescription is not None:
                        if ("Bluetooth RX" not in pinDescription) and ("Bluetooth TX" not in pinDescription):
                            res += "\n\tPin %i: %s" %(curVirtualPin, str(pinDescription))
                            if jointServo.getName() in str(pinDescription):
                                self.jointPinsStr += "\n\tJoint %i: %s \t(Pin %i)" %(len(self.jointPins), str(pinDescription), curVirtualPin)
                                self.jointPins.append(curVirtualPin)                                
                            if gripperServo.getName() in str(pinDescription):
                                self.gripperPin = curVirtualPin
                            if motor.getName() in str(pinDescription):
                                self.motorPinsStr += "\n\tMotor %i: %s \t(Pin %i)" %(len(self.motorPins), str(pinDescription), curVirtualPin)
                                self.motorPins.append(curVirtualPin)
                            curVirtualPin += 1
        for analogInPin in range(len(self.possibleAnalogInPins)):
            if self.analogInputs[analogInPin] is not None:
                res += "\n\tPin %i: %s" %(curVirtualPin, self.analogInputs[analogInPin].getName())
            curVirtualPin += 1
        return res

    def getVirtualPinsStringForBT(self):
        """
        Get a string describing what is connected to each virtual pin
        (for use by the Android app - the Arduino will send this when requsted)

        @return: A string describing what is connected to each virtual pin and their pin types.
        @rtype: String
        """
        res = "\"{"
        curVirtualPin = 0
        curPin = 0
        curChip = 0
        for chainNum in range(self.numChainPins):
            pinDescriptionsForChain = self.getPinDescriptionsForChain(chainNum)
            pinTypesForChain = self.getPinTypesForChain(chainNum)
            # Step through each chip in the chain
            curChip = 0
            for chipDescriptions in pinDescriptionsForChain:
                # Step through each pin on the chip
                curPin = 0
                for pinDescription in chipDescriptions:
                    if (pinDescription is not None):
                        if ("Bluetooth RX" not in pinDescription) and ("Bluetooth TX" not in pinDescription):
                            res += "Pin %i<%s>: %s\\n" %(curVirtualPin, pinTypesForChain[curChip][curPin], str(pinDescription))
                            curVirtualPin += 1
                    curPin += 1
                curChip += 1
        res += "}\""
        return res

    def getJointPins(self):
        """
        Searches attached devices to determine which virtual pins correspond to robot joints.

        A joint is found if and only if a "jointServo" EDevice was used.

        @return: A list of virtual pins which correspond to robot joints.
        @rtype: List of ints
        """
        self.getVirtualPinsString()
        return self.jointPins[:]

    def getGripperPin(self):
        """
        Searches attached devices to determine which virtual pin corresponds to robot gripper.

        A gripper is found if and only if a "gripperServo" EDevice was used.

        @return: The virtual pin which corresponds to the robot gripper.
        @rtype: int
        """
        self.getVirtualPinsString()
        return self.gripperPin

    def getMotorPins(self):
        """
        Searches attached devices to determine which virtual pins correspond to motors.

        A motor is found if a "motor" EDevice was used (or if an EDevice name includes the name of the provided motor device).
        
        @return: A list of virtual pins which correspond to robot motors.
        @rtype: List of ints
        """
        self.getVirtualPinsString()
        return self.motorPins[:]
    
    def getPinDescriptionsForChain(self, chainNum):
        """
        Get a list of what is attached to each virtual pin of the specified chain.

        @param chainNum: The index of the desired chain (0-indexed).
        Note that this refers to the ith possible chain of the brain,
        which is not necessarily the ith attached chain if chains were added manually to pins.
        @type chainNum: int
        
        @return: A list describing what is connected to the given chain.

          - If an EModule is connected there, then it returns
              - A list where the jth element is a 3-element list whose ith element is a String
                indicating the name and pin number of the EDevice attached to the jth module's ith pin.
                An element is None if nothing is attached to the jth module's ith pin.

              - The first element corresponds to the one connected to the brain.
          - If an EDevice is connect there, then it returns the EDevice's name.

        Returns empty list if chainNum is invalid or nothing is connected there.
        @rtype: List of List of Strings
        """
        deviceOfChain = self.chainIndexToDevice(chainNum)
        if deviceOfChain is None:
            return []
        if ("Bluetooth RX" in deviceOfChain.getName()) or ("Bluetooth TX" in deviceOfChain.getName()):
            return []
        if isinstance(deviceOfChain, EModule):
            return deviceOfChain.getPinDescriptionsForChain()
        if isinstance(deviceOfChain, EDevice):
            return [[deviceOfChain.getName()]]

    def getPinTypesForChain(self, chainNum):
        """
        Get a list describing the pin types of each pin on the chain.

        @param chainNum: The index of the desired chain (0-indexed).
        Note that this refers to the ith possible chain of the brain,
        which is not necessarily the ith attached chain if chains were added manually to pins.
        @type chainNum: int
        
        @return: A list where each element is a list of what pin types are on that chip.
        Returns empty list if chainNum is invalid.
        @rtype: List of List of Strings
        """
        deviceOfChain = self.chainIndexToDevice(chainNum)
        if deviceOfChain is None:
            return []
        if ("Bluetooth RX" in deviceOfChain.getName()) or ("Bluetooth TX" in deviceOfChain.getName()):
            return []
        if isinstance(deviceOfChain, EModule):
            return deviceOfChain.getPinTypesForChain()
        if isinstance(deviceOfChain, EDevice):
            return [deviceOfChain.getPinTypes()]
    
    def chainIndexToDevice(self, chainNum):
        """
        Helper function to get the EModule or EDevice representing the chain at index chainNum.
    
        @param chainNum: The index of the desired chain (0-indexed).
        Note that this refers to the ith possible chain of the brain,
        which is not necessarily the ith attached chain if chains were added manually to pins.
        @type chainNum: int

        @return: The device connected to the desired chain.
        @rtype: L{EModule} or L{EDevice}
        """
        pinIndex = self.chainIndexToPinIndex(chainNum)
        if pinIndex is None:
            return pinIndex
        return self.devices[pinIndex]

    def generateCode(self, destDir = None, templateDir = None, templateFiles = None):
        """
        Generates code that can be programmed onto the brain chip.
        
        User then interfaces with virtual pins described by getVirtualPinsString() above.
        
        NOTE: This will delete everything currently in the destination folder!

        This also writes text files to destdir describing the virtual pin assignments (for coding purposes)
        and what is connected to each pin of the brain (for building purposes).

        @param destDir: The destination folder for the code.
        If omitted, a folder called "Brain_Code" will be created in the current directory.
        @type destDir: String

        @param templateDir: The folder with code templates.
        If omitted, will default to
          - "utils/_Brain Code Templates/Arduino/Brain_Code" if using an Arduino
          - "utils/_Brain Code Templates/ATtiny85/Brain_Code" if using an ATtiny
        @type templateDir: String

        @param templateFiles: A list of filenames which have variables to auto-assign.
        If omitted, will default to
          - "robot_functions.h" and "robot_functions.cpp" if using an Arduino
          - "robot_functions.h" if using an ATtiny
        @type templateFiles: String or List/Tuple of Strings        
        """
        numChains = len(self.devices) - self.devices.count(None)
        chainTypes = []
        numChips = []
        chainPins = []
        pinTypes = []
        bluetoothRX = -1
        bluetoothTX = -1
        # Figure out number of chips on each chain, pin types for all chips, and pins of each chain
        for i in range(len(self.devices)):
            if self.devices[i] is not None:
                if(isinstance(self.devices[i], EModule)):
                    chainTypes.append("MODULE")
                    numChips.append(self.devices[i].getNumChipsOnChain())
                    nextPinTypes = self.devices[i].getPinTypesForChain()
                elif(isinstance(self.devices[i], EDevice)):
                    if "Bluetooth RX" in self.devices[i].getName():
                        bluetoothRX = i
                        numChains -= 1
                        continue
                    if "Bluetooth TX" in self.devices[i].getName():
                        bluetoothTX = i
                        numChains -= 1
                        continue                        
                    chainTypes.append("DEVICE")
                    numChips.append(1)
                    nextPinTypes = self.devices[i].getPinTypes()
                    if not isinstance(nextPinTypes, (list, tuple)):
                        nextPinTypes = [nextPinTypes]
                    while(len(nextPinTypes) < 3):
                          nextPinTypes.append("N_C")
                    nextPinTypes = [nextPinTypes]
                for nextChip in range(len(nextPinTypes)):
                    for nextType in range(len(nextPinTypes[nextChip])):
                        if nextPinTypes[nextChip][nextType] == "SERVO":
                            nextPinTypes[nextChip][nextType] = "A_O"
                chainPins.append(i)
                pinTypes += nextPinTypes
        # See if any are specified as joints, grippers, or motors
        self.getJointPins()
        self.getGripperPin()
        self.getMotorPins()
        # Figure out total number of chips and max chips on a chain        
        numChipsTotal = sum(numChips)
        if len(numChips) == 0:
            maxChipsOnChain = 0
        else:
            maxChipsOnChain = max(numChips)
        # Make strings that are array initialization assignments in c/c++
        chainTypesStr = str(chainTypes).replace('[','{').replace(']','}').replace('\'','')
        numChipsStr = str(numChips).replace('[','{').replace(']','}')
        jointPinsStr = str(self.jointPins).replace('[','{').replace(']','}')
        motorPinsStr = str(self.motorPins).replace('[','{').replace(']','}')
        if self.chipType == "Arduino":
            chainPinsStr = str(chainPins).replace('[','{').replace(']','}')
        elif self.chipType == "ATtiny":
            chainPinsStr = "{PORTB%i" %chainPins[0]
            for nextPin in chainPins[1:]:
                chainPinsStr += ", PORTB%i" %nextPin
            chainPinsStr += "}"
        # pinTypes may be very big, so to make code look neater write 3 chips per line
        pinTypesStr = "{"
        for i in range(len(pinTypes)):
            nextType = pinTypes[i]
            if i % 3 == 0:
                pinTypesStr += "\n\t"
            pinTypesStr += str(nextType).replace('[','{').replace(']','}').replace("'", "")
            if i < len(pinTypes) - 1:
                pinTypesStr += ", "
        pinTypesStr += "}"        

        # Use default or given directory/file names
        if templateDir is None:
            if self.chipType == "Arduino":
                templateDir = "utils/_Brain Code Templates/Arduino/Brain_Code"
            elif self.chipType == "ATtiny":
                templateDir = "utils/_Brain Code Templates/ATtiny85/Brain_Code"
        if templateFiles is None:
            if self.chipType == "Arduino":
                templateFiles = ["robot_functions.h", "robot_functions.cpp"]
            elif self.chipType == "ATtiny":
                templateFiles = ["robot_functions.h"]
        if not isinstance(templateFiles, (list, tuple)):
            templateFiles = [templateFiles]    
        if destDir is None:
            destDir = "Brain_Code"
        startDir = os.getcwd()
        
        # Delete everything in the destination folder
        if os.path.exists(destDir):
           shutil.rmtree(destDir)
        # Copy template files to destination
        shutil.copytree(templateDir, destDir)

        for templateFile in templateFiles:
            # Get handle to template file in template directory (to read)
            fin = None
            for root, dirs, files in os.walk(templateDir, topdown=False):
                for name in files:
                    if name == templateFile:
                        fin = open(os.path.join(root, name))
            # Get handle to template file in destination directory (to write)
            fout = None
            for root, dirs, files in os.walk(destDir, topdown=False):
                for name in files:
                    if name == templateFile:
                        fout = open(os.path.join(root, name), 'w')
            
            # Read through file and edit specified points
            # Variables to assign in template have their names surrounded by '$'
            # The place to insert the values of those variables are surrounded by '@'
            curVariable = None
            readingVariable = False
            nextChar = fin.read(1)
            while nextChar != "":
                if nextChar == "`":
                    if readingVariable:
                        # Finished reading a variable name
                        readingVariable = False
                    else:
                        # Start of reading a variable name
                        curVariable = ""
                        readingVariable = True
                elif nextChar == "@":
                    # Write appropriate value based on curVariable
                    if curVariable == "NUM_CHIPS":
                        fout.write(str(numChipsTotal))
                    elif curVariable == "NUM_CHAINS":
                        fout.write(str(numChains))
                    elif curVariable == "MAX_CHIPS_ON_CHAIN":
                        fout.write(str(maxChipsOnChain))
                    elif curVariable == "chainPins":
                        fout.write(chainPinsStr)
                    elif curVariable == "chainTypes":
                        fout.write(chainTypesStr)
                    elif curVariable == "numChips":
                        fout.write(numChipsStr)
                    elif curVariable == "pinTypes":
                        fout.write(pinTypesStr)
                    elif curVariable == "PINS_FOR_BT":
                        fout.write(self.getVirtualPinsStringForBT())
                    elif curVariable == "BT_TX":
                        fout.write(str(bluetoothTX))
                    elif curVariable == "BT_RX":
                        fout.write(str(bluetoothRX))
                    elif curVariable == "jointPinsAuto":
                        fout.write(jointPinsStr)
                    elif curVariable == "gripperPin":
                        fout.write(str(self.gripperPin))
                    elif curVariable == "motorPinsAuto":
                        fout.write(motorPinsStr)
                    # Read until next @ is found
                    nextChar = fin.read(1)
                    while nextChar != "@" and nextChar != "":
                        nextChar = fin.read(1)
                else:
                    fout.write(nextChar)
                    if readingVariable:
                        curVariable += nextChar
                    
                nextChar = fin.read(1)
            # All done with this file!
            fout.close()
            fin.close()

        # Write a description of the virtual pins to the destination folder
        os.chdir(destDir)
        fout = open("PIN ASSIGNMENTS.txt", 'w')
        fout.write(self.getVirtualPinsString())
        if(len(self.jointPins) > 0):
            fout.write("\n\n" + self.jointPinsStr)
        if(len(self.motorPins) > 0):
            fout.write("\n\n" + self.motorPinsStr)
        fout.close()
        os.chdir(startDir)
        print self.getVirtualPinsString()
        if(len(self.jointPins) > 0):
            print "\n" + self.jointPinsStr
        if(len(self.motorPins) > 0):
            print "\n" + self.motorPinsStr
        # Write a description of where things get connected to the brain to the destination folder
        os.chdir(destDir)
        fout = open("BUILDING THE ROBOT.txt", 'w')
        fout.write(self.toString())
        fout.close()
        os.chdir(startDir)
        print "\n" + self.toString()
        
    def toString(self):
        """
        Get a printable string describing the brain
        (can be used for building purposes).

        @return: A printable String describing the attached modules in each chain
        (and which pins of brain they are on).
        @rtype: String        
        """
        res = "Building %s: " %self.getName()
        for nextChain in range(self.numChainPins):
            nextDevice = self.chainIndexToDevice(nextChain)
            if nextDevice is not None:
                res += "\nChain %i (On Pin %i):" %(nextChain, self.chainIndexToPinIndex(nextChain))
                res += "\n\t" + nextDevice.getName()
                if(isinstance(nextDevice, EModule)):
                    while nextDevice.getNextEModule() is not None:
                        nextDevice = nextDevice.getNextEModule()
                        res += "\n\t" + nextDevice.getName()
        for nextAI in range(len(self.possibleAnalogInPins)):
            nextDevice = self.analogInputs[nextAI]
            if nextDevice is not None:
                res += "\nAnalog Input (Pin %i): " %nextAI
                res += "\n\t" + nextDevice.getName()

        res += "\n\nBuilding the modules: "
        for nextChain in range(self.numChainPins):
            nextDevice = self.chainIndexToDevice(nextChain)
            if nextDevice is not None:
                if(isinstance(nextDevice, EModule)):
                    res += "\n" + nextDevice.toString()
                    while nextDevice.getNextEModule() is not None:
                        nextDevice = nextDevice.getNextEModule()
                        res += "\n" + nextDevice.toString()
                    res += "\n"

        res += "\n"
        for nextBTPin in range(len(self.possibleRXPins)):
            if self.possibleRXPins[nextBTPin]:
                if "Bluetooth RX" in self.devices[nextBTPin].getName():
                    res += "\nBluetooth RX is on pin " + str(nextBTPin)
        for nextBTPin in range(len(self.possibleTXPins)):
            if self.possibleTXPins[nextBTPin]:
                if "Bluetooth TX" in self.devices[nextBTPin].getName():
                    res += "\nBluetooth TX is on pin " + str(nextBTPin)
        return res + "\n"

    def copyComponent(self, toCopy):
        """
        Not yet implemented!
        
        Makes this brain a copy of the given EBrain.

        @param toCopy: The brain to copy.
        @type toCopy: L{EBrain}

        @return: Whether or not it was successful (whether toCopy was valid)
        @rtype: boolean
        """
        if not isinstance(toCopy, EModule):
            return False
        self.__init__()
        raise NotImplementedError("Copying a brain is not yet supported")

        return False
        
if __name__ == "__main__":

    
    # Define some pre-made EDevices
    Servo = EDevice("Generic Servo", "A_O")
    LED = EDevice("Generic LED", "D_O")
    LightSensor = EDevice("Generic Light Sensor", "A_I")
    Accel2D = EDevice("Generic Accelerometer", ["A_I", "A_I"])
    
    # Create some EModules with various EDevices attached
    x = EModule("Module 1")
    x.addEDevice(LED.clone("LED 1")) # Adds a copy of LED but with the new name
    x.addEDevice(LED.clone("LED 2"))

    y = EModule("Module 2")
    y.setEDevices([Servo.clone("Shoulder Servo"), LED.clone("Servo LED")])

    z = EModule("Module 3")
    z.setEDevices([Accel2D.clone("Accelerometer"), Servo.clone("Servo 2")])

    # Can connect modules together manually
    x.setNextEModule(y)
    x.clearNextEModule() # Take modules apart again

    # Put modules on brain
    brain = EBrain("The Brain!")
    brain.addEModule(x,0) # add x to chain 0
    brain.addEModule(y)   # add y to new chain if a pin is available, or append it to a chain otherwise
    brain.addEModule(z,1) # add z to chain 1

    #print brain.toString()
    #print brain.getVirtualPinsString()
    #print "\n"*3
    
    # Try another example
    modLED = EModule("LED")
    modLED.addEDevice(LED)
    
    brain2 = EBrain("Another Brain!")
    brain2.setChipType("ATtiny")  # Generate code for an ATtiny85 as the brain ("Arduino" is default)
    brain2.addEModule(modLED)
    brain2.addEModule(modLED.clone(None, " (2)"))    # Uses a clone of the EModule with a postfix on the name 
    brain2.addEModule(modLED.clone(None, " (3)"))    #   (postfix applies to whole chain if there is one and to its EDevices)
    
    #print brain2.toString()
    #print brain2.getVirtualPinsString()
    
    # Make a brain to actually use
    brain = EBrain("The Brain")
    ledMod = EModule("LED")
    ledDev = EDevice("LED_Device", "A_O")
    ledMod.addEDevice(ledDev.clone("Top LED (red)"))

    ledMod2 = EModule("LED")
    ledMod2.addEDevice(ledDev.clone("Bottom LED (red)"))
    
    brain.addEModule(ledMod, 0)
    brain.addEModule(ledMod2, 0)
    brain.attach(motor)
    brain.attach(joint, "second one")
    
    print brain.toString()
    print brain.getVirtualPinsString()
    print brain.getVirtualPinsStringForBT()

    print "\n" + brain.jointPinsStr
    print "\n" + brain.motorPinsStr
    # Generate code for the first brain
    #brain.generateCode()
    


    
    

    

