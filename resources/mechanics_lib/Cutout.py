from api.component import Component
from api.shapes import Rectangle

class Cutout(Component):

  def defParameters(self):
      self.newParameter("dx")
      self.newParameter("dy")
      self.newParameter("d")
      self.newParameter("center", (0,0))

  def assemble(self):
    try:
      dx = self.getParameter("d")
      dy = self.getParameter("d")
    except KeyError:
      dx = self.getParameter("dx")
      dy = self.getParameter("dy")

    center = self.getParameter("center")
    self.drawing.append(Rectangle(dx, dy),origin = (-dx * center[0], -dy * center[1]))

if __name__ == "__main__":
    import utils.display
    h = Cutout()
    h.setParameter("d", 10)
    h.setParameter("center", (.5, .5))
    h.make()
    h.drawing.transform(origin = (0, 0), scale=10)
    utils.display.displayTkinter(h.drawing)
