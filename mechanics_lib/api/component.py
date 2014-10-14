from api.symbolic import Symbol
from drawing import Drawing
import edge
import sys

def getSubcomponentObject(c):
  try:
    mod = __import__(c, fromlist=[c])
    obj = getattr(mod, c)
    return obj()
  except ImportError:
    obj = Component(c + ".yaml")
    return obj

class Component:
  def __init__(self, yamlFile=None):
    self.rebuild()
    if yamlFile:
      self.fromYaml(yamlFile)

  def rebuild(self):
    self.reset()
    self.define()

  def reset(self):
    self.parameters = {}
    self.symbols = {}
    self.components = {}
    self.subcomponents = {}
    self.interfaces = {}
    self.connections = []

    self.drawing = None
    self.ecomponent = None

  def toYaml(self, filename):
    definition = {
      "parameters" : self.parameters,
      "subcomponents" : self.subcomponents, 
      "connections" : self.connections,
      "interfaces" : self.interfaces, 
    }

    import yaml
    with open(filename, "w") as fn:
      yaml.safe_dump(definition, fn)

  def fromYaml(self, filename):
    import yaml
    with open(filename) as fn:
      definition = yaml.safe_load(fn)

    self.parameters =  definition["parameters"]
    self.subcomponents =  definition["subcomponents"]
    self.connections = definition["connections"]
    self.interfaces =  definition["interfaces"]

  def define(self):
    # define subcomponents
    self.defComponents()
    # define additional parameters
    self.defParameters()
    # define parameter constraints
    self.defConstraints()
    # define subcomponent connections
    self.defConnections()
    # define interfaces
    self.defInterfaces()

  def defComponents(self):
    ### Override to define subcomponents
    pass
  def defParameters(self):
    ### Override to define parameters
    pass
  def defConstraints(self):
    ### Override to define parameter constraints
    pass
  def defConnections(self):
    ### Override to define component connections
    pass
  def defInterfaces(self):
    ### Override to define interfaces
    pass

  def addEComponent(self, ecomponent):
    self.ecomponent = ecomponent
    return self

  def addComponent(self, name, obj, classname = ""):
    self.components.setdefault(name, (obj, classname))
    return self
 
  def addSubcomponent(self, name, obj, inherit=False, prefix = ""):
    sc = {"object": obj, "parameters": {}}
    self.subcomponents.setdefault(name, sc)
    if inherit:
      if prefix == "":
        prefix = name + "."
      elif prefix:
        prefix += "."
      else:
        prefix = ""

      obj = getSubcomponentObject(obj)
      for key, value in obj.parameters.iteritems():
        try:
          self.newParameter(prefix + key, value)
        except KeyError:
          # It's ok if we try to add a parameter that already exists
          pass
        self.addConstraint((name, key), prefix + key)
      
    return self

  def resolveSubcomponents(self):
    for (name, sc) in self.subcomponents.iteritems():
      c = sc["object"]
      obj = getSubcomponentObject(c)
      self.addComponent(name, obj, c)

  def getComponent(self, name):
    return self.components[name][0]

  def newParameter(self, name, value=None):
    if name in self.parameters:
      raise KeyError("Parameter %s already exists" % name)
    self.parameters.setdefault(name, value)
    return self

  def setParameter(self, n, v):
    if n in self.parameters:
      self.parameters[n] = v
    else:
      raise KeyError("Parameter %s not initialized" % n)
    return self

  def getParameter(self, name, strict=True):
    if strict and self.parameters[name] is None:
      raise KeyError("Parameter %s not yet set" % name)
    return self.parameters[name]

  def setSubParameter(self, c, n, v):
    self.getComponent(c).setParameter(n, v)

  def delParameter(self, name):
    self.parameters.pop(name)
    return self

  def newInterface(self, name, val = None):
    if name in self.interfaces:
      raise ValueError("Interface %s already exists" % name)
    self.interfaces.setdefault(name, val)
    return self

  def setInterface(self, n, v):
    if n in self.interfaces:
      self.interfaces[n] = v
    else:
      raise KeyError("Interface %s not initialized" % n)
    return self

  def inheritInterface(self, name, (subcomponent, subname)):
    if name in self.interfaces:
      raise ValueError("Interface %s already exists" % name)
    self.interfaces.setdefault(name, {"subcomponent": subcomponent, "interface": subname})
    return self

  def getInterfaces(self, component, name, prefix="", index=slice(None)):
    return self.getComponent(component).getInterface(name, prefix, index)

  def getInterface(self, name, prefix="", index=slice(None)):
    names = name.split(".")

    c = self.interfaces[names[0]]

    if isinstance(c, dict):
      subc = c["subcomponent"]
      names[0] = c["interface"]
      name = ".".join(names)
      if prefix:
        prefix = prefix + "." + subc
      else:
        prefix = subc
      return self.getComponent(subc).getInterface(name, prefix, index)

    if not isinstance(c, (list, tuple)):
      c = [c]

    if len(names) == 2:
      index = eval(names[1])
    if len(names) == 3:
      index = slice(eval(names[1]), eval(names[2]))

    if isinstance(index, int):
      index = slice(index, index+1)

    if prefix:
      prefix += "."
    ret = [prefix + s for s in c[index]]
    if len(ret) == 1:
      return ret[0]
    else:
      return ret

  # TODO(mehtank) : yaml-ify default function in a simpler way 
  def addConstraint(self, (subComponent, parameterName), inputs, function="x"):
    # XXX silently overwrites existing constraints, is that ok?
    self.subcomponents[subComponent]["parameters"][parameterName] = [function, inputs]

  # TODO(mehtank) : yaml-ify this in a simpler way 
  def addConstConstraint(self, (subComponent, parameterName), value):
    self.subcomponents[subComponent]["parameters"][parameterName] = [repr(value), None]

  def evalConstraints(self):
    for subComponent in self.subcomponents.iterkeys():
      for (parameterName, (fn, inputs)) in self.subcomponents[subComponent]["parameters"].iteritems():
        
        function = eval("lambda x : " + fn)
        if isinstance(inputs, (list, tuple)):
          x = function(map(lambda x : self.getParameter(x, strict=False), inputs))
        elif inputs:
          x = function(self.getParameter(inputs, strict=False))
        else:
          x = function(None)

        self.setSubParameter(subComponent, parameterName, x)

  def addConnection(self, fromInterface, toInterface, interfaceType, **kwargs):
    self.connections.append([fromInterface, toInterface, interfaceType, kwargs])

  def evalConnections(self):
    addedComponents = set()
    for ((fromComponent, fromEdge), (toComponent, toEdge), interfaceType, kwargs) in self.connections:
      if fromComponent not in addedComponents:
        self.append(fromComponent, fromComponent)
        addedComponents.add(fromComponent)

      try:
        mod = __import__("api.edge", fromlist=interfaceType)
        interface = getattr(mod, interfaceType)

        self.attach((fromComponent, fromComponent, fromEdge),
                    (toComponent, toComponent, toEdge),
                    interface(**kwargs))
        addedComponents.add(toComponent)
      except AttributeError:
        mod = __import__("connector", fromlist=interfaceType)
        interface = getattr(mod, interfaceType)
        self.addTabs((interface(), kwargs.pop("name"), kwargs.pop("depth")),
                     (fromComponent, fromComponent, fromEdge),
                     (toComponent, toComponent, toEdge),
                     **kwargs)

  def makeComponentHierarchy(self):
    self.resolveSubcomponents()
    hierarchy = {}
    for n, (sub, c) in self.components.iteritems():
      hierarchy[n] = {"class":c, "subtree":sub.makeComponentHierarchy()}
    return hierarchy

  def makeComponentTree(self, fn, root="Root"):
    import pydot
    graph = pydot.Dot(graph_type='graph')
    mynode = pydot.Node(root)
    self.recurseComponentTree(graph, mynode, root)
    graph.write_png(fn)

  def recurseComponentTree(self, graph, mynode, myname):
    import pydot
    self.resolveSubcomponents()
    for n, (sub, c) in self.components.iteritems():
      fullstr = myname + "/" + n
      subnode = pydot.Node(fullstr, label = c + "\n" + n)
      graph.add_node(subnode)
      edge = pydot.Edge(mynode, subnode)
      graph.add_edge(edge)
      sub.recurseComponentTree(graph, subnode, fullstr)

  def make(self):
    self.resolveSubcomponents()
    self.evalConstraints()
    self.defSubParameters()

    for (name, (sub, c)) in self.components.iteritems():
      try:
        sub.make()
      except:
        print "Error in subclass %s, instance %s" % (c, name)
        raise
      
    self.drawing = Drawing()
    self.evalConnections()
    self.assemble()

  def makeOutput(self, filedir=".", tree=True, unfolding=True, autofolding=True, stl=True, code=True, 
                 display=False, displayOnly=False):
    print "Compiling robot designs...",
    sys.stdout.flush()
    self.make()
    print "done."

    # XXX: Is this the right way to do it?
    import os
    try:
      os.makedirs(filedir)
    except:
      pass

    # XXX: Shouldn't be forced
    self.drawing.transform(relative=(0,0))

    if display or displayOnly:
      import utils.display
      utils.display.displayTkinter(self.drawing)
    if displayOnly:
      return

    if tree:
      print "Generating hierarchy tree... ",
      sys.stdout.flush()
      self.makeComponentTree(filedir + "/tree.png")
      print "done."
    if unfolding:
      print "Generating cut-and-fold pattern... ",
      sys.stdout.flush()
      self.drawing.toSVG(filedir + "/lasercutter.svg", mode="Corel")
      print "done."
    if autofolding:
      print "Generating autofolding pattern... ",
      sys.stdout.flush()
      self.drawing.toDXF(filedir + "/autofold-default.dxf", mode="autofold")
      print "(graph) ... ",
      sys.stdout.flush()
      self.drawing.graph.toDXF(filedir + "/autofold-graph.dxf")
      print "done."
    if stl:
      print "Generating 3D model... ",
      sys.stdout.flush()
      self.drawing.graph.toSTL(filedir + "/model.stl")
      print "done."
    print
    if code and self.ecomponent is not None:
      print "Electrical connections:"
      print "======================:"
      self.ecomponent.generateCode(destDir = filedir + "/code")
      print

    print "Happy roboting!"

  def defSubParameters(self): 
    ### Override to set parameters of components
    pass

  def assemble(self): 
    ### Override to combine components' drawings to final drawing
    pass

  def append(self, name, prefix):
    # TODO(mehtank): make prefix optional?
    component = self.getComponent(name)

    self.drawing.append(component.drawing, prefix)

    if self.ecomponent is None:
      self.ecomponent = component.ecomponent
    else:
      if component.ecomponent is not None:
        self.ecomponent.attach(component.ecomponent)

  def attach(self, (fromName, fromPrefix, fromEdge), (toName, toPrefix, toEdge), edgetype):
    # TODO(mehtank): make names optional
    self.drawing.attach(self.getInterfaces(fromName, fromEdge, fromPrefix),
                        self.getComponent(toName).drawing,
                        self.getInterfaces(toName, toEdge),
                        toPrefix,
                        edgetype)

    if self.ecomponent is not None:
      if self.getComponent(toName).ecomponent is not None:
        self.ecomponent.attach(self.getComponent(toName).ecomponent)

  def decorate(self, component, face, decoration, offset=(0, 0), mode=None):
    g = self.getComponent(component).drawing.graph
    d = self.getComponent(decoration).drawing.graph
    for f in d.faces:
      g.getFace(face).addDecoration(([(p[0]+offset[0], p[1]+offset[1]) for p in f.pts2d], mode))

  def addTabs(self, (conn, cname, depth), froms, tos, tabattachment=None, angle=0):
    # XXX does this belong here?

    if froms is None:
      f = None
    else:
      (fromName, fromPrefix, fromEdge) = froms
      f = self.getInterfaces(fromName, fromEdge, fromPrefix)
      
    if tos is None:
      t = None
    else:
      (toName, toPrefix, toEdge) = tos
      t = self.getInterfaces(toName, toEdge, toPrefix)

    self.addConnectors((conn, cname),
                       f, t, 
                       depth, tabattachment = tabattachment, angle=angle)

  def addConnectors(self, (conn, cname), tabedge, slotedge, depth, tabattachment=None, angle=0):
    if tabattachment is None: tabattachment=(edge.Flex(angle), edge.Cut())
    # XXX does this belong here?
    if tabedge is not None:
      conn.setParameter("thickness", self.drawing.edges[tabedge].length())
    else:
      conn.setParameter("thickness", self.drawing.edges[slotedge].length())
    conn.setParameter("depth", depth) 
    conn.make()

    if tabedge is not None:
      self.drawing.attach(tabedge,
                          conn.drawing,
                          "e0",
                          cname+"tab",
                          tabattachment[0], useOrigName=True)

    if slotedge is not None:
      self.drawing.attach(slotedge,
                          conn.slots(),
                          "e0",
                          cname+"slot",
                          tabattachment[1], useOrigName=True)

  def symbolicize(self, name):
    default = self.getParameter(name)
    symbol = Symbol(name, default)
    self.setParameter(name, symbol.toLinearExpr())
    self.addSymbol(name, symbol)

  def symbolicizeSub(self, param, name):
    default = self.getParameter(param).getParameter(name)
    symbolName = param + "." + name
    symbol = Symbol(symbolName, default)
    self.getParameter(param).setParameter(name, symbol.toLinearExpr())
    self.addSymbol(symbolName, symbol)

  def addSymbol(self, name, symbol):
    self.symbols[name] = symbol
