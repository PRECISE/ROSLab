from api.component import Component

class Finger(Component):
    def defComponents(self):
        self.addSubcomponent("lbot","Beam")
        self.addSubcomponent("ltop","Beam")
        self.addSubcomponent("hinge","Hinge")
        #self.addSubcomponent("tetra","Tetrahedron")
        

    def defParameters(self):
        self.newParameter("length")
        self.newParameter("beamwidth")
        self.newParameter("shape")
        self.newParameter("perimeter")

        self.setParameter("beamwidth",10)
        self.setParameter("perimeter",self.getParameter("beamwidth")*4)
        
    def defConstraints(self):
        self.addConstraint(("lbot","shape"),"shape")
        self.addConstraint(("ltop","shape"),"shape")

        self.addConstraint(("lbot","length"),"length")
        self.addConstraint(("ltop","length"),"length")

        self.addConstraint(("lbot","beamwidth"),"beamwidth")
        self.addConstraint(("ltop","beamwidth"),"beamwidth")

        self.addConstraint(("hinge","perimeter"),"perimeter")

    def defConnections(self):
        self.addConnection(("ltop", "botedge"),("hinge","topedge.1.5"),"Flex")
        self.addConnection(("hinge","botedge.1.5"),("lbot","topedge"),"Flex")

    def defInterfaces(self):
        self.inheritInterface("topedge",("ltop","topedge"))
        self.inheritInterface("botedge",("lbot","botedge"))
        
if __name__=="__main__":
    c = Finger()
    c.toYaml("output/finger.yaml")
    c = None

    f = Component("output/finger.yaml")
    f.setParameter("length",100)
    f.setParameter("shape",4)

    f.make()

    f.drawing.transform(relative=(0,0))
    f.drawing.graph.toDXF("output/finger.dxf")
    f.drawing.toSVG("output/finger.svg")
    f.drawing.toDXF("output/autofold/finger.dxf",mode="autofold")
    import utils.display
    utils.display.displayTkinter(f.drawing)
