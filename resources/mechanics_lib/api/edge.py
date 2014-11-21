import numpy as np
from api.symbolic import LinearExpr

class EdgeType:
  """
  Values for the different modes of Edge instances
  """
  ( REG,
    CUT,
    FOLD,
    FLEX,
    FLAT,
    NOEDGE
  ) = range(6)

  def __init__(self, edgetype, angle=0):
    self.edgetype = edgetype
    self.angle = angle

  def __repr__(self):
    ret = ( "REG",
            "CUT",
            "FOLD",
            "FLEX",
            "FLAT",
            "NOEDGE",
          )[self.edgetype]
    if self.angle:
      ret += " (%d)" % self.angle
    return ret
    
  def invert(self):
    self.angle = -self.angle

  def drawArgs(self, name, mode):
    if self.edgetype in (EdgeType.FLAT, EdgeType.NOEDGE):
      return

    svgArgs = [{"stroke": c} for c in ["#00ff00", "#0000ff", "#ff0000", "#ffff00"]]
    dxfArgs = [{"color": c} for c in [3, 5, 1, 1]]
    layerArgs = [{"layer": c} for c in ["Registration", "Cut", "xxx", "Flex"]]

    if mode == "dxf":
      return dxfArgs[self.edgetype]
    elif mode == "autofold":
      ret = dxfArgs[self.edgetype]
      ret.update(layerArgs[self.edgetype])
      if self.edgetype == EdgeType.FOLD:
        ret["layer"] = repr(self.angle)
      return ret

    kwargs = {"id" : name}
    kwargs.update(svgArgs[self.edgetype])

    if self.edgetype in (EdgeType.FOLD, EdgeType.FLEX) :
      if mode == "print": 
        if self.angle < 0:
          kwargs["stroke"] = "#00ff00"
        else:
          kwargs["stroke"] = "#ff0000"
      if mode == "foldanimate":
        kwargs["stroke"] ='#%02x0000' % (256*self.angle / 180)
      else:
        kwargs["kerning"] = self.angle

    return kwargs

class Fold(EdgeType):
  def __init__(self, angle):
    EdgeType.__init__(self, EdgeType.FOLD, angle)
class Flat(EdgeType):
  def __init__(self):
    EdgeType.__init__(self, EdgeType.FLAT)
class Reg(EdgeType):
  def __init__(self):
    EdgeType.__init__(self, EdgeType.REG)
class Cut(EdgeType):
  def __init__(self):
    EdgeType.__init__(self, EdgeType.CUT)
class Flex(EdgeType):
  def __init__(self, angle=0):
    EdgeType.__init__(self, EdgeType.FLEX, angle)
class NoEdge(EdgeType):
  def __init__(self):
    EdgeType.__init__(self, EdgeType.NOEDGE)

def diag(dx, dy):
  """
  Returns the diagonal distance between two points.

  @type dx: real number
  @param dx: the change in x distance between the two points
  @type dy: real number
  @param dy: the change in y distance between the two points
  @return: the diagonal distance between two points as a numpy.float64 data type
  """
  return np.sqrt(dx*dx + dy*dy)

