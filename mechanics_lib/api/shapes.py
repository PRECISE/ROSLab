from drawing import Drawing
from edge import *
from math import pi, sin, cos
from graphs.face import Face as GraphFace

class Face(Drawing):
  def __init__(self, pts, edgetype = None, origin = True):
    Drawing.__init__(self)
    if origin:
      pts = list(pts) + [(0,0)]
    else:
      pts = list(pts)

    lastpt = pts[-1]
    edgenum = 0
    edgenames = []
    for pt in pts:
      name = 'e%d' % edgenum
      self.edges[name] = Edge(name, lastpt, pt, edgetype)
      edgenames.append(name)
      lastpt = pt
      edgenum += 1

    self.graph.addFace(GraphFace("", pts), faceEdges=edgenames)

class Rectangle(Face):
  def __init__(self, l, w, edgetype=None):
    Face.__init__(self, ((l, 0), (l, w), (0, w)), edgetype, origin=True)

class RightTriangle(Face):
  def __init__(self, b, h, edgetype=None, backwards = False):
    if backwards:
      Face.__init__(self, ((b,h),(0,h)), origin=True, edgetype=edgetype)
    else:
      Face.__init__(self, ((b,0),(0,h)), origin=True, edgetype=edgetype)

class Circle(Face):
  def __init__(self, r, n=20, edgetype=None):
    Drawing.__init__(self)
    pts = []
    dt = 2*pi/n

    for i in range(n):
      pt = (r * cos(i * dt), r * sin(i * dt))
      pts.append(pt)

    Face.__init__(self, pts, edgetype=edgetype, origin=False)

if __name__ == "__main__":

  r = Rectangle(15, 10)
  print r.graph.edges
  print r.graph.faces[0].pts2d
  r.flip()
  #r.graph.showGraph()
  c = Circle(3.5, n=7)
  #c.graph.showGraph()
  c.transform(origin=(5,5))
  r.append(c, 'c')
  # r.transform(origin=(100,100), angle=0.92729521800161219, scale=10)
  r.transform(scale=10)

  r.toSVG('test.svg', labels=True)
  r.toDXF('test.dxf', labels=True)
