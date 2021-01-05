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

from programmingtheiot.cda.app.DeviceDataManager import DeviceDataManager

class DeviceDataManagerIntegrationTest(unittest.TestCase):
	
	@classmethod
	def setUpClass(self):
		logging.basicConfig(format = '%(asctime)s:%(module)s:%(levelname)s:%(message)s', level = logging.DEBUG)
		logging.info("Testing DeviceDataManagerIntegrationTest class...")
		
	def setUp(self):
		pass

	def tearDown(self):
		pass

	#@unittest.skip("Ignore for now.")
	def testDeviceDataManagerIntegration(self):
		"""
		TEST: 
		1) Connect to the GDA using at least 1 protocol (can be MQTT and / or CoAP)
		2) Collect and send system performance data for CPU and memory to the GDA
		3) Collect data from 3 or more sensors (simulated, emulated or real) and send to the GDA
		4) Use data from at least one sensor to trigger a local (internal) actuation event due to a configured threshold crossing
		5) Implement 1 or more actuators (emulated or real – NO simulation!!!)
		6) Process actuation events received from the GDA to trigger at least 1 actuation event
		7) Run for at least 1 hour without interruption, and collect / send at least 30 system performance samples, 
		30 sensor data samples, and trigger at least 2 actuator events internally.
		"""
		# TODO: set MQTT True 
		ddMgr = DeviceDataManager(enableMqtt = True, enableCoap=False)
		ddMgr.startManager()
		
		# About 2 hours to run the tests and manually adjust the emulator values
		sleep(7300)
		
		ddMgr.stopManager()
if __name__ == "__main__":
	unittest.main()
	