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

class DeviceDataManagerNoCommsTest(unittest.TestCase):
	"""
	This test case class contains very basic integration tests for
	DeviceDataManager. It should not be considered complete,
	but serve as a starting point for the student implementing
	additional functionality within their Programming the IoT
	environment.
	
	NOTE: This test MAY require the sense_emu_gui to be running,
	depending on whether or not the 'enableEmulator' flag is
	True within the ConstraineDevice section of PiotConfig.props.
	If so, it must have access to the underlying libraries that
	support the pisense module. On Windows, one way to do
	this is by installing pisense and sense-emu within the
	Bash on Ubuntu on Windows environment and then execute this
	test case from the command line, as it will likely fail
	if run within an IDE in native Windows.
	
	"""
	
	@classmethod
	def setUpClass(self):
		logging.basicConfig(format = '%(asctime)s:%(module)s:%(levelname)s:%(message)s', level = logging.DEBUG)
		logging.info("Testing DeviceDataManager class...")
		
	def setUp(self):
		pass

	def tearDown(self):
		pass

	def testStartAndStopManagerNoComms(self):
		ddMgr = DeviceDataManager(enableMqtt = False, enableCoap = False)
		ddMgr.startManager()
		
		sleep(30)
		
		ddMgr.stopManager()

if __name__ == "__main__":
	unittest.main()
	