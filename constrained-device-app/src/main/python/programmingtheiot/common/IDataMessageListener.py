#####
# 
# This class is part of the Programming the Internet of Things
# project, and is available via the MIT License, which can be
# found in the LICENSE file at the top level of this repository.
# 
# You may find it more helpful to your design to adjust the
# functionality, constants and interfaces (if there are any)
# provided within in order to meet the needs of your specific
# Programming the Internet of Things project.
# 

from programmingtheiot.common.ResourceNameEnum import ResourceNameEnum

from programmingtheiot.data.ActuatorData import ActuatorData
from programmingtheiot.data.SensorData import SensorData
from programmingtheiot.data.SystemPerformanceData import SystemPerformanceData

class IDataMessageListener():
	"""
	Interface definition for data message listener clients.
	
	"""
	
	def handleActuatorCommandResponse(self, data: ActuatorData) -> bool:
		"""
		Callback function to handle an actuator command response packaged as a ActuatorData object.
		
		@param data The ActuatorData message received.
		@return bool True on success; False otherwise.
		"""
		pass
	
	def handleIncomingMessage(self, resourceEnum: ResourceNameEnum, msg: str) -> bool:
		"""
		Callback function to handle incoming messages on a given topic with
		a string-based payload.
		
		@param resourceEnum The topic enum associated with this message.
		@param msg The message received. It is expected to be in JSON format.
		@return bool True on success; False otherwise.
		"""
		pass

	def handleSensorMessage(self, data: SensorData) -> bool:
		"""
		Callback function to handle a sensor message packaged as a SensorData object.
		
		@param data The SensorData message received.
		@return bool True on success; False otherwise.
		"""
		pass
	
	def handleSystemPerformanceMessage(self, data: SystemPerformanceData) -> bool:
		"""
		Callback function to handle a system performance message packaged as
		SystemPerformanceData object.
		
		@param data The SystemPerformanceData message received.
		@return bool True on success; False otherwise.
		"""
		pass
	
	
	def handleActuatorCommandMessage(self, data: ActuatorData) -> bool:
		"""
		Callback function to handle an actuator command message packaged as a ActuatorData object.
		
		@param data The ActuatorData message received.
		@return bool True on success; False otherwise.
		"""
		pass