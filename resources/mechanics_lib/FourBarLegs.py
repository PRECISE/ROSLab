from api.component import Component
from connector import Tab
from api.edge import Flat, Cut
from math import sqrt

class FourBarLegs(Component):
  def defComponents(self):
    self.addSubcomponent("mount", "Rectangle")
    self.addSubcomponent("spacer", "Rectangle")
    self.addSubcomponent("fakeleg", "Rectangle")

    self.addSubcomponent("linkage", "FourBar")
    self.addSubcomponent("leg", "PointedLeg")
    self.addSubcomponent("hole", "Cutout")
    self.addSubcomponent("split", "SplitEdge")

  def defParameters(self):
    self.newParameter("height")
    self.newParameter("length")
    self.newParameter("depth")

    self.newParameter("flexlengthx")
    self.newParameter("flexwidth", 5)
    self.newParameter("leg.beamwidth", 7)

  def defInterfaces(self):
    self.newInterface("topedge")
    self.newInterface("botedge")

  def defConstraints(self):
    ### Set specific relationships between parameters
    self.addConstraint(("leg", "length"), "height")
    self.addConstraint(("leg", "beamwidth"), "leg.beamwidth")

    self.addConstraint(("linkage", "depth"), "depth")
    self.addConstraint(("linkage", "flexwidth"), "flexwidth")
    self.addConstraint(("linkage", "flexlengthx"), "flexlengthx")
    self.addConstraint(("linkage", "flexlengthy"), 
        ("length", "leg.beamwidth"), " (x[0] - %f * x[1]) / 2." % sqrt(2))

    self.addConstConstraint(("hole", "d"), 1.5)
    self.addConstConstraint(("hole", "center"), (0.5, 0.5))

    self.addConstraint(("mount", "l"), "length")
    self.addConstConstraint(("mount", "w"), 0.1)

    self.addConstraint(("spacer", "l"), "depth")
    self.addConstraint(("spacer", "w"), "leg.beamwidth", " %f * x" % sqrt(2))

    self.addConstraint(("fakeleg", "l"), "height")
    self.addConstraint(("fakeleg", "w"), "leg.beamwidth", " %f * x" % sqrt(2))

    self.addConstraint(("split", "botlength"), ("flexwidth", "length"), " (x[1],)")
    self.addConstraint(("split", "toplength"), ("flexwidth", "length"), " (x[0], x[1]-2*x[0], x[0])")

  def assemble(self):
    ### Assemble the object

    self.append("mount", "topmount")
    self.attach(("mount", "topmount", "t"),
                ("split", "topsplit", "botedge"), Flat())

    self.attach(("split", "topsplit", "topedge.0"),
                ("linkage", "l1", "topedge"), Flat())
    self.attach(("split", "topsplit", "topedge.2"),
                ("linkage", "l2", "botedge"), Flat())

    # TODO: make cleaner abstsraction
    self.drawing.attach((self.getInterfaces("linkage", "topedge", "l2"),
                         self.getInterfaces("linkage", "botedge", "l1")), 
                        self.getComponent("split").drawing, 
                        (self.getInterfaces("split", "topedge.0"), 
                         self.getInterfaces("split", "topedge.2")), 
                        "botsplit", Flat())

    self.attach(("split", "botsplit", "botedge"), 
                ("mount", "botmount", "t"),
                Flat())

    # TODO: make cleaner abstsraction
    self.getComponent("hole").drawing.transform(origin = (self.getParameter("depth")/2., sqrt(2) * self.getParameter("leg.beamwidth")/2.))
    self.getComponent("spacer").drawing.append(self.getComponent("hole").drawing, "hole")

    self.attach(("linkage", "l2", "output"),
                ("spacer", "s1", "b"), Flat())

    self.attach(("spacer", "s1", "r"), 
                ("leg", "leg1", "diag"), Flat())

    self.attach(("linkage", "l1", "output"),
                ("spacer", "s2", "t"), Flat())

    self.attach(("spacer", "s2", "r"), 
                ("fakeleg", "leg2", "r"), Flat())

    self.addTabs((Tab(), "tab1", min(10, sqrt(2) * self.getParameter("leg.beamwidth"))), 
                 None,
                 ("fakeleg", "leg2", "t"),
                 (Flat(), Cut()))

    self.setInterface("topedge", self.getInterfaces("mount", "b", "topmount"))
    self.setInterface("botedge", self.getInterfaces("mount", "b", "botmount"))

if __name__ == "__main__":

  f = FourBarLegs()

  f.setParameter("flexlengthx", 20)
  f.setParameter("flexwidth", 5)
  f.setParameter("depth", 9)
  f.setParameter("height", 25)
  f.setParameter("length", 40)
  f.setParameter("leg.beamwidth", 5)

  f.make()
  f.drawing.transform(origin=(200,50))
  import utils.display
  utils.display.displayTkinter(f.drawing)

