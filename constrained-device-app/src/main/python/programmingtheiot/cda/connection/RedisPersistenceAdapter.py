#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

import logging

from programmingtheiot.common import ConfigUtil
from programmingtheiot.common import ConfigConst
from programmingtheiot.data import SensorData
from programmingtheiot.common.ResourceNameEnum import ResourceNameEnum

import redis

class RedisPersistenceAdapter():
	"""
	Shell representation of class for student implementation.
	
	"""

	def __init__(self):
		self.configUtil = ConfigUtil()
		self.host = self.configUtil.getProperty(ConfigConst.DATA_GATEWAY_SERVICE, ConfigConst.HOST_KEY)
		self.port = int(self.configUtil.getProperty(ConfigConst.DATA_GATEWAY_SERVICE, ConfigConst.PORT_KEY))
		self.redisClient = redis.Redis(host=self.host, port=self.port)

	def connectClient(self) -> bool:
		'''
		first check if the client is already connected - 
		if so, log a warning message indicating it's already connected and return true. 
		If not, connect to the Redis DB using the Redis client, and return the boolean indicating if the connection was successful. 
		Log a message with the appropriate status - info on success; warning or error on failure.
		'''
		if self.redisClient.connection:
			logging.warn("Redis client is already connected")
			return True
		else:
			try:
				self.redisClient.ping()
				logging.info("Redis client is connected successfully")
				return True
			except redis.ConnectionError:
				logging.error("Redis client fail to connect")
				return False
		
	def disconnectClient(self) -> bool:
		'''
		first check if the client is already disconnected - 
		if so, log a warning message indicating it's already disconnected and return true. 
		If not, disconnect from the Redis DB using the Redis client, and return the boolean indicating if the disconnect was successful. 
		Log a message with the appropriate status - info on success; warning or error on failure.
		'''

		if self.redisClient.connection == None:
			logging.warn("Redis client is already disconnected")
			return True
		else:
			try:
				self.redisClient.close()
				logging.info("Redis client is disconnected successfully")
				return True
			except redis.ConnectionError:
				logging.error("Redis client fail to disconnect")
				return False
		
	def storeData(self, resource: ResourceNameEnum, data: SensorData) -> bool:
		try:
			self.redisClient.set(name = resource.name(), data)
			return True
		except Exception:
			return False


