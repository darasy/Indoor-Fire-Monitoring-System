#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

import logging
import psutil

from programmingtheiot.cda.system.BaseSystemUtilTask import BaseSystemUtilTask
import programmingtheiot.common.ConfigConst as ConfigConst

class SystemCpuUtilTask(BaseSystemUtilTask):
	"""
	Shell representation of class for System CPU utility.
	
	"""

	def __init__(self):
		"""
		Constructor
		"""
		super(SystemCpuUtilTask, self).__init__(sensorName = ConfigConst.CPU_UTIL_NAME)
	
	def _getSystemUtil(self) -> float:
		"""
		Get the used system cpu percentage
		
		@return float
		"""
		return psutil.cpu_percent()
	