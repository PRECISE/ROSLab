from api.component import Component
import api.tabs

class Connector(Component):
    """
    A class representing a Connector.
    A interface is the edges in a drawing that fold up together. Interfaces is a dictionary
    with keys being a tuple pair of Edge names (the two Edges that will be joints) and the value
    being an instance of the Connector component that will be joining them. Every Connector class
    will arbitrarily fit into the same slot, so a superclass Connector has been defined to account
    for that, and the default thickness/depth parameters that all Connectors should have.

    The Connectors are just instances of Component that have a specialized make method to create
    a drawing based off the Drawing instances of the same name in the file Drawing.
    
    """
    def __init__(self):
        Component.__init__(self)
        self.newParameter("thickness")
        self.newParameter("depth")
        self.newParameter("noflap", False)
        self.mirror = False
    
    def slots(self):
        return api.tabs.BeamSlots(self.getParameter("thickness"), self.getParameter("depth"), opts = {"mirror": self.mirror})


class Tab(Connector):
    """
    A class representing a Tab.
    """
    def __init__(self):
        Connector.__init__(self)

    def make(self):
        Connector.make(self)
        self.drawing.append(api.tabs.BeamTabs(self.getParameter("thickness"), self.getParameter("depth"), opts = {"noflap": self.getParameter("noflap")}))
        

class ThinTab(Connector):
    """
    A class representing a Tab.
    """
    def __init__(self):
        Connector.__init__(self)
        self.mirror = True

    def make(self):
        Connector.make(self)
        self.drawing.append(api.tabs.ThinTabs(self.getParameter("thickness"), self.getParameter("depth")))
        

class Hook(Connector):
    """
    A class representing a Hook.
    """
    def __init__(self):
        Connector.__init__(self)

    def make(self):
        Connector.make(self)
        self.drawing.append(api.tabs.Hook(self.getParameter("thickness"), self.getParameter("depth")))
                            

class HookTab(Connector):
    """
    A class representing a Hook Tab.
    """
    def __init__(self):
        Connector.__init__(self)

    def make(self):
        Connector.make(self)
        self.drawing.append(api.tabs.HookTab(self.getParameter("thickness"), self.getParameter("depth")))
