import numpy as np
from api.symbolic import LinearExpr


def MirrorX():
  return np.diag([-1, 1, 1, 1])

def MirrorY():
  return np.diag([1, -1, 1, 1])

def Scale(scale):
  return np.diag([scale, scale, scale, 1])

def RotateX(angle):
  r = np.array([[1, 0, 0, 0],
                [0, np.cos(angle), -np.sin(angle), 0],
                [0, np.sin(angle),  np.cos(angle), 0],
                [0, 0, 0, 1]]) 
  return r

def RotateZ(angle):
  r = np.array([[np.cos(angle), -np.sin(angle), 0, 0],
                [np.sin(angle),  np.cos(angle), 0, 0],
                [0, 0, 1, 0],
                [0, 0, 0, 1]]) 
  return r

def symbolic_atan2(y, x):
  if isinstance(y, LinearExpr) or isinstance(x, LinearExpr):
    return LinearExpr.atan2(y, x)
  return np.arctan2(y, x)

def MoveToOrigin(pt):
  return Translate([-pt[0], -pt[1], 0])

def RotateOntoX(pt, pt2=(0,0)):
  return RotateZ(-symbolic_atan2(pt[1] - pt2[1], pt[0] - pt2[0]))

def MoveOriginTo(pt):
  return Translate([pt[0], pt[1], 0])

def RotateXTo(pt, pt2=(0,0)):
  return RotateZ(symbolic_atan2(pt[1] - pt2[1], pt[0] - pt2[0]))

def Translate(origin):
  r = np.array([[1, 0, 0, origin[0]],
                [0, 1, 0, origin[1]],
                [0, 0, 1, origin[2]],
                [0, 0, 0, 1]]) 
  return r
