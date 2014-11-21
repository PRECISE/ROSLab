from google.protobuf import text_format
import numpy as np
import triangle
import api.proto.symbolic_pb2 as proto_symbolic
import api.proto.template_pb2 as proto_template
class proto:
  symbolic = proto_symbolic
  template = proto_template
from api.symbolic import *

def symbolToProto(sym, out):
  out.name = sym.name
  if sym.default is not None:
    out.default = sym.default

def linearExprToProto(expr, symbol_id_dict, out):
  for symbol in expr.coeffs:
    out.parameter_id.append(symbol_id_dict[symbol.name])
    out.coeff.append(expr.coeffs[symbol])
  out.const = expr.const

def exprToProto(expr, symbol_id_dict, out):
  if isinstance(expr, LinearExpr):
    linearExprToProto(expr, symbol_id_dict, out)
  else:
    linearExprToProto(LinearExpr(const=expr), symbol_id_dict, out)

def point2ToProto(x, y, symbol_id_dict, out):
  exprToProto(x, symbol_id_dict, out.x)
  exprToProto(y, symbol_id_dict, out.y)

def edge2ToProto(edge, vertex_id_dict, out):
  out.name = edge.name
  out.vertex1_id = vertex_id_dict[tuple(edge.coords()[0])]
  out.vertex2_id = vertex_id_dict[tuple(edge.coords()[1])]

def face2ToProto(face, vertex_id_dict, edge_id_dict, edge_dict, out):
  out.name = face.name

  def edge_to_ids(edge):
    return tuple([vertex_id_dict[tuple(edge.coords()[i])] for i in range(2)])

  # note that the edge's vertices may come in either order, so we need to figure
  # out which vertex to use for each edge so that we don't get repetitions.
  prev_ids = edge_to_ids(edge_dict[face.edges[-1].name])
  for edge in face.edges:
    out.edge_id.append(edge_id_dict[edge.name])

  vertex_pairs = [edge_to_ids(edge_dict[edge.name]) for edge in face.edges]
  vertex_pairs_backup = [edge_to_ids(edge_dict[edge.name]) for edge in face.edges]
  for i in range(len(vertex_pairs)):
    vertex_pairs[i-1] = [x for x in vertex_pairs[i-1] if x not in vertex_pairs[i]]
  if not all(len(x)==1 for x in vertex_pairs):
    print "Warning: face " + face.name + "is invalid:", vertex_pairs_backup
    for i in range(len(vertex_pairs)):
      if len(vertex_pairs[i])==2:
        vertex_pairs[i] = [x for x in vertex_pairs[i] if x in vertex_pairs_backup[i-1]]
    if not all(len(x)==1 for x in vertex_pairs):
      print "    ... and failed to try fixing it."
    else:
      print "    chose vertices", [x[0] for x in vertex_pairs]
  if all(len(x)==1 for x in vertex_pairs):
    for x in vertex_pairs:
      out.vertex_id.append(x[0])
  else:
    pass

def point3ToProto(x, y, z, symbol_id_dict, out):
  exprToProto(x, symbol_id_dict, out.x)
  exprToProto(y, symbol_id_dict, out.y)
  exprToProto(z, symbol_id_dict, out.z)

def face3ToProto(name, vertices, vertex_id_dict, out):
  out.name = name
  added_vertices = []
  for vertex in vertices:
    vertex_id = vertex_id_dict[vertex]
    if vertex_id in added_vertices:
      print "Warning: face " + name + " has duplicated vertices. Skipping duplicated ones."
    else:
      out.vertex_id.append(vertex_id)
      added_vertices.append(vertex_id)

def transform(mat, pt):
  pt = np.array(list(pt) + [0, 1])
  pt = np.dot(mat, pt)
  pt = (pt[0] / pt[3], pt[1] / pt[3], pt[2] / pt[3])
  return pt

