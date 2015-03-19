from face import Face
from hyperedge import HyperEdge
import numpy as np

def inflate(face, thickness=.1, edges=False):
  dt = np.array([[0],[0],[thickness/2.],[0]])
  nf = face-dt
  pf = face+dt

  faces = []

  if edges:
    faces.append(np.transpose(np.array((pf[:,0], nf[:,0], pf[:,1]))))
    faces.append(np.transpose(np.array((nf[:,0], nf[:,1], pf[:,1]))))
  else:
    faces.append(pf)          # top face 
    faces.append(nf[:,::-1])  # bottom face

  return faces

def STLWrite(faces, filename, thickness=0):
  import triangle

  shape = None
  shells = []
  triangles = []
  for f in faces:
    r = f[0]
    A = f[1]

    facets = []
    B = triangle.triangulate(A, opts='p')
    if not 'triangles' in B:
      print "No triangles in " + B
      continue

    if thickness:
      for t in [np.transpose(np.array([list(B['vertices'][x]) + [0,1] for x in (face[0], face[1], face[2])])) for face in B['triangles']]:
        facets.extend([np.dot(r, x) for x in inflate(t, thickness=thickness)])
      for t in [np.transpose(np.array([list(A['vertices'][x]) + [0,1] for x in (edge[0], edge[1])])) for edge in A['segments']]:
        facets.extend([np.dot(r, x) for x in inflate(t, thickness=thickness, edges=True)])
    else:
      for t in [np.transpose(np.array([list(B['vertices'][x]) + [0,1] for x in (face[0], face[1], face[2])])) for face in B['triangles']]:
        facets.append(np.dot(r, t))

    triangles.extend(facets)

    if thickness:
      FREECADPATH = '/usr/lib64/freecad/lib'
      import sys
      sys.path.append(FREECADPATH)
      import FreeCAD
      import Part
      meshes = []
      for f in (np.transpose(t[0:3,:]) for t in facets):
        try:
          meshes.append(Part.Face(Part.Wire([Part.makeLine(tuple(f[x]), tuple(f[x-1])) for x in range(3)])))
        except RuntimeError:
          print "Skipping face: " + repr(f)
      shell = Part.makeShell(meshes)
      shells.append(shell)
      if shape is None:
        shape = shell
      else:
        shape = shape.fuse(shell)

  if shape:
    with open("freecad" + filename, 'wb') as fp:
      shape.exportStl("freecad" + filename)

  from stlwriter import Binary_STL_Writer
  faces = triangles

  with open(filename, 'wb') as fp:
    writer = Binary_STL_Writer(fp)
    writer.add_faces(faces)
    writer.close()

def DXFWrite(edges, filename):
  from dxfwrite import DXFEngine as dxf
  dwg = dxf.drawing(filename)
  for e in edges:
    if e[2] is None:
      kwargs = {"layer": "Cut"}
    else:
      kwargs = {"layer": repr(e[2])}
    dwg.add(dxf.line((e[0][0], e[0][1]), (e[1][0], e[1][1]), **kwargs))
  dwg.save()

def prefix(s1, s2):
  if s1 and s2:
    return s1 + "." + s2
  return s1 + s2

