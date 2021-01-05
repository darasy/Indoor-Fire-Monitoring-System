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

	#@unittest.skip("Ignore for now.")
	def testConnectAndPublishQoS1(self):
		qos = 1
		delay = self.cfg.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE)
		listener = DefaultDataMessageListener()
		
		self.mcc.connectClient()
		
		self.mcc.subscribeToTopic(ResourceNameEnum.CDA_MGMT_STATUS_MSG_RESOURCE, qos)
		sleep(5)
		
		self.mcc.publishMessage(ResourceNameEnum.CDA_MGMT_STATUS_MSG_RESOURCE, "TEST: CSYE6530.", qos)
		sleep(5)
		
		self.mcc.unsubscribeFromTopic(ResourceNameEnum.CDA_MGMT_STATUS_MSG_RESOURCE)
		sleep(5)
		sleep(delay)
		
		self.mcc.disconnectClient()


if __name__ == "__main__":
	unittest.main()
	