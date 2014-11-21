from api.component import Component
from ELibrary import motor

class Servo(Component):
  def defComponents(self):
    self.addSubcomponent("beam", "RectBeam")
    self.addSubcomponent("hole", "Cutout")

    self.addEComponent(motor)

  def defParameters(self):
    ### beam parameters inherited by default
    self.newParameter("length")
    self.newParameter("servo")
    self.newParameter("flip", False)

    '''
    self.delParameter("depth")
    self.delParameter("width")
    self.delParameter("noflap")
    self.delParameter("dx")
    self.delParameter("dy")
    self.delParameter("center")
    '''

  def defConstraints(self):
    ### Set specific relationships between parameters
    self.addConstraint(("beam", "length"), "length")
    self.addConstraint(("beam", "width"), "servo", 'x.getParameter("motorheight")')
    self.addConstraint(("beam", "depth"), "servo", 'x.getParameter("motorwidth")')
    self.addConstConstraint(("beam", "noflap"), True)

    self.addConstraint(("hole", "dx"), "servo", 'x.getParameter("motorwidth") * 0.99')
    self.addConstraint(("hole", "dy"), "servo", 'x.getParameter("motorlength")')
    self.addConstConstraint(("hole", "center"), (0.5, 0))

  def assemble(self):
    w = self.getComponent("beam").getParameter("width")
    d = self.getComponent("beam").getParameter("depth")
    l = self.getComponent("beam").getParameter("length")

    dx = w + d + w + d/2.
    dy = l/2. - self.getParameter("servo").getParameter("hornoffset")

    self.decorate("beam", "r3", "hole", offset=(d/2., dy), mode="hole")

    if self.getParameter("flip"):
      dy = l - dy - self.getParameter("servo").getParameter("motorlength")

    self.getComponent("beam").drawing.append(self.getComponent("hole").drawing.transform(origin=(dx, dy)), "hole")
    self.drawing.append(self.getComponent("beam").drawing, "beam")

  def defInterfaces(self):
    self.inheritInterface("topedge", ("beam", "topedge"))
    self.inheritInterface("botedge", ("beam", "botedge"))
    self.inheritInterface("slotedge", ("beam", "slotedge"))
    self.newInterface("insideedge", "beam.r2.e3")

if __name__ == "__main__":

  f = Servo()
  f.toYaml("output/servo.yaml")

  from utils.dimensions import tgy1370a
  f.setParameter("servo", tgy1370a)
  f.setParameter("length", 48)
  f.setParameter("flip", True)
  '''
  f.setParameter("depth", 9)
  f.setParameter("width", 14)
  '''

  f.make()
  f.drawing.graph.toSTL("output/test.stl")
  f.drawing.graph.toDXF("output/test.dxf")
  f.drawing.transform(origin=(100,350))
  import utils.display
  utils.display.displayTkinter(f.drawing)
