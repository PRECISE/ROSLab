ROSLab
======

Requirements: Java 7

1. Running ROSLab 
ROSLab’s main executable file is aptly named ‘roslab.jar’.  You can find this executable located in the root directory of the compressed release package.  Once you have extracted the contents of the release package, ensure that the 'modules.xml' file and the 'templates' directory are also present in the directory alongside the 'roslab.jar' file.  To run ROSLab, simply double-click the 'roslab.jar' file.

The ROSLab Development Environment consists of a single split-window interface which includes a library of built-in node types and a configuration design canvas. The type library can be expanded with user-defined nodes, a process that is described in Section 2. On the design canvas, a user can build his/her desired configuration of components and connections.  The configuration elements can be rearranged as necessary for clarity.  The process of building a configuration is described in Section 3.

2. Making a user-defined node
A user-defined node can be created by right-clicking the ‘User Defined Nodes’ tree element and selecting the ‘New Component Type…’ menu item from the popup menu.  After choosing a name for the new node definition, a new element will be created under the ‘User Defined Nodes’ category. This newly-created node can now have its ports specified according to designer’s goals. New ports can be added by right-clicking on the node element and selecting ‘New port…’. From the ‘New Port Type’ dialog that appears, the user can specify the name, type, and direction of the port to be created.

3. Building a configuration
 a. Adding a component
  A component can be added to the design canvas by right-clicking on the canvas and selecting ‘Add component’. Depending on whether the user is choosing to add a built-in type or a user-defined type, he/she must choose ‘ROS Service’ or ‘ROS Node’, respectively. A dialog will appear to allow the user to define the name and type of the component instance to be added. After the user selects ‘OK’, the component instance will appear on the design canvas where it can be dragged and dropped to any desired location.
 b. Adding a connection
  A connection can be made between a port on each of two components by right-clicking on the canvas and selecting ‘Add connection’. In the ‘New Connection’ dialog that appears, the user can choose which publisher port will connect to which subscriber port. The publisher/subscriber pairing will only be possible if the types of both ports match.  
 
4. Generating a ROS Node bundle
When a configuration is fully defined, the user can choose to have ROSLab generate a set of ROS source code files representing the designed configuration. This source code bundle can then be compiled and run on a system running ROS.  Initiating this process is as simple as highlighting and right-clicking on the name of the appropriate configuration.

5. Adding a new node (ex. sensor/actuator) to the built-in library
The library of built-in nodes is initialized during ROSLab’s startup process based on the contents of the ‘modules.xml’ file. This file contains an XML representation of the library’s structure, and it can be modified by a user to include any node types that they would like to have included as built-in nodes. The process of defining a new node involves choosing its name, type, and ports.  Defining a node’s ports requires the user to decide the directionality of each port (send/receive, ie. publish/subscribe), the name of the port, and the port’s type. An example node definition is included at the top of the file to help guide the user in this process.
