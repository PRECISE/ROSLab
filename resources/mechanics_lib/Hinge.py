###Hexaflexagon Arm
from api.component import Component
from api.edge import Flex

class Hinge(Component):
    def defComponents(self):
        self.addSubcomponent("bot", "Tetrahedron")
        self.addSubcomponent("top", "Tetrahedron")
    
    def defInterfaces(self):
        self.newInterface("topedge")
        self.newInterface("botedge")
        
    def defParameters(self):
        self.newParameter("top", 0)
        self.newParameter("bot", 0)
        self.newParameter("perimeter")

    def defConstraints(self):
        self.addConstraint(("top", "perimeter"), "perimeter")
        self.addConstraint(("top", "start"), "top")
        self.addConstConstraint(("top", "end"), 1)
        self.addConstConstraint(("bot", "start"), 1)
        self.addConstraint(("bot", "end"), "bot")
        self.addConstraint(("bot", "perimeter"), "perimeter")

        # XXX Hack : makes things awkward with different fractions in and out
        self.addConstraint(("top", "min"), ("top", "bot"), "min(x)")
        self.addConstraint(("bot", "min"), ("top", "bot"), "min(x)")

    def defConnections(self):
        # XXX Hack : not all edges have the same fold angle 
        self.addConnection(("top", "endedge"),
                           ("bot", "startedge"),
                           "Flex", angle=-70.5)

    def defInterfaces(self):
        self.inheritInterface("topedge", ("top", "startedge"))
        self.inheritInterface("botedge", ("bot", "endedge"))

if __name__ == "__main__":
    h = Hinge()
    h.setParameter("perimeter", 400)
    h.setParameter("top", -.25)
    h.setParameter("bot", .25)
    h.make()
    h.drawing.transform(relative=(0,0))
    h.drawing.graph.toSTL("output/hinge.stl")
    import utils.display
    utils.display.displayTkinter(h.drawing)
