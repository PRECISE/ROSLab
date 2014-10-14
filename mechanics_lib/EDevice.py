
from api.EComponent import EComponent

class EDevice(EComponent):
    """
    A class representing an electrical device.
    For example, an EDevice may be an LED, a servo, a light sensor, etc.
    
    It stores the name of the device and the type of pin(s) it needs.

    @cvar PIN_TYPES: A list of possible pin types (each type is a String).
    """

    # Define allowed pin types
    PIN_TYPES = ["D_I", "D_O", "A_I", "A_O", "SERVO"]

    def __init__(self, name = None, inPinTypes = None):
        """
        Initialize the EDevice.
        
        @param name: The desired name.  If none is given, a default is used.
        @type name: String
        
        @param inPinTypes: The types of pins the device will need.
            If none are provided it will be a blank device.
        @type inPinTypes: A list of needed types or a single type.
            Each type is a string and must be in L{PIN_TYPES}
        """
        EComponent.__init__(self, name)
        self.clearPinTypes();
        if inPinTypes is not None:
            self.addPinTypes(inPinTypes)

    def setName(self, name):
        """
        Sets the name of the EDevice.
        
        @param name: Must be at least 1 character long.
        @type name: String
        
        @return: Returns True if successful, False if name is None or too short.
        @rtype: boolean
        """
        if name is None or len(name) < 1:
            return False
        self.name = name
        return True

    def clone(self, inName = None, postfix = None):
        """
        Returns a clone of this EDevice.
        Optionally, the clone may be given a new name or postfix.
        
        @param inName: The name of the cloned device,.
            If nothing is provided the name will be unchanged.
        @type inName: String
        
        @param postfix: A postfix to add to the clone's name (optional).
            A space will be inserted before the postfix.
        @type postfix: String
        """
        res = EDevice()
        newName = self.name
        if inName is not None:
            newName = inName
        if postfix is not None:
            newName += " " + postfix
        res.setName(newName)
        
        for nextType in self.getPinTypes():
            res.addPinType(nextType)
        return res

    def copyComponent(self, toCopy):
        """
        Makes this device a copy of the given EDevice.

        @param toCopy: The device to copy.
        @type toCopy: L{EDevice}

        @return: Whether or not it was successful (whether toCopy was valid)
        @rtype: boolean
        """
        if not isinstance(toCopy, EDevice):
            return False
        self.__init__()
        self.setName(toCopy.getName())
        
        for nextType in toCopy.getPinTypes():
            self.addPinType(nextType)
        return True
    
    def getName(self):
        """
        Gets the name of the EDevice.

        @return: The current name of the device.
        @rtype: String
        """
        return self.name
    
    def clearName(self):
        """
        Clears the name of the EDevice (sets to default).
        """
        self.name = "No Name"
    
    def clearPinTypes(self):
        """
        Deletes all stored pins from the device.
        """
        self.pinTypes = []
        
    def addPinType(self, inPinType):
        """
        Adds the given pin type as a needed pin for the device.

        @param inPinType: Must be an element of L{PIN_TYPES}.
        @type inPinType: String

        @return: False if inPinType is invalid, True otherwise
        @rtype: boolean
        """
        if inPinType not in EDevice.PIN_TYPES:
            return False

        # Everything seems to be in order... make the assignment
        self.pinTypes.append(inPinType)
        return True
    
    def addPinTypes(self, inPinTypes):
        """
        Adds all of the given pin types as required pins for the device.

        @param inPinTypes: Each element of inPinTypes must be an element of L{PIN_TYPES}
            If any element is invalid, no elements are added
        @type inPinTypes: List or Tuple of Strings
        
        @return: False if any element of inPinTypes is invalid, True otherwise.
        @rtype: boolean
        """
        # If single element, make it a list
        if not isinstance(inPinTypes, (list, tuple)):
            inPinTypes = [inPinTypes]
        # Make sure every element is an acceptable pin type
        if sum(map(lambda x: (x not in EDevice.PIN_TYPES), inPinTypes)) > 0:
            return False
        
        # Everything seems to be in order... make the assignments
        for nextType in inPinTypes:
            self.pinTypes.append(nextType)
            
        return True

    def getPinTypes(self):
        """
        Gets the required pin types of the device

        @return: The pins assocated with the device.
            Each type is one of the possible types defined in L{PIN_TYPES}
        @rtype: List of Strings
        """
        return self.pinTypes[:] 


    def attach(self, toAttach):
        """
        Does nothing since it doesn't make sense to attach anything to a device.

        Prints an error message.

        @return: False
        @rtype: bool
        """
        self._printError("Cannot attach anything to an EDevice!")
        return False
        
    def toString(self):
        """
        Get a string describing the EDevice (usually for printing purposes).

        @return: A printable String containing the name and pin types of the device.
        @rtype: String
        """
        res = "EDevice '%s' " % self.name
        res += " Uses %i pin" %len(self.pinTypes)
        if len(self.pinTypes) > 1:
            res += "s"
        res += ": %s" %str(self.pinTypes)
        return res

if __name__ == '__main__':
    LED = EDevice()
    print LED.toString()
    LED.setName("LED")
    LED.addPinType("D_O")
    print LED.toString()
    LED.addPinTypes(["D_I", "A_I"])
    print LED.toString()
    LED.addPinType("test")
    print LED.toString()
    print LED.clone().toString()
    print LED.clone("Cloned!").toString()

    
        
        
        
