from api.component import Component

class TBar(Component):
  def defComponents(self):
    # Subcomponents used in this assembly
    self.addSubcomponent("stem", "RectBeam")
    self.addSubcomponent("left", "RectBeam")
    self.addSubcomponent("right", "RectBeam")
    self.addSubcomponent("t", "TJoint")

  def defParameters(self):
    # New free parameters specific to this assembly are added
    self.newParameter("stemlength")
    self.newParameter("leftlength")
    self.newParameter("rightlength")
    self.newParameter("thickness")
    self.newParameter("stemwidth")
    self.newParameter("crosswidth")

  def defConstraints(self):
    ### Set specific relationships between parameters
    self.addConstraint(("stem", "depth"), "thickness")
    self.addConstraint(("stem", "width"), "stemwidth")
    self.addConstraint(("stem", "length"), "stemlength")

    self.addConstraint(("left", "depth"), "thickness")
    self.addConstraint(("left", "width"), "crosswidth")
    self.addConstraint(("left", "length"), "leftlength")

    self.addConstraint(("right", "depth"), "thickness")
    self.addConstraint(("right", "width"), "crosswidth")
    self.addConstraint(("right", "length"), "rightlength")

    self.addConstraint(("t", "thickness"), "thickness")
    self.addConstraint(("t", "crosswidth"), "crosswidth")
    self.addConstraint(("t", "stemwidth"), "stemwidth")

  def defConnections(self):
    self.addConnection(("t", "leftedge"),
                       ("left", "botedge.0.3"), "Flat")
    self.addConnection(("t", "rightedge"),
                       ("right", "topedge.0.3"), "Flat")
    self.addConnection(("t", "stemedge"),
                       ("stem", "topedge.0"), "Flat")
    self.addConnection(("t", "stemtab"),
                       ("stem", "topedge.2"),
                       "Tab",
                       name="tab", depth=10)

  def defInterfaces(self):
    self.inheritInterface("stemedge", ("stem", "botedge"))
    self.inheritInterface("leftedge", ("left", "topedge"))
    self.inheritInterface("rightedge", ("right", "botedge"))

if __name__ == "__main__":

  # Instantiate new object
  f = TBar()
  # Define free parameters
  f.setParameter("thickness", 10)
  f.setParameter("stemwidth", 50)
  f.setParameter("crosswidth", 30)
  f.setParameter("stemlength", 150)
  f.setParameter("leftlength", 50)
  f.setParameter("rightlength", 100)

  # Generate outputs
  f.make()
  f.drawing.graph.toSTL("output/tbar.stl")
  f.drawing.transform(relative=(0,0))
  import utils.display
  utils.display.displayTkinter(f.drawing)

