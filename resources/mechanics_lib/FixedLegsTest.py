from google.protobuf import text_format
from api import proto_conversion
from api.symbolic import *

from api.component import Component

# Load new component object from yaml definition
f = Component("FixedLegs.yaml")
# Define free parameters
f.setParameter("depth", 9)
f.setParameter("height", 50)
f.setParameter("length", 28+19)
f.setParameter("leg.beamwidth", 10)

'''
f.symbolicize('depth')
f.symbolicize('height')
f.symbolicize('length')
f.symbolicize('leg.beamwidth')

'''

# Generate outputs
f.makeOutput("output/fixedlegs", display=True)

proto = proto_conversion.componentToNewTemplateProto(f)
text_format.PrintMessage(proto, open('output/fixedlegs.template.asciiproto', 'w'), 2)
open('output/fixedlegs.template.proto', 'wb').write(proto.SerializeToString())

quit()
