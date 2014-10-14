from api.component import Component

class Ant(Component):
  def defComponents(self):
    self.addSubcomponent("brain", "Brains", inherit=True, prefix=None)
    self.addSubcomponent("front", "LegPair", inherit=True, prefix=None)
    self.addSubcomponent("back", "LegPair", inherit=True, prefix=None)

  def defParameters(self):
    self.delParameter("width")

  def defConstraints(self):
    self.addConstraint(("front", "width"), ("brain", "servo"), "x[0].getParameter('width') + \
                                                               x[1].getParameter('motorheight') * 2")
    self.addConstraint(("back", "width"), ("brain", "servo"), "x[0].getParameter('width') + \
                                                               x[1].getParameter('motorheight') * 2")
  def defConnections(self):
    self.addConnection(("brain", "botright"),
                       ("front", "topedge.1"),
                       "Fold",
                       angle=-180)
    self.addConnection(("brain", "topleft"),
                       ("back", "topedge.1"),
                       "Fold",
                       angle=-180)
    self.addConnection(("front", "botedge.2"),
                       ("back", "topedge.3"),
                       "Tab",
                       name="tabfront", depth=9)
    self.addConnection(("back", "botedge.2"),
                       ("front", "topedge.3"),
                       "Tab",
                       name="tabback", depth=9)

if __name__ == "__main__":
  f = Ant()
  f.toYaml("output/ant.yaml")

  from utils.dimensions import tgy1370a, proMini
  f.setParameter("servo", tgy1370a)
  f.setParameter("brain", proMini)

  f.setParameter("length", 48)
  f.setParameter("height", 25)
  f.setParameter("leg.beamwidth", 10)

  f.makeOutput("output/ant", display=True)
