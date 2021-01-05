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
import logging
import random 

class BaseSystemUtilTask():
    """
    Shell representation of class for base system utility task.
    """
    
    def __init__(self, sensorName = ConfigConst.NOT_SET):
        """
        Constructor
        
        @param sensorNAme: default to Not Set
        """
        self.sensorName = sensorName
        self.latestSensorData = None
        self.minVal = 0.0
        self.maxVal = 100.0
    
    def generateTelemetry(self) -> SensorData:
        """
        A method to generate SensorData
        
        @return SensorData
        """
        ###
        # NOTE: Use self._getSystemUtil() to retrieve the value from the sub-class
        # Create a new SensorData instance and 
        sd = SensorData()
        # Set the value of new SensorData to be the return value of self._getSystemUtil()
        sd.setValue(self._getSystemUtil())
        # Set a new SensorData as the latestSensorData reference
        self.latestSensorData = sd
        # Return the latestSensorData
        return self.latestSensorData
        
    def getTelemetryValue(self) -> float:
        """
        A method to get a telemetry value
        
        @return float
        """
        # If latestSensorData == None, call generateTelemetry()
        if self.latestSensorData == None: self.generateTelemetry()
        # Get the telemetry value from latestSensorData
        val = self.latestSensorData.getValue()
        logging.info("{}: Telemetry value = {}".format(self.__class__.__name__, str(val)))
        return val
    
    def _getSystemUtil(self) -> float:
        """
        Template method implemented by sub-class.
        
        Retrieve the system utilization value as a float.
        
        @return float
        """
        pass
        