class Graph:
  def __init__(self):
    self.faces = []
    self.edges = []

  def addFace(self, f, faceEdges=None, faceAngles=None):
    if f in self.faces:
      raise ValueError("Face already in graph")
    self.faces.append(f)

    if faceEdges is not None:
      f.renameEdges(faceEdges, faceAngles, self.edges)

    self.rebuildEdges()
    return self

  def delFace(self, facename):
    for (i, f) in enumerate(self.faces):
      if f.name == facename:
        f.disconnectAll()
        self.faces.pop(i)
        self.rebuildEdges()
        return self

    return self

  def getFace(self, name):
    for f in self.faces:
      if f.name == name:
        return f
    return None

  def getEdge(self, name):
    for e in self.edges:
      if e.name == name:
        return e
    return None

  def renameEdge(self, fromname, toname):
    e = self.getEdge(fromname)
    if e:
      e.rename(toname)

  def rebuildEdges(self):
    self.edges = []
    for f in self.faces:
      for e in f.edges:
        if e not in self.edges:
          self.edges.append(e)

  def invertEdges(self):
    # swap mountain and valley folds
    for e in self.edges:
      for f in e.faces:
        e.faces[f] = -e.faces[f]

  def mergeEdge(self, edge1, edge2, useOrigEdge=False, angle=0):
    e1 = self.getEdge(edge1)
    e2 = self.getEdge(edge2)
    if useOrigEdge:
      e1.mergeWith(e2, angle=angle, flip=True)
    else:
      e2.mergeWith(e1, angle=angle, flip=True)
    return self

  def attach(self, edge1, (g2, prefix2, edge2), merge=True, useOrigEdge=False, angle=0):
    #TODO : carry over face angles
    for f in g2.faces:
      self.addFace(f.copy(prefix(prefix2, f.name)), faceEdges = [prefix(prefix2, e.name) for e in f.edges], faceAngles = [e.faces[f] for e in f.edges])

    if merge:
      self.mergeEdge(edge1, prefix(prefix2, edge2), useOrigEdge=useOrigEdge, angle=angle)

    return self

  def flip(self):
    return
    for f in self.faces:
      f.flip()

  def transform(self, scale=1, angle=0, origin=(0,0)):
    pass

  def dotransform(self, scale=1, angle=0, origin=(0,0)):
    for f in self.faces:
      f.transform(scale, angle, origin)
      #r = np.dot(RotateZ(angle), Scale(scale))
      #r = np.dot(Translate(list(origin) + [0,]), r)
      #f.transform(r)

  def mirrorY(self):
    return
    for f in self.faces:
      f.transform( mirrorY())

  def mirrorX(self):
    return
    for f in self.faces:
      f.transform( mirrorX())

  def printGraph(self):
    print 
    for f in self.faces:
      print f.name + repr(f.edges)

  def graphObj(self):
    g = {}
    for f in self.faces:
      g[f.name] = dict([(e and e.name or "", e) for e in f.edges])
    return g

  def showGraph(self):
    import objgraph
    objgraph.show_refs(self.graphObj(), max_depth = 2, filter = lambda x : isinstance(x, (dict, HyperEdge)))

  def toSTL(self, filename):
    stlFaces = []
    stlEdges = []
    transform = np.eye(4)
    facesTouched = []
    self.faces[0].getAll3DFaces(None, transform, stlFaces, stlEdges, facesTouched)
    STLWrite(stlFaces, filename)

  def toDXF(self, filename):
    dxfFaces = []
    dxfEdges = []
    transform = np.eye(4)
    facesTouched = []
    self.faces[0].getAll3DFaces(None, transform, dxfFaces, dxfEdges, facesTouched, fold=False)
    DXFWrite(dxfEdges, filename)

  @staticmethod
  def joinAlongEdge((g1, prefix1, edge1), (g2, prefix2, edge2), merge=True, useOrigEdge=False, angle=0):
    # TODO(mehtank): make sure that edges are congruent

    g = Graph()
    for f in g1.faces:
      g.addFace(f.copy(prefix(prefix1, f.name)), faceEdges = [prefix(prefix1, e.name) for e in f.edges])

    return g.attach(edge1, (g2, prefix2, edge2), merge=merge, useOrigEdge=useOrigEdge, angle=angle)

  '''
  @staticmethod
  def joinAlongFace((g1, prefix1, face1), (g2, prefix2, face2), toKeep=1):
    # TODO(mehtank): make sure that faces are congruent
    g = Graph()
    for f in g1.faces:
      g.addFace(f.copy(prefix1 + "." + f.name), faceEdges = [prefix1 + "." + e.name for e in f.edges])
    for f in g2.faces:
      g.addFace(f.copy(prefix2 + "." + f.name), faceEdges = [prefix2 + "." + e.name for e in f.edges])
    f1 = g.getFace(prefix1 + "." + face1)
    f2 = g.getFace(prefix2 + "." + face2)
    for (e1, e2) in zip(f1.edges, f2.edges):
      e1.mergeWith(e2)
    if toKeep < 2:
      g.delFace(f2.name)
    if toKeep < 1:
      g.delFace(f1.name)
    return g
  '''

if __name__ == "__main__":
  import shapes

  t = shapes.RegularNGon("triangle", 3, 1)
  s = shapes.Square("square", 1)

  tetra = Graph()
  tetra.addFace( t.copy("one"), ("12", "13", "14"))
  tetra.addFace( t.copy("two"), ("12", "24", "23"))
  tetra.addFace( t.copy("three"), ("13", "23", "34"))
  tetra.addFace( t.copy("four"), ("14", "34", "24"))

  tetra.showGraph()
  print tetra.faces
  print tetra.edges

  '''
  pyramid = Graph()
  pyramid.addFace( s.copy("base"), ("b1", "b2", "b3", "b4"))
  pyramid.addFace( t.copy("one"), ("b1", "14", "12"))
  pyramid.addFace( t.copy("two"), ("b2", "12", "23"))
  pyramid.addFace( t.copy("three"), ("b3", "23", "34"))
  pyramid.addFace( t.copy("four"), ("b4", "34", "14"))
  #pyramid.showGraph()

  cube = Graph()
  cube.addFace( s.copy("one"), ("12", "13", "14", "15"))
  cube.addFace( s.copy("two"), ("12", "24", "26", "23"))
  cube.addFace( s.copy("three"), ("13", "23", "36", "35"))
  cube.addFace( s.copy("four"), ("14", "45", "46", "24"))
  cube.addFace( s.copy("five"), ("15", "35", "56", "45"))
  cube.addFace( s.copy("six"), ("26", "46", "56", "36"))
  #cube.showGraph()

  join = Graph.joinAlongEdge((tetra, "t", "12"), (cube, "c", "12"))
  #join.showGraph()

  join = Graph.joinAlongFace((pyramid, "p2", "base"), (cube, "c", "one"), 2)
  join.showGraph()

  join = Graph.joinAlongFace((pyramid, "p1", "base"), (cube, "c", "one"), 1)
  join.showGraph()

  join = Graph.joinAlongFace((pyramid, "p0", "base"), (cube, "c", "one"), 0)
  join.addFace( s.copy("diag"), ("c.23", None, "c.45", None))
  join.showGraph()
  '''

  '''
  print cube.faces
  print cube.edges

  cube.delFace("one")
  cube.delFace("two")
  
  print
  print cube.faces
  print cube.edges
  
  cube.trimEdges()

  print
  print cube.faces
  print cube.edges
  '''
