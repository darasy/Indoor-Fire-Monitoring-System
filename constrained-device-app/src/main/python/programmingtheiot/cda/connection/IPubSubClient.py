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

import string

from programmingtheiot.common.ResourceNameEnum import ResourceNameEnum
from programmingtheiot.common.IDataMessageListener import IDataMessageListener

class IPubSubClient():
	"""
	Interface definition for pub/sub clients.
	
	"""
	DEFAULT_QOS = 0
	
	def connectClient(self) -> bool:
		"""
		Connects to the pub/sub broker / server using configuration parameters
		specified by the sub-class.
		
		@return bool True on success; False otherwise.
		"""
		pass

	def disconnectClient(self) -> bool:
		"""
		Disconnects from the pub/sub broker / server if the client is already connected.
		If not, this call is ignored, but will return a False.
		
		@return bool True on success; False otherwise.
		"""
		pass

	def publishMessage(self, resource: ResourceNameEnum, msg: string, qos: int = DEFAULT_QOS) -> bool:
		"""
		Attempts to publish a message to the given topic with the given qos
		to the pub/sub broker / server. If not already connected, the sub-class
		implementation should either throw an exception, or handle the exception
		and log a message, and return False.
		
		@param resource The topic Enum containing the topic value to publish the message to.
		@param msg The message to publish. This is expected to be well-formed JSON.
		@param qos The QoS level. This is expected to be 0 - 2.
		@return bool True on success; False otherwise.
		"""
		pass

	def subscribeToTopic(self, resource: ResourceNameEnum, qos: int = DEFAULT_QOS) -> bool:
		"""
		Attempts to subscribe to a topic with the given qos hosted by the
		pub/sub broker / server. If not already connected, the sub-class
		implementation should either throw an exception, or handle the exception
		and log a message, and return False.
		
		@param resource The topic Enum containing the topic value to subscribe to.
		@param qos The QoS level. This is expected to be 0 - 2.
		@return bool True on success; False otherwise.
		"""
		pass

	def unsubscribeFromTopic(self, resource: ResourceNameEnum) -> bool:
		"""
		Attempts to unsubscribe from a topic hosted by the pub/sub broker / server.
		If not already connected, the sub-class implementation should either
		throw an exception, or handle the exception and log a message, and return False.
		
		@param resource The topic Enum containing the topic value to unsubscribe from.
		@return bool True on success; False otherwise.
		"""
		pass

	def setDataMessageListener(self, listener: IDataMessageListener) -> bool:
		"""
		Sets the data message listener reference, assuming listener is non-null.
		
		@param listener The data message listener instance to use for passing relevant
		messages, such as those received from a subscription event.
		@return bool True on success (if listener is non-null will always be the case); False otherwise.
		"""
		pass
	