#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

import logging
import random

from programmingtheiot.data.ActuatorData import ActuatorData
from numpy.distutils.tests.test_npy_pkg_config import simple
import programmingtheiot.common.ConfigConst as ConfigConst

class BaseActuatorSimTask():
    """
    Shell representation of class for student implementation.
    
    """
    
    def __init__(self, actuatorName = ConfigConst.NOT_SET,  actuatorType: int = ActuatorData.DEFAULT_ACTUATOR_TYPE, simpleName: str = "Actuator", dataSet = None):
        self.name = actuatorName
        self.actuatorType = actuatorType
        self.simpleName = simpleName
        self.latestActuatorData = ActuatorData() 

    def activateActuator(self, val: float) -> bool:
        try:
            actuatorT = "Actuator"
            if self.actuatorType == 1: 
                actuatorT = "HVAC"
            if self.actuatorType == 2: 
                actuatorT = "HUMIDIFIER"
                
            logging.info("The actuator was sent an 'ON' command with the value = " + str(val) + "." + "\n" 
                        + "*******\n" + "* ON *\n" + "*******\n" 
                        + actuatorT + " VALUE -> " + str(val) + "\n=======")
            
            self.latestActuatorData.setCommand(ActuatorData.COMMAND_ON) 
            return True
        
        except:
            return False
        
    def deactivateActuator(self) -> bool:
        try:
            logging.info("The actuator was sent an 'OFF' command.\n" + "*******\n" + "* OFF *\n" + "*******")
            self.latestActuatorData.setCommand(ActuatorData.COMMAND_OFF)
            return True
        except:
            return False
        
    def getLatestActuatorResponse(self) -> ActuatorData:
        return self.latestActuatorData
    
    def getSimpleName(self) -> str:
        return self.simpleName
    
    def updateActuator(self, data: ActuatorData) -> bool:
        if data:
            if data.hasError == False:
                if data.getCommand() == ActuatorData.COMMAND_ON: 
                    self.activateActuator(data.getValue())
                else: 
                    self.deactivateActuator()
                statusCode = self._handleActuation(data.getCommand(), data.getValue(), data.getStateData())
                if statusCode == -2: statusCode = data.getStatusCode()
                
                
                self.latestActuatorData.updateData(data)
                self.latestActuatorData.setStatusCode(statusCode)
                self.latestActuatorData.setStateData(data.getStateData())
                self.latestActuatorData.setAsResponse()
                return True
        return False
    
    def _handleActuation(self, cmd: int, val: float = 0.0, stateData: str = None) -> int:
        return -2
        