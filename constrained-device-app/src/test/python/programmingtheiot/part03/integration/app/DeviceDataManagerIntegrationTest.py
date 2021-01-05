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

	def testDeviceDataManagerIntegration(self):
		# TODO: set either MQTT or CoAP to True - you'll only need one.
		ddMgr = DeviceDataManager(enableMqtt = True, enableCoap = False)
		ddMgr.startManager()
		
		# 5 min's should be long enough to run the tests and manually adjust the emulator values
		sleep(300)
		
		ddMgr.stopManager()
if __name__ == "__main__":
	unittest.main()
	