class Edge:
  """
  A class representing an Edge.
  """
  
  def __init__(self, name, pt1, pt2, edgetype):
    """
    Initializes an Edge object with pt1 and pt2 in the form [[x1,y1],[x2,y2]]

    The Edge can have 4 different types: CUT, FLAT, BEND, FOLD

    @type pt1: tuple
    @param pt1: location of point one in the form (x1,y1).
    @type pt2: tuple
    @param pt2: location of point two in the form (x2,y2).
    @type mode: string
    @param mode: 4 different types of Edges: CUT, FLAT, BEND, FOLD
    @return: an Edge object
    """

    self.name = name
    self.x1 = pt1[0]
    self.y1 = pt1[1]
    self.x2 = pt2[0]
    self.y2 = pt2[1]
    if edgetype is None:
      edgetype = Cut()
    self.edgetype = edgetype

  def isSymbolic(self):
    return isinstance(self.x1, LinearExpr) or isinstance(self.y1, LinearExpr) or isinstance(self.x2, LinearExpr) or isinstance(self.y2, LinearExpr)
  def coords(self):
    """
    @return: a list of the coordinates of the Edge instance endpoints, in the form [[x1,y1],[x2,y2]] rounded to nearest 10^-6 
    """

    if self.isSymbolic():
        return [[LinearExpr.round(self.x1, 6),LinearExpr.round(self.y1,6)],
               [LinearExpr.round(self.x2,6),LinearExpr.round(self.y2,6)]]

    coords  = [[round(self.x1, 6),round(self.y1,6)],[round(self.x2,6),round(self.y2,6)]]
    for i in coords:
      if i[0] == -0.0:
        i[0] = 0.0
      if i[1] == -0.0:
        i[1] = 0.0
    return coords

  def length(self):
    """
    Uses the diag() function
    
    @return: the length of the edge as a numpy.float64 data type
    """
    dx = self.x2 - self.x1
    dy = self.y2 - self.y1
    if self.isSymbolic():
        return LinearExpr.hypot(dx, dy)
    return diag(dx, dy)

  def angle(self, deg=False):
    """
    @type deg: boolean
    @param deg: sets angle return type to be deg or rad (rad by default)
    @return: angle of the Edge instance with respect to the positive x axis as a numpy.float64
    """
    dx = self.x2 - self.x1
    dy = self.y2 - self.y1
    if self.isSymbolic():
        ang = LinearExpr.atan2(dy, dx)
    else:
        ang = np.arctan2(dy, dx)
    if deg: 
      return np.rad2deg(ang)
    else:
      return ang

  def elongate(self, lengths, otherway = False):
    """
    Returns a list of Edge instances that extend out from the endpoint of another Edge instance.
    Mode of all smaller edges is the same as the original Edge instance.
    @type lengths: list
    @param lengths: list of lengths to split the Edge instance into
    @param otherway: boolean specifying where to start from (pt2 if otherway == False, pt1 if otherway == True)
    @return: a list of Edge instances that extend out from the endpoint of another Edge instance
    """

    edges = []
    if otherway:
      lastpt = (self.x1, self.y1)
      for length in lengths:
        e = Edge((0, 0),(-length,0), self.edgetype)
        e.transform(angle=self.angle(), origin=lastpt)
        lastpt = (e.x2, e.y2)
      edges.append(e)
    else:
      lastpt = (self.x2, self.y2)
      for length in lengths:
        e = Edge((0,0), (length, 0), self.edgetype)
        e.transform(angle=self.angle(), origin=lastpt)
        lastpt = (e.x2, e.y2)
      edges.append(e)
      
    return edges


  def transform(self, scale=1, angle=0, origin=(0,0)):
    """
    Scales, rotates, and translates an Edge instance.

    @type scale: float
    @param scale: scaling factor
    @type angle: float
    @param angle: angle to rotate in radians
    @type origin: tuple
    @param origin: origin

    """

    r = np.array([[np.cos(angle), -np.sin(angle)],
                  [np.sin(angle),  np.cos(angle)]]) * scale

    o = np.array(origin)

    pt1 = np.dot(r, np.array((self.x1, self.y1))) + o
    pt2 = np.dot(r, np.array((self.x2, self.y2))) + o

    self.x1 = pt1[0]
    self.y1 = pt1[1]
    self.x2 = pt2[0]
    self.y2 = pt2[1]

  def invert(self):
    """
    Swaps mountain and valley folds
    """
    self.edgetype.invert()

  def mirrorX(self):
    """
    Changes the coordinates of an Edge instance so that it is symmetric about the Y axis.
    """
    self.x1 = -self.x1
    self.x2 = -self.x2
    self.flip()

  def mirrorY(self):
    """
    Changes the coordinates of an Edge instance so that it is symmetric about the X axis.
    """
    self.y1 = -self.y1
    self.y2 = -self.y2
    self.flip()

  def flip(self):
    """
    Flips the directionality of an Edge instance around
    """
    x = self.x2
    y = self.y2
    self.x2 = self.x1
    self.y2 = self.y1
    self.x1 = x
    self.y1 = y

  def copy(self):
    return Edge(self.name, (self.x1, self.y1), (self.x2, self.y2), self.edgetype)
    
  def midpt(self):
    """
    @return: a tuple of the edge midpoint
    """
    pt1 = self.coords()[0]
    pt2 = self.coords()[1]
    midpt = ((pt2[0]+pt1[0])/2, (pt2[1]+pt1[1])/2)
    return midpt

  def toDrawing(self, drawing, label="", mode=None, engine=None):
    """
    Draws an Edge instance to a CAD file.

    @type drawing: 
    @param drawing: 
    @type label: tuple
    @param label: location of point two in the form (x2,y2).
    @type mode: 
    @param mode: 
    """

    if engine is None:
      engine = drawing

    kwargs = self.edgetype.drawArgs(self.name, mode)
    if kwargs:

      dpi = None

      if mode in ( 'Corel', 'print'):
        dpi = 96 # scale from mm to 96dpi for CorelDraw
      elif mode == 'Inkscape':
        dpi = 90 # scale from mm to 90dpi for Inkscape
      elif mode == 'autofold':
        if str(self.edgetype.angle) not in drawing.layers:
          drawing.add_layer(str(self.edgetype.angle))

      if dpi: self.transform(scale=(dpi/25.4)) 
      drawing.add(engine.line((self.x1, self.y1), (self.x2, self.y2), **kwargs))
      if dpi: self.transform(scale=(25.4/dpi)) # scale back to mm

    if label:
      r = [int(self.angle(deg=True))]*len(label)
      t = engine.text(label, insert=((self.x1+self.x2)/2, (self.y1+self.y2)/2))# , rotate=r)
      # t.rotate=r
      drawing.add(t)

if __name__ == "__main__":
  import svgwrite
  e = Edge("e1", (0,0), (1,1), Flex())
  svg = svgwrite.Drawing("testedge.svg")
  e.toDrawing(svg, mode="Inkscape")
  svg.save()

  from dxfwrite import DXFEngine as dxf
  svg = dxf.drawing("testedge.dxf")
  e.toDrawing(svg, mode="dxf", engine=dxf)
  svg.save()
