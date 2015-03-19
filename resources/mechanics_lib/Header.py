from api.component import Component
from api.edge import *
from math import sqrt
from api.shapes import Rectangle

class Header(Component):

  def defParameters(self):
      self.newParameter("nrows", 0)
      self.newParameter("ncols", 0)
      self.newParameter("rowsep", 2.54)
      self.newParameter("colsep", 2.54)
      self.newParameter("diameter", 0.5)

  def assemble(self):
    d = self.getParameter("diameter")
    hole = Rectangle(d, d).transform(origin = (-d/2., -d/2.))

    for i in range(self.getParameter("nrows")):
      for j in range(self.getParameter("ncols")):
        dx = (j - (self.getParameter("ncols")-1)/2.)*self.getParameter("colsep")
        dy = (i - (self.getParameter("nrows")-1)/2.)*self.getParameter("rowsep")
        self.drawing.append(hole, 
                            "hole%02d%02d" % (i,j), 
                            origin=(dx, dy))

if __name__ == "__main__":
    import utils.display
    h = Header()
    h.setParameter("nrows", 11)
    h.setParameter("ncols", 2)
    h.setParameter("rowsep", 0.1 * 25.4)
    h.setParameter("colsep", 0.6 * 25.4)
    h.make()
    h.drawing.transform(origin = (0, 0), scale=10)
    utils.display.displayTkinter(h.drawing)
