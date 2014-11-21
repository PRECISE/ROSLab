from api.component import Component
from api.shapes import Face
from numpy import array, cumsum

class SplitEdge(Component):
    def defParameters(self):
        self.newParameter("toplength")
        self.newParameter("botlength")
        self.newParameter("tolerance", 0.001)

    def defInterfaces(self):
        self.newInterface("topedge")
        self.newInterface("botedge")

    def assemble(self):
        t = cumsum(array(self.getParameter("toplength"))[::-1])
        b = cumsum(array(self.getParameter("botlength"))[::-1])
        if t[-1] != b[-1]:
          raise ValueError("SplitEdge lengths not equal: %s <> %s" % (repr(t), repr(b)))

        TOL = self.getParameter("tolerance")
        pts = [(x, 0) for x in b]
        pts += [(x, TOL) for x in t[::-1]]
        pts += [(0, TOL)]
        self.drawing = Face(pts)

        self.setInterface("topedge", ["e%d" % (len(b) + d + 1) for d in range(len(t))])
        self.setInterface("botedge", ["e%d" % d for d in range(len(b))[::-1]])

if __name__ == "__main__":
  e = SplitEdge()
  e.setParameter("toplength", (10, 30, 60))
  e.setParameter("botlength", (80,20))
  e.setParameter("tolerance", 10)
  e.make()

  print e.interfaces["topedge"]
  print e.interfaces["botedge"]

  import utils.display
  e.drawing.transform(origin=(100,100))
  utils.display.displayTkinter(e.drawing)
