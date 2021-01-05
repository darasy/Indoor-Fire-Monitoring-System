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

class SystemMemUtilTask(BaseSystemUtilTask):
	"""
	Shell representation of class for System Memory Utility.
	
	"""

	def __init__(self):
		"""
		Constructor
		"""
		super(SystemMemUtilTask, self).__init__(sensorName = ConfigConst.MEM_UTIL_NAME)
	
	def _getSystemUtil(self) -> float:
		"""
		Get the used system memory percentage
		
		@return float
		"""
		return psutil.virtual_memory().percent
		