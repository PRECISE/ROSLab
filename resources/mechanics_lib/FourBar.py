from api.component import Component
from api.shapes import Rectangle
from api.edge import Fold

class FourBar(Component):
    def defParameters(self):
        self.newParameter("flexlengthx")
        self.newParameter("flexlengthy")
        self.newParameter("flexwidth", 5)
        self.newParameter("depth")

    def assemble(self):
        lx = self.getParameter("flexlengthx")
        ly = self.getParameter("flexlengthy")
        w = self.getParameter("flexwidth")
        t = self.getParameter("depth")

        r = Rectangle(w, lx)
        s = Rectangle(w, t)

        self.drawing.append(r, 'r0')
        self.drawing.attach('r0.e2', s, 'e0', 'r1', Fold(90))
        self.drawing.attach('r1.e2', r, 'e0', 'r2', Fold(90))

        r = Rectangle(t, lx/2.)
        self.drawing.attach('r1.e1', r, 'e0', 'r3', Fold(90))

        r = Rectangle(t, ly)
        self.drawing.attach('r3.e2', r, 'e0', 'r5', Fold(90))

    def defInterfaces(self):
        self.newInterface("topedge", "r0.e0")
        self.newInterface("botedge", "r2.e2")
        self.newInterface("output", "r5.e2")

if __name__ == "__main__":
  import utils.display
  e = FourBar()
  e.setParameter("flexlengthx", 100)
  e.setParameter("flexlengthy", 200)
  e.setParameter("flexwidth", 20)
  e.setParameter("depth", 50)
  e.make()
  e.drawing.transform(origin=(100,100))
  utils.display.displayTkinter(e.drawing)
