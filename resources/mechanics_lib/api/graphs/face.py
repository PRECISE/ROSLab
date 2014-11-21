from collections import OrderedDict
from api.symbolic import LinearExpr
from hyperedge import *
from transforms import *
import numpy as np

class Face():
  allNames = []

  def __init__(self, name, pts, edgeNames=None, edgeAngles=None, allEdges=None, decorations=None):
    if name:
      self.name = name
    else:
      self.name = "" # "face%03d" % len(Face.allNames)
    Face.allNames.append(self.name)

    self.pts2d = list(pts)

    self.edges = [None] * len(pts)
    self.renameEdges(edgeNames, edgeAngles, allEdges)
    if decorations:
      self.decorations = decorations
    else:
      self.decorations = []

  def renameEdges(self, edgeNames=None, edgeAngles=None, allEdges=None):
    if edgeNames:
      if edgeAngles is None:
        edgeAngles = [None] * len(edgeNames)
      for (index, name) in enumerate(edgeNames):
        self.setEdge(index, name, edgeAngles[index], allEdges)
    return self

  def setEdge(self, index, name=None, angle=None, allEdges=None):
    if name is None:
      return self
    try: 
      if self.edges[index].name == name:
        if angle is not None:
          self.edges[index].setAngle(angle)
        return self
    except:
      pass

    self.disconnect(index)

    e = HyperEdge.edge(allEdges, name, self, angle)
    self.edges[index] = e

    return self

  def replaceEdge(self, oldEdge, newEdge):
    for (i, e) in enumerate(self.edges):
      if e is oldEdge:
        self.disconnect(i)
        self.edges[i] = newEdge
        newEdge.join(self)
    return self

  def edgeLength(self, edgeIndex):
    pt1 = np.array(self.pts2d[edgeIndex-1])
    pt2 = np.array(self.pts2d[edgeIndex])

    d = pt2 - pt1
    if isinstance(d[0], LinearExpr) or isinstance(d[1], LinearExpr):
        return LinearExpr.hypot(d[0], d[1])
    else:
        return np.linalg.norm(d)

  def rotate(self, n=1):
    for i in range(n):
      self.edges.append(self.edges.pop(0))
      self.pts2d.append(self.pts2d.pop(0))

    return self
  
  def flip(self):
    newEdges = []
    newPts = []
    while self.edges:
      newEdges.append(self.edges.pop())
      newPts.append(self.pts2d.pop())
    newEdges.insert(0, newEdges.pop())
    self.edges = newEdges
    self.pts2d = newPts
    # XXX TODO: flip orientation of edges
    return self

  def transform(self, scale=1, angle=0, origin=(0,0)):
    r = np.array([[np.cos(angle), -np.sin(angle)],
                  [np.sin(angle),  np.cos(angle)]]) * scale
    o = np.transpose(np.array(origin))

    pts = np.transpose(np.dot(r, np.transpose(np.array(self.pts2d)))) + o
    self.pts2d = [tuple(x) for x in pts]
    for (i, d) in enumerate(self.decorations):
      pts = np.transpose(np.dot(r, np.transpose(np.array(d[0])))) + o
      self.decorations[i] = ([tuple(x) for x in pts], d[1])

  def disconnectFrom(self, edgename):
    for (i, e) in enumerate(self.edges):
      if edgename == e.name:
        return self.disconnect(i)
    return self

  def disconnectAll(self):
    for i in range(len(self.edges)):
      self.disconnect(i)
    return self

  def disconnect(self, index):
    e = self.edges[index]

    if e is None:
      return self

    self.edges[index] = None
    e.remove(self)
    return self

  def allNeighbors(self):
    n = []
    for es in self.neighbors():
      n.extend(es)
    return n

  def neighbors(self):
    n = []
    for e in self.edges:
      if e is None:
        n.append([])
      else:
        n.append([f.name for f in e.faces if f.name != self.name])
    return n

  def copy(self, name):
    return Face(name, self.pts2d, decorations=self.decorations)

  def matches(self, other):
    if len(self.pts2d) != len(other.pts2d):
      return False
    #XXX TODO: verify congruence
    bothpts = zip(self.pts2d, other.pts2d)
    return True

  def addDecoration(self, pts):
    self.decorations.append(pts)
    
  def preTransform(self, edge):
    index = self.edges.index(edge)
    return np.dot(RotateOntoX(self.pts2d[index-1], self.pts2d[index]), MoveToOrigin(self.pts2d[index]))
   
  def triangulate(self, edgeFrom):
    pts4d = np.transpose(np.array([np.array(list(x) + [0,1]) for x in self.pts2d]))

    d4d = []
    edges = []
    for d in ( x[0] for x in self.decorations if x[1] == "hole" ):
      d4d.append(np.transpose(np.array([np.array(list(x) + [0,1]) for x in d])))

    if edgeFrom is not None:
      r = self.preTransform(edgeFrom)
      pts4d = np.dot(r, pts4d)
      for i,d in enumerate(d4d):
        d4d[i] = np.dot(r, d)

    pts2d = pts4d[0:2]

    vertices = [x for x in np.transpose(pts2d)]
    segments = [np.array((i, (i+1) % len(vertices))) for i in range(len(vertices))]
    holes = []
    for d2d in d4d:
      for i in range(len(d2d)):
        edges.append([d2d[:,i-1], d2d[:,i], None])
      d = np.transpose(d2d[0:2])
      segments.extend( [len(vertices)+np.array(((i+1) % len(d), i)) for i in range(len(d))] )
      vertices.extend( [x for x in d] )
      holes.append( sum(d)/len(d) )

    if holes:
      A = dict(vertices=np.array(vertices), segments=np.array(segments), holes=np.array(holes))
      '''
      import triangle.plot
      import matplotlib.pyplot as plt
      triangle.plot.compare(plt, A, A)
      plt.show()
      '''
    else:
      A = dict(vertices=np.array(vertices), segments=np.array(segments))

    '''
    B = triangle.triangulate(A, opts='p')
    faces = [np.transpose(np.array(
               [list(B['vertices'][x]) + [0,1] for x in (face[0], face[1], face[2])]
             )) for face in B['triangles']]
    return faces, pts2d, pts4d, cutedges
    '''
    return A, pts2d, pts4d, edges
     
  def getAll3DFaces(self, edgeFrom, transform, allFaces, allEdges, facesTouched, fold=True):
    if self in facesTouched:
      # TODO : verify that it connects appropriately along alternate path
      print "Repeated face : " + self.name
      return

    facesTouched.append(self)

    #faces, pts2d, pts4d = self.triangulate(edgeFrom)
    #allFaces.extend([np.dot(transform, x) for x in faces])
    '''
    for f in faces:
      allFaces.extend([np.dot(transform, x) for x in inflate(f)])
    '''
    A, pts2d, pts4d, edges = self.triangulate(edgeFrom)
    allFaces.append((transform, A, self.name))
    allEdges.extend([(np.dot(transform, x[0]), np.dot(transform, x[1]), x[2]) for x in edges])
    pts4d = np.dot(transform, pts4d)

    for (i, e) in enumerate(self.edges):
      # XXX hack: don't follow small edges
      if e is None or len(e.faces) <= 1 or self.edgeLength(i) <= 0.01:
        if self.edgeLength(i) <= 0.01:
          print "skipping short edge"
        allEdges.append([pts4d[:,i-1], pts4d[:,i], None])
        continue
      da = e.faces[self]
      # XXX : Assumes both faces have opposite edge orientation
      #       Only works for non-hyper edges -- need to store edge orientation info for a +/- da
      for (f, a) in ((f, a+da) for (f, a) in e.faces.iteritems() if f not in facesTouched):
        allEdges.append([pts4d[:,i-1], pts4d[:,i], a])

    for (i, e) in enumerate(self.edges):
      # XXX hack: don't follow small edges
      if e is None or len(e.faces) <= 1 or self.edgeLength(i) <= 0.01:
        continue

      pt1 = pts2d[:,i-1]
      pt2 = pts2d[:,i]
      da = e.faces[self]

      # TODO : Only skip self and the face that you came from to verify multi-connected edges
      # XXX : Assumes both faces have opposite edge orientation
      #       Only works for non-hyper edges -- need to store edge orientation info for a +/- da
      for (f, a) in ((f, a+da) for (f, a) in e.faces.iteritems() if f not in facesTouched):
        r = np.eye(4)
        if fold:
          r = RotateX(np.deg2rad(a))
        x = RotateXTo(pt2, pt1)
        r = np.dot(x, r)
        r = np.dot(MoveOriginTo(pt1), r)
        f.getAll3DFaces(e, np.dot(transform, r), allFaces, allEdges, facesTouched, fold)

  def __eq__(self, other):
    return self.name == other.name

  def __hash__(self):
    return self.name.__hash__()

