from api.component import Component

###################
# Component builder
###################

c = Component()

### Subcomponents used in this assembly
c.addSubcomponent("bar", "RectBeam")
c.addSubcomponent("leg1", "PointedLeg")
c.addSubcomponent("leg2", "PointedLeg")
c.addSubcomponent("split", "SplitEdge")

### New free parameters specific to this assembly 
c.newParameter("depth")
c.newParameter("height")
c.newParameter("length")
c.newParameter("leg.beamwidth")

### Subcomponent parameter inheritance
# Constrain one length of the RectBeam object based on new FixedLegs parameter
c.addConstraint(("bar", "length"), "length")
# Constrain one parameter of the RectBeam object based on PointedLeg parameter
c.addConstraint(("bar", "depth"), "leg.beamwidth")
# Constrain other parameter of the RectBeam object based on new FixedLegs parameter
c.addConstraint(("bar", "width"), "depth")

# Constrain one parameter of the PointedLeg object based on new FixedLegs parameter
c.addConstraint(("leg1", "length"), "height")
# Constrain one parameter of the RectBeam object based on PointedLeg parameter
c.addConstraint(("leg1", "beamwidth"), "leg.beamwidth")

# Constrain one parameter of the PointedLeg object based on new FixedLegs parameter
c.addConstraint(("leg2", "length"), "height")
# Constrain one parameter of the RectBeam object based on PointedLeg parameter
c.addConstraint(("leg2", "beamwidth"), "leg.beamwidth")

# Break apart the edge where the two PointedLegs will connect 
c.addConstraint(("split", "botlength"), ("length", "leg.beamwidth"), "(x[0],)")
c.addConstraint(("split", "toplength"), ("length", "leg.beamwidth"), "(x[1], x[0] - 2*x[1], x[1])")

### Subcomponents connections
# SplitEdge component to define multiple attachment points
c.addConnection(("bar", "tabedge"),
                ("split", "botedge"), 
                "Flat")
# Attach one leg
c.addConnection(("split", "topedge.2"),
                ("leg1", "front"), 
                "Flat")
# Attach other leg
c.addConnection(("split", "topedge.0"),
                ("leg2", "right"), 
                "Flat")

# Add tabs for rigid attachment
c.addConnection(("leg1", "right"), 
                ("bar", "botedge.1"), 
                "Tab", 
                name="tab1", angle=90, depth=6)

c.addConnection(("leg2", "front"), 
                ("bar", "topedge.1"), 
                "Tab", 
                name="tab2", angle=90, depth=6)

### Exoposed interfaces
# Locations on FixedLegs component that higher order components can use for assembly
c.inheritInterface("topedge", ("bar", "topedge"))
c.inheritInterface("botedge", ("bar", "botedge"))

c.toYaml("FixedLegs.yaml")

