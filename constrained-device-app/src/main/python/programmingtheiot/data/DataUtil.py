#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

import json, logging
from json import JSONEncoder

from programmingtheiot.data.ActuatorData import ActuatorData
from programmingtheiot.data.SensorData import SensorData
from programmingtheiot.data.SystemPerformanceData import SystemPerformanceData

class DataUtil():
    """
    Shell representation of class for Data Utiility task.
    
    """

    def __init__(self, encodeToUtf8 = False):
        """
        Constructor
        """
        pass
    
    def actuatorDataToJson(self, actuatorData):
        """
        Convert ActuatorData to json
        
        @return String
        """
        # Use the built-in json library and the JsonDataEncoder class
        if actuatorData.actuatorType == ActuatorData.ALARM_ACTUATOR_TYPE:
            logging.debug("Encoding ActuatorData to JSON [pre]")
        else: logging.debug("Encoding ActuatorData to JSON [pre]  --> " + str(actuatorData))
        
        jsonData = json.dumps(actuatorData, indent = 4, cls = JsonDataEncoder, ensure_ascii = True)
        if actuatorData.actuatorType == ActuatorData.ALARM_ACTUATOR_TYPE:
            logging.debug("Encoding ActuatorData to JSON [post]")
        else: logging.debug("Encoding ActuatorData to JSON [post]  --> " + jsonData)
        return jsonData
    
    def sensorDataToJson(self, sensorData):
        """
        Convert SensorData to json
        
        @return String
        """
        # Use the built-in json library and the JsonDataEncoder class
        if sensorData.getSensorType() != SensorData.CAM_SENSOR_TYPE:
            logging.debug("Encoding SensorData to JSON [pre]  --> " + str(sensorData))
        else: logging.debug("Encoding SensorData to JSON [pre]")
        
        jsonData = json.dumps(sensorData, indent = 4, cls = JsonDataEncoder, ensure_ascii = True)
        
        if sensorData.getSensorType() != SensorData.CAM_SENSOR_TYPE:
            logging.debug("Encoding SensorData to JSON [post]  --> " + jsonData)
        else: logging.debug("Encoding SensorData to JSON [post]")
        
        return jsonData

    def systemPerformanceDataToJson(self, sysPerfData):
        """
        Convert SystemPerformanceData to json
        
        @return String
        """
        # Use the built-in json library and the JsonDataEncoder class
        logging.debug("Encoding SysetmPerformanceData to JSON [pre]  --> " + str(sysPerfData))
        jsonData = json.dumps(sysPerfData, indent = 4, cls = JsonDataEncoder, ensure_ascii = True)
        logging.debug("Encoding SysetmPerformanceData to JSON [post]  --> " + jsonData)
        return jsonData
    
    def jsonToActuatorData(self, jsonData):
        """
        Convert json to ActuatorData
        
        @return ActuatorData
        """
        # Replace double quotes with single quotes, and booleans with upper case to lower case.
        jsonData = jsonData.decode('utf8').replace("\'", "\"").replace('False','false').replace('True', 'true')
        # Load the dictionary data for the JSON string
        adDict = json.loads(jsonData)
        # Create an instance of ActuatorData, extract the variables,
        # then map the JSON dict into the new object via an iterative lookup of each key / value pair.
        ad = ActuatorData()
        mvDict = vars(ad)
        for key in adDict:
            if key in mvDict:
                setattr(ad, key, adDict[key])
        return ad
    
    def jsonToSensorData(self, jsonData):
        """
        Convert json to SensorData
        
        @return SensorData
        """
        # Replace double quotes with single quotes, and booleans with upper case to lower case.
        jsonData = jsonData.decode('utf8').replace("\'", "\"").replace('False','false').replace('True', 'true')
        # Load the dictionary data for the JSON string
        sdDict = json.loads(jsonData)
        # Create an instance of SensorData, extract the variables,
        # then map the JSON dict into the new object via an iterative lookup of each key / value pair.
        sd = SensorData()
        mvDict = vars(sd)
        for key in sdDict:
            if key in mvDict:
                setattr(sd, key, sdDict[key])
        return sd
    
    def jsonToSystemPerformanceData(self, jsonData):
        """
        Convert json to SystemPerformanceData
        
        @return SystemPerformanceData
        """
        # Replace double quotes with single quotes, and booleans with upper case to lower case.
        jsonData = jsonData.decode('utf8').replace("\'", "\"").replace('False','false').replace('True', 'true')
        # Load the dictionary data for the JSON string
        spdDict = json.loads(jsonData)
        # Create an instance of SystemPerformanceData, extract the variables,
        # then map the JSON dict into the new object via an iterative lookup of each key / value pair.
        spd = SystemPerformanceData()
        mvDict = vars(spd)
        for key in spdDict:
            if key in mvDict:
                setattr(spd, key, spdDict[key])
        return spd
    
class JsonDataEncoder(JSONEncoder):
    """
    Convenience class to facilitate JSON encoding of an object that
    can be converted to a dict.
    
    """
    def default(self, o):
        return o.__dict__
    