#####
# 
# This class is part of the Programming the Internet of Things
# project, and is available via the MIT License, which can be
# found in the LICENSE file at the top level of this repository.
# 
# Copyright (c) 2020 by Andrew D. King
# 

import logging
import unittest

from time import sleep

import programmingtheiot.common.ConfigConst as ConfigConst

from programmingtheiot.cda.connection.MqttClientConnector import MqttClientConnector
from programmingtheiot.common.ConfigUtil import ConfigUtil
from programmingtheiot.common.ResourceNameEnum import ResourceNameEnum
from programmingtheiot.common.DefaultDataMessageListener import DefaultDataMessageListener
from programmingtheiot.data.DataUtil import DataUtil
from programmingtheiot.data.ActuatorData import ActuatorData
from programmingtheiot.cda.system.SystemCpuUtilTask import SystemCpuUtilTask
from programmingtheiot.cda.system.SystemMemUtilTask import SystemMemUtilTask
from programmingtheiot.data.SystemPerformanceData import SystemPerformanceData
from programmingtheiot.data.SensorData import SensorData

class MqttClientConnectorTest2(unittest.TestCase):
	"""
	This test case class contains very basic unit tests for
	MqttClientConnector. It should not be considered complete,
	but serve as a starting point for the student implementing
	additional functionality within their Programming the IoT
	environment.
	"""
	
	@classmethod
	def setUpClass(self):
		logging.basicConfig(format = '%(asctime)s:%(module)s:%(levelname)s:%(message)s', level = logging.DEBUG)
		logging.info("Testing MqttClientConnector class...")
		
		self.cfg = ConfigUtil()
		self.mcc = MqttClientConnector(clientID = 'CDAMqttClientConnectorTest001')
		self.dataUtil = DataUtil(False)
		
	def setUp(self):
		pass

	def tearDown(self):
		pass


	#@unittest.skip("Ignore for now.")
	def testIntegrateWithGdaPublishCdaSensorTopic(self):
		"""
		TEST: Collect and send system performance data for CPU and memory to the GDA
		"""
		qos = 1
		delay = self.cfg.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE)
		
		self.mcc.connectClient()
		
		temp = SensorData(sensorType = SensorData.TEMP_SENSOR_TYPE)
		temp.setValue(25.0)
		temp.setName("Temperature")
		temp.setStatusCode(0)
		temp = self.dataUtil.sensorDataToJson(temp)
		
		self.mcc.publishMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, temp, qos)
		
		sleep(5 + delay)
		
		self.mcc.disconnectClient()
		

	@unittest.skip("Ignore for now.")
	def testIntegrateWithGdaPublishCdaSysPerfTopic(self):
		"""
		TEST: Collect and send system performance data for CPU and memory to the GDA
		"""
		qos = 1
		delay = self.cfg.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE)
		
		self.mcc.connectClient()
		
		cpuUtilTask = SystemCpuUtilTask()
		memUtilTask = SystemMemUtilTask()
		cpuUtilPct = cpuUtilTask.getTelemetryValue()
		memUtilPct = memUtilTask.getTelemetryValue()
		testData = SystemPerformanceData();
		testData.setCpuUtilization(cpuUtilPct);
		testData.setMemoryUtilization(memUtilPct);
		testData = self.dataUtil.systemPerformanceDataToJson(testData)
		
		self.mcc.publishMessage(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, testData, qos)
		
		sleep(5 + delay)
		
		self.mcc.disconnectClient()

	@unittest.skip("Ignore for now.")
	def testIntegrateWithGda(self):
		"""
		TEST: Connect to the GDA using at least 1 protocol , which is MQTT in this case
		"""
		qos = 1
		delay = self.cfg.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE)
		listener = DefaultDataMessageListener()
		
		self.mcc.connectClient()
		self.mcc.publishMessage(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, "TEST: This is the CDA message payload.", qos)
		
		sleep(5 + delay)
		
		self.mcc.disconnectClient()
		
		


if __name__ == "__main__":
	unittest.main()
	