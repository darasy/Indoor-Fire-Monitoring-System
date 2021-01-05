#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

import logging

from apscheduler.schedulers.background import BackgroundScheduler

from programmingtheiot.common.IDataMessageListener import IDataMessageListener

from programmingtheiot.cda.system.SystemCpuUtilTask import SystemCpuUtilTask
from programmingtheiot.cda.system.SystemMemUtilTask import SystemMemUtilTask
from programmingtheiot.data.SystemPerformanceData import SystemPerformanceData

class SystemPerformanceManager(object):
	"""
	Shell representation of class for System Performance Manager.
	
	"""

	def __init__(self, pollRate: int = 30):
		"""
		Constructor
		
		@param pollRate: a poll rate for the scheduler to call handleTelemetry()
		"""
		self.cpuUtilTask = SystemCpuUtilTask()
		self.memUtilTask = SystemMemUtilTask()
		self.dataMsgListener = IDataMessageListener()
		self.scheduler = BackgroundScheduler()
		self.scheduler.add_job(self.handleTelemetry, 'interval', seconds = pollRate)

	def handleTelemetry(self):
		"""
		A method to handlle system performance data including CPU and memory utilities
		"""
		self.cpuUtilPct = self.cpuUtilTask.getTelemetryValue()
		self.memUtilPct = self.memUtilTask.getTelemetryValue()
		logging.info('CPU utilization is %s percent, and memory utilizatio')
		data = SystemPerformanceData();
		data.setCpuUtilization(self.cpuUtilPct);
		data.setMemoryUtilization(self.memUtilPct);
		self.dataMsgListener.handleSystemPerformanceMessage(data);
		
	def setDataMessageListener(self, listener: IDataMessageListener) -> bool:
		"""
		Assign a new data listener
		
		@return Boolean 
		"""
		if listener:
			self.dataMsgListener = listener
			return True
		return False
	
	def startManager(self):
		"""
		Start the SystemPerformanceManager
		"""
		logging.info("Started SystemPerformanceManager.")
		self.scheduler.start()
		
	def stopManager(self):
		"""
		Stop the SystemPerformanceManager
		"""
		logging.info("Stopped SystemPerformanceManager.")
		self.scheduler.shutdown()