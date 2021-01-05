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

from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.image import img_to_array
import numpy as np
import base64
import cv2
import time
import logging 

class CameraSensorEmulatorTask(BaseSensorSimTask):
    """
    Shell representation of class for camera sensor emulator.
    
    """

    def __init__(self, dataSet = None):
        """
            Constructor
        """
        # Derive from BaseSensorSimTask
        super(CameraSensorEmulatorTask, self).__init__(sensorName= ConfigConst.CAM_SENSOR_NAME, sensorType=SensorData.CAM_SENSOR_TYPE)
        # Create an instance of webcam image captured using OpenCV
        self.configUtil = ConfigUtil()
        # 0: is for built camera if available otherwise USB camera (can also be 1 and 2)
        self.imageFrame = cv2.VideoCapture(0)
        if self.imageFrame.isOpened(): # try to get the first frame
            _, frame = self.imageFrame.read()
        else:
            logging.warn("Fail to open the WebCam.")
            
        self.imageSize = 64
        # Deep Learning model to detect fire
        self.model = load_model('/home/darasy/git/constrained-device-app-rethd/src/main/python/programmingtheiot/cda/emulated/Fire-Model.h5')

    # A function to generate the telemetry
    def generateTelemetry(self) -> SensorData:
        """
            A method to generate a telemetry
            
            Return SensorData
        """
        # Create a Sensor Data with sensorType
        sensorData = SensorData(name = ConfigConst.CAM_SENSOR_NAME, sensorType = self.sensorType)
        
        # Set a value to the current capture image
        _, frame = self.imageFrame.read()
        
        # Encoding the Frame
        _, buffer = cv2.imencode('.jpg', frame)
        # Converting into encoded bytes
        jpg_as_text = base64.b64encode(buffer)
        jpg_as_text = jpg_as_text.decode('UTF-8')
        
        # image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        image = cv2.resize(frame, (self.imageSize, self.imageSize))  
        image = image.astype("float") / 255.0
        image = img_to_array(image)
        image = np.expand_dims(image, axis=0)
        # Detect fire with probability outcome
        fire_prob = self.model.predict(image)[0][0] * 100
        logging.info("Fire Detected with Probability : " + str(fire_prob))
        
        sensorVal = fire_prob
        sensorData.setValue(sensorVal)
        sensorData.setStateData(jpg_as_text)
        # Set the latestSensorData to the newly created SensorData
        self.latestSensorData = sensorData

        return sensorData
    
