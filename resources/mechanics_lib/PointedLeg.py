from api.component import Component
from api.shapes import Face, Rectangle
from connector import Tab
from api.edge import Fold
from math import sqrt

class PointedLeg(Component):
    def defParameters(self):
        self.newParameter("beamwidth", 7)
        self.newParameter("length")

    def defInterfaces(self):
        self.newInterface("front")
        self.newInterface("right")
        self.newInterface("slots")
        self.newInterface("diag")

    def assemble(self):
        l = self.getParameter("length")
        w = self.getParameter("beamwidth")
        w2 = w * sqrt(2)

        self.drawing.append(Rectangle(w2, l), "r0")
        self.drawing.attach("r0.e1", Face(((w, -w), (w, l), (0, l))), "e3", "r1", Fold(135))
        self.drawing.attach("r1.e1", Face(((w, w), (w, l+w), (0, l+w))), "e3", "r2", Fold(90))

        self.addConnectors((Tab(), "t1"), "r2.e1", "r0.e3", min(10, w2), angle=135)

        self.setInterface("front", "r1.e2")
        self.setInterface("right", "r2.e2")
        self.setInterface("slots", "r0.e3")
        self.setInterface("diag", "r0.e2")

if __name__ == "__main__":
  import utils.display
  e = PointedLeg()
  e.setParameter("length", 50)
  e.setParameter("beamwidth", 10)
  e.make()
  e.drawing.transform(origin=(0,0), relative=(0,0))
  e.drawing.graph.toSTL("output/leg.stl")
  e.drawing.graph.toDXF("output/leg.dxf")
  e.drawing.toSVG("output/leg.svg")
  e.drawing.toDXF("output/autofold/leg.dxf", mode="autofold")
  utils.display.displayTkinter(e.drawing)
