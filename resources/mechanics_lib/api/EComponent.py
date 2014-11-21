
class EComponent:
    """
    A class to define an interface to electrical components.  For example,
    L{EBrain}, L{EModule}, and L{EDevice} may all inherit from this class.

    Classes inheriting from EComponent should override the following:
      - L{attach}
      - L{copyComponent}
    """

    def __init__(self, name = None):
        """
        Initializes an empty EComponent which has a name but no type.
        """
        if name is None or not self.setName(name):
            self.clearName()
    
    def setName(self, inName):
        """
        Sets the name of the EComponent.
        
        @param inName: Must be at least 1 character long.
        @type inName: String
        
        @return: Returns True if successful, False if name is None or too short.
        @rtype: boolean
        """
        if inName is None or len(inName) < 1:
            self.printError("Names must be at least one character long! (" + str(inName) + " was given)")
            return False
        self.name = inName
        return True

    def getName(self):
        """
        Gets the name of the EComponent.

        @return: The current name of the device.
        @rtype: String
        """
        return self.name
    
    def clearName(self):
        """
        Clears the name of the EDevice (sets to default).
        """
        self.name = "Default Name"

    def attach(self, toAttach):
        """
        Attaches the given EComponent to this EComponent.

        Subclasses of EComponent should override this method for appropriate behavior.

        If this EComponent is empty, it becomes a copy of toAttach.

        If this EComponent is not empty, it attemps to attach toAttach.
        For example, see the attach method of L{EBrain}, L{EModule}, or L{EDevice} for details.

        @param toAttach: The EComponent to attach
        @type toAttach: A subclass of EComponent

        @return: Whether or not it was successfull.
        @rtype: bool
        """
        # Note this implementation is only called if subclass doesn't override it
        # So this implementation assumes this EComponent is empty
        if not isinstance(toAttach, EComponent):
            return False
        self.__class__ = toAttach.__class__
        self.copyComponent(toAttach)

    def copyComponent(self, toCopy):
        """
        Makes this component a copy of the given component.

        Classes inheriting from EComponent should override this!
        This default implementation raises an error.
        """
        raise NotImplementedError("The subclass of EComponent you are using has no copyComponent() method defined!")
        
    def _printError(self, message):
        print("*** ERROR: " + message + " *** ")




