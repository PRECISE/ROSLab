from api.component import Component
from api.shapes import Rectangle as Rect

class Rectangle(Component):

  def defParameters(self):
    self.newParameter("l")
    self.newParameter("w")

  def defInterfaces(self):
    self.newInterface("t")
    self.newInterface("b")
    self.newInterface("l")
    self.newInterface("r")

  def assemble(self):
    dx = self.getParameter("l")
    dy = self.getParameter("w")
    self.drawing = Rect(dx, dy)

    self.setInterface("b", "e0")
    self.setInterface("r", "e1")
    self.setInterface("t", "e2")
    self.setInterface("l", "e3")

if __name__ == "__main__":
    import utils.display
    h = Rectangle()
    h.setParameter("l", 10)
    h.setParameter("w", 10)
    h.make()
    h.drawing.transform(origin = (0, 0), scale=10)
    utils.display.displayTkinter(h.drawing)