def drawingToProto(drawing, symbol_id_dict, out):
  vertex_id_dict = {}  # vertex coord to id
  edge_id_dict = {}  # edge name to id
  edge_dict = {}  # edge name to edge
  vertices = drawing.points()
  for vertex in vertices:
    vertex = tuple(vertex)
    id = len(vertex_id_dict)
    vertexProto = out.vertex.add()
    point2ToProto(vertex[0], vertex[1], symbol_id_dict, vertexProto.point)
    vertexProto.id = id
    vertex_id_dict[vertex] = id
  for edge_name in drawing.edges:
    edge = drawing.edges[edge_name]
    id = len(edge_id_dict)
    edgeProto = out.edge.add()
    edge2ToProto(edge, vertex_id_dict, edgeProto)
    edgeProto.name = edge_name  # somehow names may be different (from composition)
    edgeProto.id = id
    edge_id_dict[edge_name] = id
    edge_dict[edge_name] = edge
  for face in drawing.graph.faces:
    face2ToProto(face, vertex_id_dict, edge_id_dict, edge_dict, out.face.add())

def meshToProto(graph, symbol_id_dict, out):
  edges = []
  faces = []
  transform_matrix = np.eye(4)
  facesTouched = []
  graph.faces[0].getAll3DFaces(None, transform_matrix, faces, edges, facesTouched)
  vertex_id_dict = {}  # vertex coord to id

  def faceToProto(face, faceProto):
    name = face[2]
    mat = face[0]
    A = face[1]
    vertices = A['vertices']
    segments = A['segments']
    if any(segments[i][1] != segments[(i+1)%len(segments)][0] for i in range(len(segments))):
      print "Warning: face segments discontinuous: " + name, [[y for y in x] for x in segments]
      for i in range(len(segments)):
        if segments[i][1] == segments[0][0]:
          print "    chose the first", i+1, "segments only, to form a valid face."
          segments = segments[:i+1]
          break
      else:
        print "    ... and failed to recover from it."

    starts = [x[0] for x in segments]
    if len(starts) != len(set(starts)):
      print "Warning: face " + name + " has duplicate vertices!"
      return
    points = [
      transform(mat, vertices[p1])
      for (p1, p2) in segments
    ]
    for point in points:
      if point not in vertex_id_dict:
        id = len(vertex_id_dict)
        vertex_id_dict[point] = id
        vertexProto = out.vertex.add()
        point3ToProto(point[0], point[1], point[2], symbol_id_dict, vertexProto.point)
        vertexProto.id = id
    face3ToProto(name, points, vertex_id_dict, faceProto)
  for face in faces:
    faceToProto(face, out.face.add())

def componentToTemplateProto(component, out):
  symbol_id_dict = {}
  # add in three translation parameters
  for i in range(3):
    parameterProto = out.parameter.add()
    parameterProto.id = -i-1
    parameterProto.name = ['x', 'y', 'z'][i]
    parameterProto.default = 0
  for symbol_name in component.symbols:
    id = len(symbol_id_dict)
    symbol_id_dict[symbol_name] = id
    parameterProto = out.parameter.add()
    symbolToProto(component.symbols[symbol_name], parameterProto)
    parameterProto.id = id
  mappingFuncProto = out.mapping_function
  drawing2dProto = mappingFuncProto.linear_2
  mesh3dProto = mappingFuncProto.linear_3
  drawingToProto(component.drawing, symbol_id_dict, drawing2dProto.drawing)
  meshToProto(component.drawing.graph, symbol_id_dict, mesh3dProto.mesh)

  # go in and add translation to every vertex
  for vertex in mesh3dProto.mesh.vertex:
    vertex.point.x.coeff.append(1)
    vertex.point.x.parameter_id.append(-1)
    vertex.point.y.coeff.append(1)
    vertex.point.y.parameter_id.append(-2)
    vertex.point.z.coeff.append(1)
    vertex.point.z.parameter_id.append(-3)

  # add a constraint that every parameter (except translations) has to be > 0
  constraintsProto = out.feasible_set.constraint_list
  for symbol_name in component.symbols:
    constraintProto = constraintsProto.constraint.add()
    leftHand = -component.symbols[symbol_name].toLinearExpr()
    linearExprToProto(leftHand, symbol_id_dict, constraintProto.linear_constraint.expr)
    constraintProto.linear_constraint.type = 2

def componentToNewTemplateProto(component):
  out = proto.template.TemplateSet()
  templateProto = out.template.add()
  componentToTemplateProto(component, templateProto)
  templateProto.id = 1
  out.root_template_id = 1
  return out

def componentToProtoFile(component, filename, ascii=False):
  out = componentToNewTemplateProto(component)
  if ascii:
    text_format.PrintMessage(out, open(filename, 'w'), 2)
  else:
    open(filename, 'wb').write(out.SerializeToString())
