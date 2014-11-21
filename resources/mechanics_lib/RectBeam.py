from api.component import Component
from connector import Tab
from utils.rectbeam import RectBeam as BeamDrawing
from math import sin, pi

class RectBeam(Component):
  def defParameters(self):
    self.newParameter("length")
    self.newParameter("width")
    self.newParameter("depth")
    self.newParameter("phase", 0)

    self.newParameter("angle")
    self.newParameter("rangle", 90)
    self.newParameter("langle", 90)

    self.newParameter("noflap", False)
    self.newParameter("faces")

  def defInterfaces(self):
    self.newInterface("topedge")
    self.newInterface("botedge")
    self.newInterface("tabedge")
    self.newInterface("slotedge")

  def assemble(self):
    try:
      rangle = self.getParameter("angle")
      langle = self.getParameter("angle")
    except KeyError:
      rangle = self.getParameter("rangle")
      langle = self.getParameter("langle")

    try:
      faces = self.getParameter("faces")
    except KeyError:
      faces = None

    ### Assemble the object
    b = BeamDrawing(self.getParameter("length"), 
                    (self.getParameter("width"), self.getParameter("depth")),
                    langle   = langle,
                    rangle   = rangle,
                    phase    = self.getParameter("phase"),
                    faces    = faces)

    self.drawing.append(b)

    tabwidth = self.getParameter("width")
    if self.getParameter("phase") % 2:
      tabwidth = self.getParameter("depth")

    if faces is None:
      t = Tab()
      t.setParameter("noflap", self.getParameter("noflap"))
      self.addConnectors((t, "tabs"), "e1", "e3", min(10, tabwidth), angle=90)

    self.drawing.edges["e3"].flip()

    self.setInterface("topedge", ["r%d.e0" % n for n in range(4)])
    self.setInterface("botedge", ["r%d.e2" % n for n in range(4)])
    self.setInterface("tabedge", "tabstab.e2")
    self.setInterface("slotedge", "e3")

    #Define interfaces

if __name__ == "__main__":
  b = RectBeam()
  b.setParameter("length", 30)
  b.setParameter("width", 10)
  b.setParameter("depth", 5)
  b.setParameter("rangle", -60)
  b.setParameter("langle", 30)
  b.setParameter("phase", 4)
  b.setParameter("faces", range(0,2))
  b.makeOutput("output/beam")
  import utils.display
  b.drawing.transform(relative=(0,0))
  utils.display.displayTkinter(b.drawing)
