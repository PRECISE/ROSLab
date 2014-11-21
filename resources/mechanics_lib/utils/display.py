try:
  from api.drawing import *
  from api.edge import *
except:
  from ..api.drawing import *
  from ..api.edge import *

from Tkinter import *
import math

#def displayTkinter(dwg, height = 500, width = 700, showFlats = True):

class DisplayApp:
    def __init__(self, dwg, height = 500, width = 700, showFlats = True):
        self.root = Tk()
        self.root.title('Display')
        self.height = height
        self.width = width
        self.canvas = Canvas(self.root, height = self.height, width = self.width)
        self.canvas.focus_set() #creates the border
        self.canvas.grid(row =0, column =0, padx = 10, pady = 10)
        self.dwg = dwg
        
        self.scale = 1
        self.showFlats = showFlats
        #canvas.config(scrollregion = canvas.bbox(ALL))

        self.draw()
        self.createAddOns()
        self.bind()
        self.grid()

        self.pos_x = self.pos_y = 0.0

    def createAddOns(self):
        self.label = StringVar()
        self.mode = StringVar()
        self.coords = StringVar()
        self.currentc = StringVar()
        self.label1 = Label(self.root, textvariable = self.label, font = 100, relief = RIDGE, width = 15)
        self.label2 = Label(self.root,  textvariable = self.mode, font = 100,relief = RIDGE, width = 15)
        self.label3 = Label(self.root,  textvariable = self.coords , font = 100,relief = RIDGE)
        self.label4 = Label(self.root, textvariable = self.currentc)
        self.scrolly = Scrollbar(self.root, command = self.canvas.yview)
        self.scrollx = Scrollbar(self.root, orient = HORIZONTAL, command = self.canvas.xview)

        self.direction = Canvas(self.root, height = 50, width = 50)
        self.direction.create_line(0,0,0,0,arrow = LAST, tags = 'direction')
        

    def bind(self):
        self.canvas.bind('<Motion>', self.current)
        self.canvas.bind('<Button-1>', self.click)
        self.canvas.bind('<B1-Motion>', self.drag)
        self.canvas.bind('<MouseWheel>', self.zoom)
        
    def grid(self):
        self.scrolly.grid(row = 0, column = 1, sticky = N + S)
        self.scrollx.grid(row = 1, column = 0, sticky = E + W)
        
        self.label1.grid(row = 2, column = 0)
        self.label2.grid(row = 3, column = 0)
        self.label3.grid(row = 4, column = 0)
        self.label4.grid(row = 2, column = 1)
        self.direction.grid(row = 3, column = 0, sticky = S + E)

        #create_Rectangle = Button(
            
    def zoom(self, event):
        if event.delta > 0:
            self.scale = 1.2
        elif event.delta < 0:
            self.scale = .8
        self.canvas.scale(ALL, self.canvas.canvasx(event.x), self.canvas.canvasy(event.y), self.scale, self.scale)
        #redraw(canvas, event.x, event.y, img_id = True, k = scale)

    
    def draw(self):
        print 'REDRAWING'
        k = self.scale
        dwg = self.dwg
        print dwg
        color = "white"
        for e in dwg.edges.items():
            if e[1].edgetype.edgetype == EdgeType.NOEDGE: 
                continue
            if e[1].edgetype.edgetype == EdgeType.CUT:
                color = 'blue'
            elif e[1].edgetype.edgetype == EdgeType.FOLD:
                color = 'red'
            elif e[1].edgetype.edgetype == EdgeType.FLEX:
                color = 'purple'
            if e[1].edgetype.edgetype == EdgeType.FLAT:
                if self.showFlats:
                    color = 'black'
                else:
                    continue
            self.canvas.create_line(k*e[1].x1,k*e[1].y1,k*e[1].x2,k*e[1].y2, fill = color, activewidth = 5, tag = e[0])
    
    
    def click(self,event):
        edgename = self.canvas.gettags(event.widget.find_closest(self.canvas.canvasx(event.x),self.canvas.canvasy( event.y)))[0]
        self.label.set(edgename)
        self.mode.set(str(self.dwg.edges[edgename].edgetype))
        self.coords.set(str(self.dwg.edges[edgename].coords()))
        angle = self.dwg.edges[edgename].angle() 
        self.direction.coords('direction', 25,25,25+25*math.cos(angle),25+25*math.sin(angle))
        print edgename, self.dwg.edges[edgename].length()

        self._y = event.y
        self._x = event.x
        

    def drag(self,event):
        print "its working"
        y = (self._y-event.y)
        if y<0: y *= -1
        x = (self._x-event.x)
        if x<0: x *= -1
        
        self.canvas.yview("scroll",y/self.width,"units")
        self.canvas.xview("scroll",x/self.height,"units")

        self._x = event.x
        self._y = event.y
        
    def current(self,event):
        c = (self.canvas.canvasx(event.x), self.canvas.canvasy(event.y))
        self.currentc.set(str(c))
    
def displayTkinter(dwg, showFlats = True):
    d = DisplayApp(dwg, showFlats = showFlats)
    d.root.mainloop()
