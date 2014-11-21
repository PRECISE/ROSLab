from api.component import Component

class Fulcrum(Component):
  def defComponents(self):
    # Subcomponents used in this assembly
    self.addSubcomponent("stem", "Hinge")
    self.addSubcomponent("left", "RectBeam")
    self.addSubcomponent("right", "RectBeam")
    self.addSubcomponent("t", "TJoint")

  def defParameters(self):
    # Subcomponent free parameters are inherited by default
    # Subcomponent parameters that are no longer free in this assembly are deleted
    '''
    self.delParameter("length")
    self.delParameter("width")
    self.delParameter("depth")
    self.delParameter("angle")
    self.delParameter("rangle")
    self.delParameter("langle")
    self.delParameter("phase")
    self.delParameter("noflap")
    self.delParameter("faces")
    '''
    
    # New free parameters specific to this assembly are added
    self.newParameter("leftlength")
    self.newParameter("rightlength")

    self.newParameter("stemwidth")
    self.newParameter("crosswidth")
    self.newParameter("thickness")

  def defInterfaces(self):
    # Locations on FixedLegs component that higher order components can use for assembly
    self.newInterface("stemedge")
    self.newInterface("leftedge")
    self.newInterface("rightedge")
    self.newInterface("lefttab")

  def defConstraints(self):
    ### Set specific relationships between parameters

    self.addConstraint(("stem", "perimeter"), ("stemwidth", "thickness"), "2 * sum(x)")
    self.addConstraint(("stem", "top"), ("stemwidth", "thickness"), "(x[1]-x[0]) * 1.0 / sum(x)")
    self.addConstraint(("stem", "bot"), ("stemwidth", "thickness"), "(x[1]-x[0]) * 1.0 / sum(x)")

    self.addConstraint(("left", "depth"), ("thickness"))
    self.addConstraint(("left", "width"), ("crosswidth"))
    self.addConstraint(("left", "length"), ("leftlength"))

    self.addConstraint(("right", "depth"), ("thickness"))
    self.addConstraint(("right", "width"), ("crosswidth"))
    self.addConstraint(("right", "length"), ("rightlength"))

    self.addConstraint(("t", "thickness"), "thickness")
    self.addConstraint(("t", "crosswidth"), "crosswidth")
    self.addConstraint(("t", "stemwidth"), "stemwidth")

  def defConnections(self):
    self.addConnection(("t", "leftedge"),
                       ("left", "botedge.0.3"), "Flat")
    self.addConnection(("t", "rightedge"),
                       ("right", "topedge.0.3"), "Flat")
    self.addConnection(("t", "stemedge"),
                       ("stem", "topedge.1"), 
                       "Fold", angle=(-70.5/2))
    # XXX Not well shaped -- leaves overhang
    self.addConnection(("t", "stemtab"),
                       ("stem", "topedge.3"),
                       "Tab",
                       name="tab", depth=10, angle=(-70.5/2))

  def defInterfaces(self):
    # Define interface locations in terms of subcomponent interfaces
    self.inheritInterface("stemedge", ("stem", "botedge"))
    self.inheritInterface("lefttab", ("left", "tabedge"))
    self.inheritInterface("leftedge", ("left", "topedge"))
    self.inheritInterface("rightedge", ("right", "botedge"))

if __name__ == "__main__":

  # Instantiate new object
  f = Fulcrum()
  # Define free parameters
  f.setParameter("thickness", 10)
  f.setParameter("stemwidth", 20)
  f.setParameter("crosswidth", 30)
  f.setParameter("leftlength", 50)
  f.setParameter("rightlength", 100)

  # Generate outputs
  f.make()
  f.drawing.graph.toSTL("output/tbar.stl")
  f.drawing.transform(relative=(0,0))
  import utils.display
  utils.display.displayTkinter(f.drawing)

