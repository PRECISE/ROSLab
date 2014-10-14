from api.component import Component
from api.shapes import Rectangle
from api.edge import Fold

class TJoint(Component):
    def defParameters(self):
        self.newParameter("thickness")
        self.newParameter("stemwidth")
        self.newParameter("crosswidth")

    def defInterfaces(self):
        self.newInterface("stemedge")
        self.newInterface("stemtab")
        self.newInterface("leftedge")
        self.newInterface("rightedge")

    def assemble(self):
        sw = self.getParameter("stemwidth")
        cw = self.getParameter("crosswidth")
        t = self.getParameter("thickness")

        r = Rectangle(sw, cw)
        s = Rectangle(sw, t)

        self.drawing.append(r, 'r0')
        self.drawing.attach('r0.e2', s, 'e0', 'r1', Fold(90))
        self.drawing.attach('r1.e2', r, 'e0', 'r2', Fold(90))

        self.setInterface("stemedge", "r0.e0")
        self.setInterface("stemtab", "r2.e2")
        self.setInterface("leftedge", ["r%d.e1" % x for x in range(3)])
        self.setInterface("rightedge", ["r%d.e3" % x for x in range(3)])

if __name__ == "__main__":
  import utils.display
  e = TJoint()
  e.setParameter("stemwidth", 50)
  e.setParameter("crosswidth", 30)
  e.setParameter("thickness", 10)
  e.make()
  e.drawing.transform(origin=(100,100))
  utils.display.displayTkinter(e.drawing)
