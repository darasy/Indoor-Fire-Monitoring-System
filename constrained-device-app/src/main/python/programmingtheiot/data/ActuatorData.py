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

class ActuatorData(BaseIotData):
	"""
	Shell representation of class for actuator data class.
	
	"""
	DEFAULT_COMMAND = 0
	COMMAND_OFF = DEFAULT_COMMAND
	COMMAND_ON = 1

	# for now, actuators will be 1..99
	# and displays will be 100..1999
	DEFAULT_ACTUATOR_TYPE = 0
	
	HVAC_ACTUATOR_TYPE = 1
	HUMIDIFIER_ACTUATOR_TYPE = 2
	LED_DISPLAY_ACTUATOR_TYPE = 100
	ALARM_ACTUATOR_TYPE = 3
	
	def __init__(self, actuatorType: int = DEFAULT_ACTUATOR_TYPE, name = ConfigConst.NOT_SET, d = None):
		"""
		Constructor.
		
		@param d Defaults to None. The data (dict) to use for setting all parameters.
		It's provided here as a convenience - mostly for testing purposes. The utility
		in DataUtil should be used instead.
		@param name: default to Not Set
		"""
		super(ActuatorData, self).__init__(name = name, d = d)
		
		self.isResponse = False
		self.actuatorType = actuatorType
		
		if d:
			self.command = d['command']
			self.stateData = d['stateData']
			self.curValue = d['curValue']
			self.actuatorType = d['actuatorType']
		else:
			self.command = self.DEFAULT_COMMAND
			self.stateData = None
			self.curValue = self.DEFAULT_VAL
			self.actuatorType = actuatorType
	
	"""
	Getter methods
	"""
	def getCommand(self) -> int:
		return self.command
	
	def getStateData(self) -> str:
		return self.stateData
	
	def getValue(self) -> float:
		return self.curValue
	
	def isResponseFlagEnabled(self) -> bool:
		return self.isResponse
	
	"""
	Setter methods
	"""
	def setCommand(self, command: int):
		self.command = command
	
	def setAsResponse(self):
		self.isResponse = True
		
	def setStateData(self, stateData: str):
		self.stateData = stateData
	
	def setValue(self, val: float):
		self.curValue = val
		
	def _handleUpdateData(self, data):
		if data:
			self.setValue(data.getValue())
			self.setStateData(data.getStateData())
			self.setCommand(data.getCommand())
	
	
	def __str__(self):
		"""
		Returns a string representation of this instance.
		
		@return The string representing this instance.
		"""
		customStr = \
			str('name='	+ self.name + \
			',timeStamp=' + str(self.timeStamp) + \
			',command=' + str(self.command) + \
			',hasError=' + str(self.hasError) + \
			',statusCode=' + str(self.statusCode) + \
			',stateData=' + str(self.stateData) + \
			',curValue=' + str(self.curValue) + \
			',actuatorType=' + str(self.actuatorType))

		return customStr
		