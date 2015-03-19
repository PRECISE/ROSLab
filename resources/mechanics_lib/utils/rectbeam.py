try:
  from api.drawing import Drawing
  from api.shapes import Face, Rectangle
  from api.edge import Fold
except:
  from ..api.drawing import Drawing
  from ..api.shapes import Face, Rectangle
  from ..api.edge import Fold

import numpy as np

class RectBeam(Drawing):
  """
  A class representing a Beam.
  """
  def __init__(self, length, (width, depth), langle=90, rangle=90, phase=0, faces=None):
    """
    Initializes a Beam object 

    @type length: 
    @param length: 
    @return: a Beam object
    """

    Drawing.__init__(self)

    langle = 90 - langle
    rangle = 90 - rangle
    if faces is None:
      faces = range(4)

    rs = []
    rs.append(Rectangle(width, length))
    rs.append(Face((
      (depth, np.tan(np.deg2rad(langle)) * depth), 
      (depth, length - np.tan(np.deg2rad(rangle)) * depth), 
      (0, length)
    )))
    rs.append(Rectangle(width, length - (np.tan(np.deg2rad(langle)) + np.tan(np.deg2rad(rangle))) * depth))
    rs.append(Face((
      (0, length), (0,0),
      (depth, np.tan(np.deg2rad(rangle)) * depth),
      (depth, length - np.tan(np.deg2rad(langle)) * depth),
    ), origin=False))

    for i in range(phase):
      rs.append(rs.pop(0))

    i = faces[0]
    self.append(rs[i], 'r%d'%i)
    self.renameedge('r%d.e1'%i, 'e1')
    self.renameedge('r%d.e3'%i, 'e3')

    for i in faces[1:]:
      self.attach('e1', rs[i], 'e3', 'r%d' % i, Fold(90))
      self.renameedge('r%d.e1' % i, 'e1')

if __name__ == "__main__":
  print "hi"
  import display
  b = RectBeam(10, (5, 2), 45, 135)
  b.transform(origin=(10,10))
  display.displayTkinter(b)
  #b.toSVG('testbeam.svg', labels=False, mode='Corel')
