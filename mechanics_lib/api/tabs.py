from drawing import Drawing
from shapes import *
from edge import *
from math import pi, sin, tan, atan

class Tab(Face):
  def __init__(self, w, t, noflap=False):
    if (noflap or t > w/2):
      Face.__init__(self, 
        ((w,0), (w,t), (0,t)))
    else:
      Face.__init__(self, 
        ((w,0), (w+t,0), (w,t), (0,t), (-t,0)))
      self.edges['f0'] = Edge("f0", (0,0), (0,t), Flex())
      self.edges['f1'] = Edge("f1", (w,0), (w,t), Flex())

    #self.edges['e0'].edgetype = Flat()
    self.edges.pop('e0')
    self.transform(origin=(-w/2.0,-t/2.))
    self.graph = None

class ThinTab(Face):
  def __init__(self, w, t):
    if (t > w/2):
      Face.__init__(self, 
        ((w,0), (w,t), (0,t)))
    else:
      Face.__init__(self, 
        ((w,0), (w+t,0), (w,t), (0,t), (-t,0)))
      self.edges['f0'] = Edge("f0", (0,0), (0,t), Flex())
      self.edges['f1'] = Edge("f1", (w,0), (w,t), Flex())

    self.edges['e0'].edgetype = NoEdge()
    self.transform(origin=(-w/2.0,-t/2.))
    self.graph = None

class Slot(Rectangle):
  def __init__(self, w, t, noflap = False):
    Rectangle.__init__(self, w+0.5, 0.5)
    self.transform(origin=(-w/2. - 0.25, -t/2. - 0.25));
    self.graph = None

class BeamTabSlotHelper(Rectangle):
  def __init__(self, rl, thick, widget, name, edgetype, opts=None):
    Rectangle.__init__(self, rl, thick, edgetype)
    if edgetype.edgetype == EdgeType.NOEDGE:
      self.flip()
      self.graph = None

    n = 0
    tw = thick * 3
    while (LinearExpr.evalDefault(tw) > LinearExpr.evalDefault(thick * 2)):
      n += 1
      d = rl*1.0 / (n*5+1)
      tw = 2 * d

    noflap = False
    try:
      noflap = opts["noflap"]
    except: pass
    t = widget(w=tw, t=thick/2., noflap = noflap)
    t.transform(angle=pi, origin=(-2 * d, thick/2.))
    try:
      if opts["mirror"]:
        t.mirrorY()
        t.transform(origin=(0, thick))
    except: pass

    for i in range(n):
      t.transform(origin=(d * 5, 0))
      self.append(t, name + repr(i))
      try:
        if opts["alternating"]:
          t.mirrorY()
          t.transform(origin=(0, thick))
      except: pass

class ThinTabs(BeamTabSlotHelper):
  def __init__(self, rl, thick, edgetype=None):
    if edgetype is None: edgetype = Cut()
    BeamTabSlotHelper.__init__(self, rl, thick, Tab, 'tab', edgetype, opts = None)

class BeamTabs(BeamTabSlotHelper):
  def __init__(self, rl, thick, edgetype=None, opts=None):
    if edgetype is None: edgetype = Cut()
    BeamTabSlotHelper.__init__(self, rl, thick, Tab, 'tab', edgetype, opts = opts)

class BeamSlots(BeamTabSlotHelper):
  def __init__(self, rl, thick, edgetype=None, opts=None):
    if edgetype is None: edgetype = NoEdge()
    BeamTabSlotHelper.__init__(self, rl, thick, Slot, 'slot', edgetype, opts = opts)

class BeamTabSlot:
  def __init__(self, rl, thick, edgetypes=None, opts=None):
    if edgetypes is None: edgetypes = (Cut(), NoEdge())
    self.tabs = BeamTabs(rl, thick, edgetypes[0], opts = opts)
    self.slots = BeamSlots(rl, thick, edgetypes[1], opts = opts)

'''
class Taper(Drawing):
  def __init__(self, thickness, angle, shape = 3, cover = False, deg = False):
     Drawing.__init__(self)
     self.thickness = thickness
     self.angle = angle
     
     if deg:
       phi = (shape - 2)*180 / shape
       phi = np.deg2rad(phi)
       angle = np.deg2rad(angle)
     else:
       phi = (shape - 2)*pi / shape
     theta_cut = atan(tan(angle)*sin(phi))
     delta = thickness * tan(theta_cut)
     
     a = Rectangle(thickness, delta)
     rtri = Triangle(delta, thickness)
     ltri = Triangle(delta, thickness, backwards = True)

    # XXX Flex -> fold angle?
     a.attach('e1', rtri, 'e0', 'r', Flex())
     a.attach('e3', ltri, 'e1', 'l', Flex())
     self.append(a)

     if cover:
       pass

class Hook(Rectangle):
    def __init__(self, thickness, length):
        Rectangle.__init__(self, thickness, length)
        self.thickness = thickness
        self.length = length
        self.attach('e2', Taper(thickness, 45, deg = True), 'e2', 'h', NoEdge())
    def hookhole(self):
        helper = Rectangle(self.thickness, self.length, NoEdge())
        helper.attach('e2', Rectangle(self.thickness, .8), 'e2', 'slot', Cut())
        helper.flip()
        return helper

class HookTab(Rectangle):
  def __init__(self, thickness, length):
    Rectangle.__init__(self, thickness, length)
    self.thickness = thickness
    self.length = length
    # XXX distance = 'Center' went away!
    # XXX Flex -> fold angle?
    self.attach('e2',Taper(thickness/4, 50, deg = True), 'e2', 'fl', Flex()) #, distance = 'Center')

  def hole(self):
    helper = Rectangle(self.thickness, self.length, NoEdge())
    # XXX distance = 'Center' went away!
    helper.attach('e2', Rectangle(self.thickness/4.+.4, .8), 'e0','h', Cut()) #, distance = 'Center')
    helper.flip()
    return helper
'''
