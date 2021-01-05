#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

from datetime import datetime

import programmingtheiot.common.ConfigConst as ConfigConst

class BaseIotData(object):
	"""
	This is a simple wrapper for an Actuator abstraction - it provides
	a container for the actuator's state, value, name, and status. A
	command variable is also provided to instruct the actuator to
	perform a specific function (in addition to setting a new value
	via the 'val' parameter.
	
	"""
	DEFAULT_VAL = 0.0
	
	DEFAULT_STATUS = 0
	STATUS_IDLE = DEFAULT_STATUS
	STATUS_ACTIVE = 1
	
	
	def __init__(self, name = ConfigConst.NOT_SET, d = None):
		"""
		Constructor.
		
		@param d Defaults to None. The data (dict) to use for setting all parameters.
		It's provided here as a convenience - mostly for testing purposes. The utility
		in DataUtil should be used instead.
		"""
		if d:
			self.name = d['name']
			self.timeStamp = d['timeStamp']
			self.hasError = d['hasError']
			self.statusCode = d['statusCode']
		else:
			self.updateTimeStamp()
			self.name = name
			self.hasError = False
			self.statusCode = self.DEFAULT_STATUS
	
	
	def getName(self) -> str:
		"""
		Returns the name.
		
		@return The name as a string.
		"""
		return self.name
	
	def getStatusCode(self) -> int:
		"""
		Returns the status code value.
		
		@return The status code value as an integer.
		"""
		return self.statusCode
	
	def getTimeStamp(self) -> str:
		"""
		Returns the time stamp.
		
		@return The time stamp as a string.
		"""
		return self.timeStamp
	
	def hasErrorFlag(self):
		"""
		Returns the boolean flag indicating if an error is present.
		
		@return The boolean flag representing the error state.
		True if there's an error condition; false otherwise.
		"""
		return self.hasError
	
	def setName(self, name: str):
		"""
		Sets the name.
		
		@param The name as a string.
		"""
		self.name = name
		
	def setStatusCode(self, statusCode: int):
		"""
		Sets the status code value. If the status code is
		less than 0, the error flag will be set.
		
		@param statusCode The status code value as an integer.
		"""
		self.statusCode = statusCode
		
		if statusCode < 0:
			self.hasError = True
	
	def updateData(self, data):
		"""
		Sets the internal values of this object to be that of 'data',
		which is assumed to be an BaseIotData instance.
		
		NOTE: The time stamp will NOT be affected by this action.
		
		@param data The BaseIotData data to apply to this instance.
		"""
		self.name = data.getName()
		self.hasError = data.hasErrorFlag()
		self.statusCode = data.getStatusCode()
		
		self._handleUpdateData(data)
		
	def updateTimeStamp(self):
		"""
		Updates the internal time stamp to the current date / time.
		
		"""
		self.timeStamp = str(datetime.now())
	
	def __str__(self):
		"""
		Returns a string representation of this instance.
		
		@return The string representing this instance.
		"""
		customStr = \
			str('name='	+ self.name + \
			',timeStamp=' + str(self.timeStamp) + \
			',hasError=' + str(self.hasError) + \
			',statusCode=' + str(self.statusCode))
					
		return customStr
	
	def _handleUpdateData(self, data):
		"""
		Template method definition to update sub-class data.
		
		@param data The BaseIotData data to apply to this instance.
		"""
		pass
