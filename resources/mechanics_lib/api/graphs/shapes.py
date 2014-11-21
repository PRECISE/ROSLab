from face import Face
from math import sin, cos, pi

class RegularNGon(Face):
  def __init__(self, name, n, length, edgeNames=None, allEdges=None):
    pts = []
    lastpt = (0, 0)
    dt = (2 * pi / n)
    for i in range(n):
      lastpt = (lastpt[0] + cos(i * dt), lastpt[1] + sin(i * dt))
      pts.append(lastpt)
      
    Face.__init__(self, name, pts, edgeNames=edgeNames, allEdges=allEdges)

class Square(RegularNGon):
  def __init__(self, name, length, edgeNames=None, allEdges=None):
    RegularNGon.__init__(self, name, 4, length, edgeNames=edgeNames, allEdges=allEdges)

class Rectangle(Face):
  def __init__(self, name, l, w, edgeNames=None, allEdges=None):
    Face.__init__(self, name, ((l, 0), (l, w), (0, w), (0,0)), edgeNames=edgeNames, allEdges=allEdges)

class RightTriangle(Face):
  def __init__(self, name, l, w, edgeNames=None, allEdges=None):
    Face.__init__(self, name, ((l, 0), (0, w), (0,0)), edgeNames=edgeNames, allEdges=allEdges)

if __name__ == "__main__":
  r = Rectangle("r1", 15, 10)
  p = RegularNGon("pent", 5, 1)
  print p.pts
  print [e.name for e in p.edges]

  s = Square("square", 1)

  ae = []
  f1 = s.copy("one").setEdges(("12", "13", "15", "14"), allEdges = ae)
  f2 = s.copy("two").setEdges(("12", "24", "26", "23"), allEdges = ae)
  f3 = s.copy("three").setEdges(("13", "23", "36", "35"), allEdges = ae)
  f4 = s.copy("four").setEdges(("14", "45", "46", "24"), allEdges = ae)
  f5 = s.copy("five").setEdges(("15", "35", "56", "45"), allEdges = ae)
  f6 = s.copy("six").setEdges(("26", "46", "56", "36"), allEdges = ae)
  print f1.neighbors()
  print f2.neighbors()
  print f3.neighbors()
  print f4.neighbors()
  print f5.neighbors()
  print f6.neighbors()

