from api.component import Component
from api.edge import Fold

class LegPair(Component):
  def defComponents(self):
    self.addSubcomponent("fixed", "FixedLegs", inherit=True, prefix=None)
    self.addSubcomponent("move", "MovingLegs", inherit=True, prefix=None)

  def defParameters(self):
    self.delParameter("depth")
    self.newParameter("width")

  def defConstraints(self):
    ### Set specific relationships between parameters
    self.addConstraint(("fixed", "length"), "width") # set fixed.length <- width
    self.addConstraint(("fixed", "depth"), "servo", "x.getParameter('motorwidth')")

  def assemble(self):
    ### Assemble the object
    self.append("move", "move")

    self.getComponent("fixed").drawing.invertEdges()

    self.attach(("move", "move", "botedge.3"),
                ("fixed", "fixed", "topedge.2"), Fold(90))

  def defInterfaces(self):
    self.inheritInterface("topedge", ("move", "topedge"))
    self.inheritInterface("botedge", ("fixed", "botedge"))

if __name__ == "__main__":

  f = LegPair()
  f.toYaml("output/legpair.yaml")

  from utils.dimensions import tgy1370a

  f.setParameter("servo", tgy1370a)
  f.setParameter("height", 30)
  f.setParameter("length", 48)
  f.setParameter("width", 28+19)
  f.setParameter("leg.beamwidth", 10)

  f.makeOutput("output/halfant", display=True)

