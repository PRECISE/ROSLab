from api.component import Component
from api.edge import Flat
from EBrain import EBrain

class Brains(Component):
  def defComponents(self):
    self.addSubcomponent("brain", "RectBeam")
    self.addSubcomponent("prog", "RectBeam")
    self.addSubcomponent("bt", "RectBeam")
    self.addSubcomponent("rest", "RectBeam")
    self.addSubcomponent("header", "Header")

    ec = EBrain("brains")
    ec.addBluetooth()
    self.addEComponent(ec)

  def defParameters(self):
    ### beam parameters inherited by default
    self.newParameter("brain")
    self.newParameter("length")

  def defConstraints(self):
    ### Set specific relationships between parameters
    # XXX: doesn't check to see whether minimum length is satisfied
    self.faces = range(1,4)
    self.proglength = 6
    self.maxbt = 10

    def getBrainParameter(p):
      return "brain", "x.getParameter('%s')" % p

    self.addConstraint(("prog", "width"), *getBrainParameter("width"))
    self.addConstraint(("prog", "depth"), *getBrainParameter("height"))
    self.addConstConstraint(("prog", "faces"), self.faces)
    self.addConstConstraint(("prog", "length"), self.proglength)

    self.addConstraint(("brain", "width"), *getBrainParameter("width"))
    self.addConstraint(("brain", "depth"), *getBrainParameter("height"))
    # XXX could be negative?
    self.addConstraint(("brain", "length"), "brain", "x.getParameter('length') - %d" % self.proglength)

    self.addConstraint(("bt", "width"), *getBrainParameter("width"))
    self.addConstraint(("bt", "depth"), *getBrainParameter("height"))
    # XXX could be negative?
    self.addConstraint(("bt", "length"), ("brain", "length"), 
                                         "min(x[1] - x[0].getParameter('length'), %d)" % self.maxbt)
    self.addConstConstraint(("bt", "faces"), self.faces)

    self.addConstraint(("rest", "width"), *getBrainParameter("width"))
    self.addConstraint(("rest", "depth"), *getBrainParameter("height"))
    self.addConstraint(("rest", "length"), ("brain", "length"), 
                                           "max(x[1] - x[0].getParameter('length') - %d, 0)" % self.maxbt)

    self.addConstraint(("header", "nrows"), *getBrainParameter("nrows"))
    self.addConstraint(("header", "ncols"), *getBrainParameter("ncols"))
    self.addConstraint(("header", "rowsep"), *getBrainParameter("rowsep"))
    self.addConstraint(("header", "colsep"), *getBrainParameter("colsep"))

    '''
    if length < brainlength:
      raise ValueError("Brain module too short")
    '''

  def assemble(self):
    ### Assemble the object

    w = self.getComponent("brain").getParameter("width")
    d = self.getComponent("brain").getParameter("depth")
    l = self.getComponent("brain").getParameter("length")
    dx = w + d + w + d
    dx2 = d + w/2.

    # XXX TODO: Where did 18 come from?
    dy = 18

    self.decorate("prog", "r2", "header", offset=(w/2., dy), mode="hole")
    self.decorate("brain", "r2", "header", offset=(w/2., dy - self.proglength), mode="hole")

    self.append("prog", "prog")
    edge = "edge.%d.%d" % (min(self.faces), max(self.faces)+1)

    self.attach(("prog", "prog", "bot"+edge), 
                ("brain", "brain", "top"+edge), Flat() )

    if self.getComponent("bt").getParameter("length") > 0:
      self.attach(("brain", "brain", "bot"+edge), 
                  ("bt", "bt", "top"+edge), Flat() )

    if self.getComponent("rest").getParameter("length") > 0:
      self.attach(("bt", "bt", "bot"+edge), 
                  ("rest", "rest", "top"+edge), Flat() )

    self.drawing.append(self.getComponent("header").drawing,origin=(dx2, dy))

    self.setInterface("topedge",  self.getInterfaces("prog", "topedge", "prog"))
    self.setInterface("topright", self.getInterfaces("prog", "topedge", "prog", index=1))
    self.setInterface("topleft",  self.getInterfaces("prog", "topedge", "prog", index=3))

    bot = "brain"
    if self.getComponent("bt").getParameter("length") > 0:
      bot = "bt"
    if self.getComponent("rest").getParameter("length") > 0:
      bot = "rest"

    self.setInterface("botedge",  self.getInterfaces(bot, "botedge", bot))
    self.setInterface("botright", self.getInterfaces(bot, "botedge", bot, index=1))
    self.setInterface("botleft",  self.getInterfaces(bot, "botedge", bot, index=3))

  def defInterfaces(self):
    self.newInterface("topedge")
    self.newInterface("topright")
    self.newInterface("topleft")
    self.newInterface("botedge")
    self.newInterface("botright")
    self.newInterface("botleft")

if __name__ == "__main__":

  f = Brains()
  f.toYaml("output/brains.yaml")

  # f.setParameter("depth", 10)
  f.setParameter("length", 39)
  from utils.dimensions import proMini
  f.setParameter("brain", proMini)

  f.make()
  f.drawing.graph.toSTL("output/test.stl")
  f.drawing.graph.toDXF("output/test.dxf")
  import utils.display
  utils.display.displayTkinter(f.drawing)
  f.drawing.toSVG("output/test.svg", mode="Corel")
