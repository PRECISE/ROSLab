import graph 
import shapes

### Build a Face object for an equilateral triangle with side length 1
t = shapes.RegularNGon("triangle", 3, 1)
### Build a Face object for a square with side length 1
s = shapes.Square("square", 1)

### Build the graph for a tetrahedron by assembling 4 different triangle faces
### Faces are named one-four
### Edges are named for the two faces they connect
### Right hand rule for edge order, faces oriented outwards
tetra = graph.Graph()
tetra.addFace( t.copy("one"), ("12", "13", "14"))
tetra.addFace( t.copy("two"), ("12", "24", "23"))
tetra.addFace( t.copy("three"), ("13", "23", "34"))
tetra.addFace( t.copy("four"), ("14", "34", "24"))
#tetra.showGraph()

### Build the graph for a tetrahedron by assembling 4 triangles and a square
### same rules as above
pyramid = graph.Graph()
pyramid.addFace( s.copy("base"), ("b1", "b2", "b3", "b4"))
pyramid.addFace( t.copy("one"), ("b1", "14", "12"))
pyramid.addFace( t.copy("two"), ("b2", "12", "23"))
pyramid.addFace( t.copy("three"), ("b3", "23", "34"))
pyramid.addFace( t.copy("four"), ("b4", "34", "14"))
#pyramid.showGraph()

### Build the graph for a cube by assembling 6 squares
### same rules as above
cube = graph.Graph()
cube.addFace( s.copy("one"), ("12", "13", "14", "15"))
cube.addFace( s.copy("two"), ("12", "24", "26", "23"))
cube.addFace( s.copy("three"), ("13", "23", "36", "35"))
cube.addFace( s.copy("four"), ("14", "45", "46", "24"))
cube.addFace( s.copy("five"), ("15", "35", "56", "45"))
cube.addFace( s.copy("six"), ("26", "46", "56", "36"))
#cube.showGraph()

### Build a new graph that has a tetrahedron named "t" and a cube named "c" attached along an edge
### Merge edge "12" of the tetrahedron with edge "56" of the cube
### There's no geometry checks to make sure the edges are congruent [ yet ]
join = graph.Graph.joinAlongEdge((tetra, "t", "12"), (cube, "c", "56"))
#join.showGraph()

### Build a new graph that has a pyramid named "p2" and a cube named "c" attached along a face
### Stick face "base" of the pyramid onto face "one" of the cube
### Keep both (2) copies of the face where they meet, and just merge their surrounding edges
### There's no geometry checks to make sure the faces are congruent [ yet ]
join = graph.Graph.joinAlongFace((pyramid, "p2", "base"), (cube, "c", "one"), 2)
#join.showGraph()

### Build a new graph that has a pyramid and a cube attached along a face
### Keep only 1 layer at the face where they meet
join = graph.Graph.joinAlongFace((pyramid, "p1", "base"), (cube, "c", "one"), 1)
#join.showGraph()

### Build a new graph that has a pyramid and a cube attached along a face
### Remove the joining face altogether
join = graph.Graph.joinAlongFace((pyramid, "p0", "base"), (cube, "c", "one"), 0)
### Just for kicks, attach a couple extra faces inside the cube, joined to various edges
### We don't check any geometry to make sure this is reasonable
### Also remove one of the pyramid faces
join.addFace( s.copy("new1"), ("c.23", "newtop", "c.45", None))
join.addFace( s.copy("new2"), ("c.36", None, "newtop", None))
join.delFace( "p0.two" )
join.showGraph()

