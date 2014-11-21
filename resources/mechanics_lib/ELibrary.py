from EDevice import EDevice
from EModule import EModule

# Define some devices and modules to use

digitalLED = EDevice("Digital LED", "D_O")
analogLED = EDevice("Analog LED", "A_O")
digitalSensor = EDevice("Digital Sensor", "D_I")
analogSensor = EDevice("Analog Sensor", "A_I")
servo = EDevice("Servo", "SERVO")
jointServo = EDevice("Joint Servo", "SERVO")
gripperServo = EDevice("Gripper Servo", "SERVO")
motor = EDevice("Motor", "SERVO")
empty = EDevice("Nothing", "D_I")

joint = EModule("Joint")
joint.setEDevices([analogLED, jointServo, analogLED])
gripper = EModule("Gripper")
gripper.setEDevices([analogLED, gripperServo, analogLED])

class Joint(EModule):
    """
    A joint module, which will have two LEDs and a servo
    """

    def __init__(self, name):
        EModule.__init__(self, name)
        EModule.setEDevices(self, [analogLED, jointServo, analogLED])

class Gripper(EModule):
    """
    A gripper module, which will have two LEDs and a servo
    """

    def __init__(self, name):
        EModule.__init__(self, name)
        EModule.setEDevices(self, [analogLED, gripperServo, analogLED])
