from api.component import Component
from connector import Tab
from utils.beam import Beam as BeamDrawing
from math import sin, pi

class Beam(Component):
  def defParameters(self):
    self.newParameter("length")

    self.newParameter("diameter")
    self.newParameter("beamwidth")

    self.newParameter("shape", 3)
    self.newParameter("phase", 0)

    self.newParameter("angle")
    self.newParameter("rangle", 90)
    self.newParameter("langle", 90)

  def defInterfaces(self):
    self.newInterface("topedge")
    self.newInterface("botedge")

  def assemble(self):
    ### Assemble the object
    try:
      rangle = self.getParameter("angle")
      langle = self.getParameter("angle")
    except KeyError:
      rangle = self.getParameter("rangle")
      langle = self.getParameter("langle")

    try:
      d = self.getParameter("diameter")
      t = self.getParameter("diameter") * sin(pi / self.getParameter("shape"))
    except KeyError:
      d = self.getParameter("beamwidth") / sin(pi / self.getParameter("shape"))
      t = self.getParameter("beamwidth") 

    b = BeamDrawing(length   = self.getParameter("length"), 
                    diameter = d,
                    langle   = langle,
                    rangle   = rangle,
                    shape    = self.getParameter("shape"),
                    phase    = self.getParameter("phase"))

    self.drawing.append(b)
    self.addConnectors((Tab(), "tabs"), "e1", "e3", min(10, t), angle = 360./self.getParameter("shape"))

    # Assign interfaces
    self.setInterface("topedge", ["r%d.e0" % n for n in range(self.getParameter("shape"))])
    self.setInterface("botedge", ["r%d.e2" % n for n in range(self.getParameter("shape"))])

if __name__ == "__main__":
  b = Beam()
  b.setParameter("length", 100)
  b.setParameter("beamwidth", 10)
  b.setParameter("shape", 5)
  b.setParameter("rangle", 45)
  b.setParameter("langle", -45)
  b.setParameter("phase", 1)
  b.make()
  import utils.display
  b.drawing.transform(relative=(0,0))
  utils.display.displayTkinter(b.drawing)
