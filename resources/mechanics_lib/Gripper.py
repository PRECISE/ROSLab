from api.component import Component

class Gripper(Component):
    def defComponents(self):
        self.addSubcomponent("lbot","Beam")
        self.addSubcomponent("ltop","Beam")
        self.addSubcomponent("hinge","Hinge")
        #self.addSubcomponent("tetra","Tetrahedron")
        

    def defParameters(self):
        self.newParameter("length")
        self.newParameter("beamwidth")
        self.newParameter("shape")
        self.newParameter("rangle")
        self.newParameter("langle")
        self.newParameter("perimeter")

        self.setParameter("beamwidth",10)
        self.setParameter("perimeter",self.getParameter("beamwidth")*4)
        
    def defConstraints(self):
        self.addConstraint(("lbot","shape"),"shape")
        self.addConstraint(("ltop","shape"),"shape")

        self.addConstraint(("lbot","length"),"length")
        self.addConstraint(("ltop","length"),"length")

        self.addConstraint(("lbot","rangle"),"langle")
        self.addConstraint(("lbot","langle"),"langle")
        self.addConstraint(("ltop","langle"),"langle")

        self.addConstraint(("lbot","beamwidth"),"beamwidth")
        self.addConstraint(("ltop","beamwidth"),"beamwidth")

        self.addConstraint(("hinge","perimeter"),"perimeter")

    def defConnections(self):
        self.addConnection(("ltop", "botedge"),("hinge","topedge.0.4"),"Flex")
        #self.addConnection(("lbot","topedge"),("hinge","botedge"),"Flex")
        
if __name__=="__main__":
    c = Gripper()
    c.toYaml("output/gripper.yaml")
    c = None

    f = Component("output/gripper.yaml")
    f.setParameter("length",100)
    f.setParameter("shape",4)
    f.setParameter("rangle", 90)
    f.setParameter("langle", -90)

    f.make()

    f.drawing.transform(relative=(0,0))
    f.drawing.graph.toDXF("output/gripper.dxf")
    f.drawing.toSVG("output/gripper.svg")
    f.drawing.toDXF("output/autofold/gripper.dxf",mode="autofold")
    import utils.display
    utils.display.displayTkinter(f.drawing)
