from api.component import Component
from brains import Brains
from halfant import HalfAnt
from connector import Tab
from api.edge import Fold

class Bot(Component):
  def defComponents(self):
    #self.addComponent("brain", Brains, None)
    #self.addComponent("half", HalfAnt, None)
    self.addComponent("brain", Brains, None)
    self.addComponent("half1", HalfAnt, None)
    self.addComponent("half2", HalfAnt, None)


  def defSubParameters(self):
    #self.setSubParameter("half", "width", self.getParameter("brain").getParameter("width") + self.getParameter("servo").getParameter("motorheight") * 2)
    self.setSubParameter("brain", "length", 48)
    self.setSubParameter("brain", "height", 25)
    self.setSubParameter("brain", "hardware", proMini)
    self.setSubParameter("half1", "width", self.getParameter('brain').getParameter('width'))
    self.setSubParameter("half2", "width", self.getParameter('brain').getParameter('width'))


  def assemble(self):
    ### Assemble the object

    # Insert the main beam (first append and then attach things to that)
    #self.append("brain", "core")
    self.append("brain", "core")


    #self.attach(("brain", "core", "botright"),
    #            ("half", "front", "topright"), Fold(-180) )
    #self.attach(("brain", "core", "topleft"),
    #            ("half", "back", "topright"), Fold(-180) )
    self.attach(("brain", "core", "botright"), ("half1", "half1", "topright"), Fold(-180))
    self.attach(("brain", "core", "topleft"), ("half2", "half2", "topright"), Fold(-180))


    #self.addTabs((Tab(), "tabfront", 9),
    #             ("half", "front", "botedge.2"),
    #             ("half", "back", "topedge.3"))
    #self.addTabs((Tab(), "tabback", 9),
    #             ("half", "back", "botedge.2"),
    #             ("half", "front", "topedge.3"))
    self.addTabs((Tab(), "tabhalf1", 9), ("half1", "half1", "botedge.2"), ("half1", "half1", "topedge.3")
    self.addTabs((Tab(), "tabhalf2", 9), ("half2", "half2", "botedge.2"), ("half2", "half2", "topedge.3")


if __name__ == "__main__":

  import sys

  print "Loading robot definition...",
  sys.stdout.flush()
  f = Bot()
  print "done."

  from utils.dimensions import tgy1370a, proMini
  #f.setParameter("servo", tgy1370a)
  #f.setParameter("brain", proMini)
  #f.setParameter("length", 48)
  #f.setParameter("height", 25)
  f.setParameter("brain", proMini)
  f.setParameter("length", 48)
  f.setParameter("height", 25)


  print "Compiling robot designs...",
  sys.stdout.flush()
  f.make()
  print "done."
  print "Generating cut-and-fold pattern... ",
  sys.stdout.flush()
  f.drawing.transform(relative=(0,0))
  f.drawing.toSVG("output/ant.svg", mode="Corel")
  print "done."
  print "Generating autofolding pattern... ",
  sys.stdout.flush()
  f.drawing.graph.toDXF("output/ant.dxf")
  print "done."
  print "Generating 3D model... ",
  sys.stdout.flush()
  f.drawing.graph.toSTL("output/ant.stl")
  print "done."
  print
  print "Electrical connections:"
  print "======================:"

  if f.ecomponent is not None:
    f.ecomponent.generateCode()

  print
  print
  print "Happy roboting!"
