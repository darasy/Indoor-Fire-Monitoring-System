#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

from programmingtheiot.data.SensorData import SensorData

import programmingtheiot.common.ConfigConst as ConfigConst

from programmingtheiot.common.ConfigUtil import ConfigUtil
from programmingtheiot.cda.sim.BaseSensorSimTask import BaseSensorSimTask
from programmingtheiot.cda.sim.SensorDataGenerator import SensorDataGenerator

from pisense import SenseHAT

class HumiditySensorEmulatorTask(BaseSensorSimTask):
    """
    Shell representation of class for humidity sensor emulator task.
    
    """

    def __init__(self, dataSet = None):
        """
        Constructor
        """
        # Derive from BaseSensorSimTask
        super(HumiditySensorEmulatorTask, self).__init__(sensorType=SensorData.HUMIDITY_SENSOR_TYPE, minVal = SensorDataGenerator.LOW_NORMAL_ENV_HUMIDITY, maxVal = SensorDataGenerator.HI_NORMAL_ENV_HUMIDITY)
        # Create an instance of SenseHAT and set the emulate flag to True if running the emulator, or False if using real hardware
        # This can be read from ConfigUtil using the ConfigConst.CONSTRAINED_DEVICE section and the ConfigConst.ENABLE_SENSE_HAT_KEY
        # If the ConfigConst.ENABLE_SENSE_HAT_KEY is False, set the emulate flag to True, otherwise set to False
        self.configUtil = ConfigUtil()
        enableEmulation = False
        if self.configUtil.getProperty(ConfigConst.CONSTRAINED_DEVICE, ConfigConst.ENABLE_SENSE_HAT_KEY) == "False":
            enableEmulation = True
        self.sh = SenseHAT(emulate = enableEmulation)

    # A function to generate the telemetry
    def generateTelemetry(self) -> SensorData:
        """
        A function to generate the telemetry  
        
        @return SensorData
        """
        # Create a Sensor Data with sensorType
        sensorData = SensorData(name = ConfigConst.HUMIDITY_SENSOR_NAME, sensorType = self.sensorType)
        # Set a value to the SenseHat generated data
        sensorVal = self.sh.environ.humidity        
        sensorData.setValue(sensorVal)
        # Set the latestSensorData to the newly created SensorData
        self.latestSensorData = sensorData
        
        return sensorData
