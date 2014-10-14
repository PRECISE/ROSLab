from api.component import Component
from connector import Tab
from api.shapes import Face
from api.edge import *

class Tetrahedron(Component):
    def defParameters(self):
        self.newParameter("perimeter")
        self.newParameter("start", 0)
        self.newParameter("end", 1)
        self.newParameter("min", 1)
    
    def defInterfaces(self):
        self.newInterface("endedge")
        self.newInterface("startedge")
        
    def assemble(self):
       #an equilateral triangular face requires width = unitheight* rt(3)
       fullwidth = self.getParameter("perimeter")/4.
       fullheight = fullwidth*(3**.5)/2.
       h = fullheight * abs(self.getParameter("end") - self.getParameter("start"))

       def splits(width, frac):
         return [width * x for x in (frac/2., 1-frac, 1+frac, 1-frac, 1+frac/2.)]

       se = splits(fullwidth, self.getParameter("end"))
       ss = splits(fullwidth, self.getParameter("start"))

       m = min(self.getParameter("start"),
               self.getParameter("end"),
               self.getParameter("min")) * fullwidth / 2.

       se[0] -= m
       ss[0] -= m
       se[-1] += m
       ss[-1] += m
       xb, xt, index = 0, 0, 0

       for (xstart, xend) in zip(ss, se):
         r = Face(((xb, 0), (xb+xstart, 0), (xt+xend, h), (xt, h)), origin=False)
         if index:
           self.drawing.attach("r%d.e2" % (index-1), r, "e0", "r%d" % index, Fold(109.5))
         else:
           self.drawing.append(r, "r%d" % index)
         xb += xstart
         xt += xend
         index += 1

       self.addConnectors((Tab(), "t1"), "r0.e0", "r4.e2", min(10, fullwidth / 2.), (Flat(), Cut()))

       self.setInterface("startedge", ["r%d.e1" % x for x in range(5)])
       self.setInterface("endedge", ["r%d.e3" % x for x in range(5)])

if __name__ == "__main__":
    h = Tetrahedron()
    h.setParameter("perimeter", 400)
    h.setParameter("start", 1)
    h.setParameter("end", 0)
    h.make()
    h.drawing.transform(relative = (0,0))

    h.drawing.graph.toSTL("output/tetra.stl")
    import utils.display
    utils.display.displayTkinter(h.drawing)
