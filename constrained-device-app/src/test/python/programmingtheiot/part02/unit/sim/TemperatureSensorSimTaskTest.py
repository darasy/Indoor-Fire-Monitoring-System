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

from programmingtheiot.cda.sim.TemperatureSensorSimTask import TemperatureSensorSimTask

class TemperatureSensorSimTaskTest(unittest.TestCase):
	"""
	This test case class contains very basic unit tests for
	TemperatureSensorSimTask. It should not be considered complete,
	but serve as a starting point for the student implementing
	additional functionality within their Programming the IoT
	environment.
	"""
	
	@classmethod
	def setUpClass(self):
		logging.basicConfig(format = '%(asctime)s:%(module)s:%(levelname)s:%(message)s', level = logging.DEBUG)
		logging.info("Testing TemperatureSensorSimTask class...")
		self.tSimTask = TemperatureSensorSimTask()
		
	def setUp(self):
		pass

	def tearDown(self):
		pass

	def testGenerateTelemetry(self):
		sd = self.tSimTask.generateTelemetry()
		
		self.assertIsNotNone(sd)
		
		# default simulator generates temp values > 0.0C
		self.assertGreaterEqual(sd.getValue(), 0.0)
		logging.info("Temperature SensorData: %s", str(sd))
			
	#@unittest.skip("Ignore for now.")
	def testGetTelemetryValue(self):
		val = self.tSimTask.getTelemetryValue()
		
		self.assertGreater(val, 0.0)
		logging.info("Temperature data: %f", val)

if __name__ == "__main__":
	unittest.main()
	