if __name__ == "__main__":
  ae = []

  f1 = Face("one"  , ((1,0), (1,1), (0,1), (0,0)), edgeNames = ("12", "13", "15", "14"), allEdges = ae)
  f2 = Face("two"  , ((1,0), (1,1), (0,1), (0,0)), edgeNames = ("12", "24", "26", "23"), allEdges = ae)
  f3 = Face("three", ((1,0), (1,1), (0,1), (0,0)), edgeNames = ("13", "23", "36", "35"), allEdges = ae)
  f4 = Face("four" , ((1,0), (1,1), (0,1), (0,0)), edgeNames = ("14", "45", "46", "24"), allEdges = ae)
  f5 = Face("five" , ((1,0), (1,1), (0,1), (0,0)), edgeNames = ("15", "35", "56", "45"), allEdges = ae)
  f6 = Face("six"  , ((1,0), (1,1), (0,1), (0,0)), edgeNames = ("26", "46", "56", "36"), allEdges = ae)

  print f1.pts2d
  f1.transform(scale=2, angle=np.deg2rad(45), origin=(1,1))
  print f1.pts2d
  '''
  f1.rotate()
  f1.flip()
  f1.rotate(2)
  f1.flip()
  f1.rotate()

  print [str(x) for x in f2.edges]
  print f2.edges
  e = f1.edges[0]
  e.remove(f1)

  print
  print f1.neighbors()
  print f2.neighbors()
  print f3.neighbors()
  print f4.neighbors()
  print f5.neighbors()
  print f6.neighbors()
   
  f1.disconnectAll()
  print
  print f1.neighbors()
  print f2.neighbors()
  print f3.neighbors()
  print f4.neighbors()
  print f5.neighbors()
  print f6.neighbors()
   
  f1.setEdge(0, "12", ae)
  print
  print f1.neighbors()
  print f2.neighbors()
  print f3.neighbors()
  print f4.neighbors()
  print f5.neighbors()
  print f6.neighbors()

  loopedges = []
  loop = Face("loop"  , ((1,0), (1,1), (0,1), (0,0)), edgeNames = ("join", "left", "join", "right"), allEdges = loopedges)
  print loopedges
  print [str(x) for x in loop.edges]
  for e in loopedges:
    print e.faces
  '''
