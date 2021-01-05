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

from programmingtheiot.data.SensorData import SensorData
import programmingtheiot.common.ConfigConst as ConfigConst

class BaseSensorSimTask():
	"""
	Shell representation of class for student implementation.
	
	"""
	
	DEFAULT_MIN_VALUE = 0.0
	DEFAULT_MAX_VALUE = 1000.0	

	def __init__(self, sensorName = ConfigConst.NOT_SET, sensorType: int = SensorData.DEFAULT_SENSOR_TYPE, dataSet = None, minVal: float = DEFAULT_MIN_VALUE, maxVal: float = DEFAULT_MAX_VALUE):
		self.sensorName = sensorName
		self.sensorType = sensorType
		self.dataSet = dataSet
		self.minVal = minVal
		self.maxVal = maxVal
		
		self.dataSetIndex = 0
		self.latestSensorData = None
		self.useRandomizer = False
		if dataSet == None: self.useRandomizer = True
		
	def generateTelemetry(self) -> SensorData:
		sd = SensorData(name = self.sensorName, sensorType=self.sensorType)

		if self.useRandomizer == True:
			sd.setValue(random.uniform(self.minVal, self.maxVal))
		else:
			sd.setValue(self.dataSet.dataEntries[self.dataSetIndex])
			self.dataSetIndex += 1
			if self.dataSetIndex > len(self.dataSet.dataEntries):
				self.dataSetIndex = 0
			
		self.latestSensorData = sd
		return self.latestSensorData
		
	
	def getTelemetryValue(self) -> float:
		if self.latestSensorData: 
			return self.latestSensorData.getValue()
		else:
			self.latestSensorData = self.generateTelemetry()
			return self.latestSensorData.getValue()
		