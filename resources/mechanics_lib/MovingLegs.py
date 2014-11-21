from api.component import Component
from connector import Tab
from api.edge import Flex, Flat

class MovingLegs(Component):
  def defComponents(self):
    self.addSubcomponent("servo", "Servo")
    self.addSubcomponent("move", "FourBarLegs")

  def defParameters(self):
    ### beam parameters inherited by default
    self.newParameter("servo")
    self.newParameter("length")
    self.newParameter("flip", False)
    self.newParameter("height")
    self.newParameter("flexwidth", 5)
    self.newParameter("leg.beamwidth", 7)

  def defInterfaces(self):
    self.newInterface("topedge")
    self.newInterface("topright")
    self.newInterface("botedge")

  def defConstraints(self):
    ### Set specific relationships between parameters
    self.addConstraint(("move", "height"), "height")
    self.addConstraint(("move", "length"), "length")
    self.addConstraint(("move", "flexwidth"), "flexwidth")
    self.addConstraint(("move", "leg.beamwidth"), "leg.beamwidth")

    self.addConstraint(("servo", "length"), "length")
    self.addConstraint(("servo", "servo"), "servo")
    self.addConstraint(("servo", "flip"), "flip")

    self.addConstraint(("move", "flexlengthx"), "servo", '2*x.getParameter("hornheight")')
    self.addConstraint(("move", "depth"), "servo", 'x.getParameter("motorwidth")')

  def assemble(self):
    ### Assemble the object
    self.append("servo", "servo")

    self.attach(("servo", "servo", "slotedge"), 
                ("move", "move", "topedge") , Flat())

    self.addTabs((Tab(), "t1", min(10, self.getParameter("servo").getParameter("motorheight"))), 
                 ("move", "move", "botedge"),
                 ("servo", "servo", "insideedge"),
                 (Flat(), Flex()))

    self.setInterface("topedge", self.getInterfaces("servo", "topedge", "servo"))
    self.setInterface("topright", self.getInterfaces("servo", "topedge", "servo", index=1))
    self.setInterface("botedge", self.getInterfaces("servo", "botedge", "servo"))

if __name__ == "__main__":

  f = MovingLegs()

  from utils.dimensions import tgy1370a

  f.setParameter("servo", tgy1370a)
  f.setParameter("height", 25)
  f.setParameter("length", 40)

  f.makeOutput("output/movinglegs", display=True)

