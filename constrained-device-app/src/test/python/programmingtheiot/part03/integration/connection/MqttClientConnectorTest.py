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

class MqttClientConnectorTest(unittest.TestCase):
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
		
	def setUp(self):
		pass

	def tearDown(self):
		pass

	@unittest.skip("Ignore for now.")
	def testConnectAndDisconnect(self):
		delay = self.cfg.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE)
		
		self.mcc.connectClient()
		
		sleep(delay + 5)
		
		self.mcc.disconnectClient()

	@unittest.skip("Ignore for now.")
	def testConnectAndPublish(self):
		qos = 1
		delay = self.cfg.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE)
		listener = DefaultDataMessageListener()
		
		self.mcc.connectClient()
		self.mcc.subscribeToTopic(ResourceNameEnum.CDA_MGMT_STATUS_MSG_RESOURCE, qos)
		sleep(5)
		
		self.mcc.publishMessage(ResourceNameEnum.CDA_MGMT_STATUS_MSG_RESOURCE, "TEST: This is the CDA message payload.", qos)
		sleep(5)
		
		self.mcc.unsubscribeFromTopic(ResourceNameEnum.CDA_MGMT_STATUS_MSG_RESOURCE)
		sleep(5)
		
		sleep(delay)
		
		self.mcc.disconnectClient()

	@unittest.skip("Ignore for now.")
	def testIntegrateWithGdaSubscribeCdaCmdTopic(self):
		qos = 1
		delay = self.cfg.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE)
		listener = DefaultDataMessageListener()
		
		self.mcc.connectClient()
		self.mcc.subscribeToTopic(ResourceNameEnum.CDA_MGMT_STATUS_CMD_RESOURCE, qos)
		
		sleep(delay)
		
		self.mcc.disconnectClient()

	@unittest.skip("Ignore for now.")
	def testIntegrateWithGdaPublishCdaMgmtTopic(self):
		qos = 1
		delay = self.cfg.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE)
		listener = DefaultDataMessageListener()
		
		self.mcc.connectClient()
		self.mcc.publishMessage(ResourceNameEnum.CDA_MGMT_STATUS_MSG_RESOURCE, "TEST: This is the CDA message payload.", qos)
		
		sleep(5)
		
		self.mcc.disconnectClient()
		
		
	def testActuatorCmdPubSub(self):
		qos = 1

		# NOTE: delay can be anything you'd like - the sleep() calls are simply to slow things down a bit for observation
		delay = self.cfg.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE)
		
		actuatorData = ActuatorData()
		payload = DataUtil().actuatorDataToJson(actuatorData)
		
		self.mcc.connectClient()
		
		sleep(5)
		
		self.mcc.publishMessage(resource = ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, msg = payload, qos = qos)
		
		sleep(delay)
		
		self.mcc.disconnectClient()


if __name__ == "__main__":
	unittest.main()
	