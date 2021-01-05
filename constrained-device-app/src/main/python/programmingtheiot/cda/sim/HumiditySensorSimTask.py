#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

import logging, random

from programmingtheiot.cda.sim.BaseSensorSimTask import BaseSensorSimTask
from programmingtheiot.cda.sim.SensorDataGenerator import SensorDataGenerator
import programmingtheiot.common.ConfigConst as ConfigConst
from programmingtheiot.data.SensorData import SensorData

class HumiditySensorSimTask(BaseSensorSimTask):
	"""
	Shell representation of class for student implementation.
	
	"""

	def __init__(self, dataSet = None):
		super(HumiditySensorSimTask, self).__init__(sensorName = ConfigConst.HUMIDITY_SENSOR_NAME, sensorType=SensorData.HUMIDITY_SENSOR_TYPE, dataSet = dataSet, minVal = SensorDataGenerator.LOW_NORMAL_ENV_HUMIDITY, maxVal = SensorDataGenerator.HI_NORMAL_ENV_HUMIDITY)
	
	def generateTelemetry(self) -> SensorData:
		sd = SensorData(name = ConfigConst.HUMIDITY_SENSOR_NAME, sensorType = self.sensorType)
	
		if self.useRandomizer == True:
			sd.setValue(random.uniform(self.minVal, self.maxVal))
		else:
			sd.setValue(self.dataSet.dataEntries[self.dataSetIndex])
			self.dataSetIndex += 1
			if self.dataSetIndex > len(self.dataSet.dataEntries):
				self.dataSetIndex = 0
			
		self.latestSensorData = sd
		return self.latestSensorData