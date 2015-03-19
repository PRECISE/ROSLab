try:
  from api.drawing import Drawing
  from api.shapes import Face
  from api.edge import Fold
except:
  from ..api.drawing import Drawing
  from ..api.shapes import Face
  from ..api.edge import Fold

import numpy as np

class Beam(Drawing):
  """
  A class representing a Beam.
  """
  def __init__(self, length, diameter, langle=90, rangle=90, shape=3, phase=0):
    """
    Initializes a Beam object 

    @type length: 
    @param length: 
    @type diameter: 
    @param diameter: 
    @type langle: 
    @param rangle: 
    @type shape:
    @param shape:
    @return: a Beam object
    """

    Drawing.__init__(self)

    radius = diameter/2.
    dtheta = np.deg2rad(360. / shape)
    thetas = [ dtheta / 2. + dtheta * i for i in range(phase, shape+phase) ]

    thickness = 2 * radius * np.sin(dtheta / 2.)

    dl = [ radius * (1 - np.cos(t)) / np.tan(np.deg2rad(langle)) for t in thetas ]
    dl = [ l - dl[-phase % shape] for l in dl ]
    dr = [ radius * (1 - np.cos(t)) / np.tan(np.deg2rad(rangle)) for t in thetas ]
    dr = [ r - dr[-phase % shape] for r in dr ]

    r = Face(((thickness, dr[0]), (thickness, length - dl[0]), (0, length - dl[-1]), (0, dr[-1])), origin = False);
    self.append(r, 'r0')
    self.renameedge('r0.e1', 'e1')
    self.renameedge('r0.e3', 'e3')

    angle = 360. / shape
    for i in range(1,len(thetas)):
      r = Face(((thickness, dr[i]), (thickness, length - dl[i]), (0, length - dl[i-1]), (0, dr[i-1])), origin = False);
      self.attach('e1', r, 'e3', 'r%d' % i, Fold(angle))
      self.renameedge('r%d.e1' % i, 'e1')

if __name__ == "__main__":
  import display
  b = Beam(10, 5, 45, 135, 7)
  b.transform(origin=(10,10))
  display.displayTkinter(b)
  #b.toSVG('testbeam.svg', labels=False, mode='Corel')
