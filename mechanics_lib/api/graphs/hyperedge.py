import numpy as np
from numpy.linalg import norm
from math import copysign

class HyperEdge:
  
  @staticmethod
  def edge(allEdges, name, face, angle=0):
    if angle is None:
      angle = 0
    if allEdges is not None:
      for e in allEdges:
        if e.name == name:
          e.join(face, angle=angle)
          return e

    e = HyperEdge(name, face, angle)
    try:
      allEdges.append(e)
    except:
      pass

    return e

  def __init__(self, name, face=None, angle=0):
    self.name = name
    #self.pt1 = pt1
    #self.pt2 = pt2
    if face:
      self.faces = {face: angle}
    else:
      self.faces = {}

  def remove(self, face):
    if face in self.faces:
      self.faces.pop(face)
      try:
        e = face.edges.index(self)
        face.disconnect(e)
      except (ValueError, AttributeError):
        pass

  def rename(self, name):
    self.name = name

  def setAngle(self, face, angle):
    if face in self.faces:
      self.faces[face] = angle

  def join(self, face, fromface=None, angle = 0, flipped = True):
    baseangle = 0
    if fromface in self.faces:
      baseangle = self.faces[fromface]
    newangle = (abs(baseangle)+angle) % 360
    if flipped:
      newangle = copysign(newangle, -baseangle)
    else:
      newangle = copysign(newangle, baseangle)

    self.faces[face] = newangle

  '''
  def matches(self, other):
    return self.length() == other.length()
  '''

  def mergeWith(self, other, angle=0, flip=False):
    # TODO : flip orientation of edge
    if other is None: 
      return self

    for face in other.faces.keys():
      da = other.faces.pop(face)
      face.replaceEdge(other, self)
      self.faces[face] = angle + da
    return self

  '''
  def split(self, lengths, names=None):
    tol = 1e-3 # 0.1% tolerance

    edges = []
    lastpt = self.pt1
    d = self.pt2 - self.pt1
    totlen = 0
    index = 0

    for length in lengths:
      totlen += length
      try:
        name = names[index]
      except:
        name = self.name + ".s%d" % index

      if totlen >= (1-tol) * self.length():
        # less than 0.1% is probably just rounding error
        if abs(totlen - self.length()) * 1.0 / totlen > .001:
          print 'length exceeded by ' + repr(totlen - self.length())
        break
      e = HyperEdge(name, lastpt, self.pt1 + totlen/self.length() * d)
      lastpt = self.pt1 + totlen/self.length() * d
      edges.append(e)
      index += 1

    e = HyperEdge(name, lastpt, self.pt2)
    edges.append(e)
    return edges
  '''

  def __eq__(self, other):
    return self.name == other.name

  def __str__(self):
    return self.name + ": " + repr(self.faces)

  def __repr__(self):
    # return self.name + " [ # faces : %d, len : %d ]" % (len(self.faces), self.length)
    ret = "%s#%d" % (self.name, len(self.faces))
    if len(self.faces) > 1:
      return ret + repr(self.faces.values())
    else:
      return ret
      
