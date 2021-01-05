#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

from programmingtheiot.data.BaseIotData import BaseIotData
import programmingtheiot.common.ConfigConst as ConfigConst

class SystemPerformanceData(BaseIotData):
	"""
	Shell representation of class for student implementation.
	
	"""
	DEFAULT_VAL = 0.0
	DEFAULT_CPU_UTIL_DATA = 0.0
	DEFAULT_DISK_UTIL_DATA = 0.0
	DEFAULT_MEM_UTIL_DATA = 0.0
	
	def __init__(self, d = None):
		"""
		Constructor.
		
		@param d Defaults to None. The data (dict) to use for setting all parameters.
		It's provided here as a convenience - mostly for testing purposes. The utility
		in DataUtil should be used instead.
		"""
		super(SystemPerformanceData, self).__init__(name = ConfigConst.SYS_PERF_DATA, d = d)
		
		if d:
			self.cpuUtil = d['cpuUtil']
			self.diskUtil = d['diskUtil']
			self.memUtil = d['memUtil']
		else:
			self.cpuUtil = self.DEFAULT_VAL
			self.diskUtil = self.DEFAULT_VAL
			self.memUtil = self.DEFAULT_VAL
	
	"""
	Getter methods
	"""
	def getCpuUtilization(self):
		return self.cpuUtil
	
	def getDiskUtilization(self):
		return self.diskUtil
	
	def getMemoryUtilization(self):
		return self.memUtil
	
	"""
	Setter methods
	"""
	def setCpuUtilization(self, cpuUtil):
		self.cpuUtil = cpuUtil
	
	def setDiskUtilization(self, diskUtil):
		self.diskUtil = diskUtil
	
	def setMemoryUtilization(self, memUtil):
		self.memUtil = memUtil
	
	
	
	def _handleUpdateData(self, data):
		"""
		A method to update SystemPermanceData
		"""
		if data:
			self.setCpuUtilization(data.getCpuUtilization())
			self.setDiskUtilization(data.getDiskUtilization())
			self.setMemoryUtilization(data.getMemoryUtilization())
