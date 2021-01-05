#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

import logging, json, random, base64, time

from programmingtheiot.cda.connection.CoapClientConnector import CoapClientConnector
from programmingtheiot.cda.connection.MqttClientConnector import MqttClientConnector

from programmingtheiot.cda.system.ActuatorAdapterManager import ActuatorAdapterManager
from programmingtheiot.cda.system.SensorAdapterManager import SensorAdapterManager
from programmingtheiot.cda.system.SystemPerformanceManager import SystemPerformanceManager

import programmingtheiot.common.ConfigConst as ConfigConst

from programmingtheiot.common.ConfigUtil import ConfigUtil
from programmingtheiot.common.IDataMessageListener import IDataMessageListener
from programmingtheiot.common.ResourceNameEnum import ResourceNameEnum

from programmingtheiot.data.DataUtil import DataUtil
from programmingtheiot.data.ActuatorData import ActuatorData
from programmingtheiot.data.SensorData import SensorData
from programmingtheiot.data.SystemPerformanceData import SystemPerformanceData


class DeviceDataManager(IDataMessageListener):
	"""
	Shell representation of class for device data manager.
	
	"""
	
	def __init__(self, enableMqtt: bool = True, enableCoap: bool = False):
		"""
		Constructor
		"""
		self.enableMqtt = enableMqtt
		self.enableCoap = enableCoap
		
		# Check whether MQTT is used
		self.enableMqttClient = enableMqtt
		self.mqttClient = None
		
		self.enableCoapClient = enableCoap
		self.coapClient = None
		
		if self.enableMqttClient:
			self.mqttClient = MqttClientConnector()
		
		self.configUtil = ConfigUtil()
		self.enableEmulator = self.configUtil._getConfig()['ConstrainedDevice']['enableEmulator']
		
		self.sysPerfManager = SystemPerformanceManager()
		self.sysPerfManager.setDataMessageListener(self)
		
		self.sensorAdapterManager = SensorAdapterManager(useEmulator=self.enableEmulator)
		self.sensorAdapterManager.setDataMessageListener(self)
		self.fireProbabilityHistory = [0] * 5
		self.fireProbabilityHistoryTimestamp = [0, 0]
		self.fireProbabilityHistoryIndex = 0
		self.fireProbabilityHistoryTimestampIndex = 0
		
		self.actuatorAdapterManager = ActuatorAdapterManager(useEmulator=self.enableEmulator)
		self.actuatorAdapterManager.setDataMessageListener(self)
		
		self.enableHandleTempChangeOnDevice = self.configUtil.getBoolean(ConfigConst.CONSTRAINED_DEVICE, ConfigConst.ENABLE_HANDLE_TEMP_CHANGE_ON_DEVICE_KEY)
		self.triggerHvacTempFloor = self.configUtil.getFloat(ConfigConst.CONSTRAINED_DEVICE, ConfigConst.TRIGGER_HVAC_TEMP_FLOOR_KEY);		
		self.triggerHvacTempCeiling = self.configUtil.getFloat(ConfigConst.CONSTRAINED_DEVICE, ConfigConst.TRIGGER_HVAC_TEMP_CEILING_KEY);
		
		self.fireProbThreshold = self.configUtil.getFloat(ConfigConst.CONSTRAINED_DEVICE, ConfigConst.FIRE_PROBABILITY_THRESHOLD);
		
		encodeToUtf8 = False	
		self.dataUtil = DataUtil(encodeToUtf8)
	
	def handleActuatorCommandResponse(self, data: ActuatorData) -> bool:
		'''
		* Log a message indicating this method has been called.
		* Use the DataUtil class to convert the ActuatorData to JSON. 
		* Call self._handleUpstreamTransmission() with the ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE and json data as its parameters. 
			** This will ensure the response from the actuator command gets passed up the chain to the GDA.
			
		@return Boolean
		'''
		try:
			logging.info("Started handleActuatorCommandResponse.")
			'''
			TO DO
			Use the DataUtil class to convert the ActuatorData to JSON. 
			'''
			#logging.debug("Encoding ActuatorData to JSON [pre]  --> " + str(data))
			json = self.dataUtil.actuatorDataToJson(data)
			json_msg = json
			#logging.debug("Encoding ActuatorData to JSON [post]  --> " + json_msg)
			self._handleUpstreamTransmission(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, json_msg)
			return True
		except: 
			return False
	
	def handleIncomingMessage(self, resourceEnum: ResourceNameEnum, msg: str) -> bool:
		'''
		* Log a message indicating this method has been called.
		* Use the DataUtil class to convert the ActuatorData to JSON. 
		* Call self._handleIncomingDataAnalysis() with the message content (should be JSON when all the components and systems are connected together). 
		
		@return Boolean
		'''
		try:
			logging.info("Started handleIncomingMessage.")
			'''
			Use the DataUtil class to convert the ActuatorData to JSON. 
			'''
			topic = self.validateTopicName(resourceEnum)
			if topic == "CDA_ACTUATOR_CMD_RESOURCE":
				self._handleIncomingDataAnalysis(msg)
				return True
		except: 
			return False
		
		
	def handleSensorMessage(self, data: SensorData) -> bool:
		'''
		* Log a message indicating this method has been called.
		* Use the DataUtil class to convert the SensorData to JSON. 
		* Call self._handleUpstreamTransmission() with the ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE and json data as its parameters. 
			** This will ensure the sensor data gets passed up the chain to the GDA.
		* Call self._handleSensorDataAnalysis() and pass it the SensorData instance. 
		
		@return Boolean
		'''
		try:
			logging.info("Started handleSensorMessage.")
			'''
			TO DO
			Use the DataUtil class to convert the ActuatorData to JSON. 
			'''
			json = self.dataUtil.sensorDataToJson(data)
			json_msg = json
			self._handleUpstreamTransmission(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, json_msg)
			self._handleSensorDataAnalysis(data)

			return True

		except:
			return False
		
		
	def handleSystemPerformanceMessage(self, data: SystemPerformanceData) -> bool:
		'''
		* Log a message indicating this method has been called.
		* Use the DataUtil class to convert the SystemPerformanceData to JSON. 
		* Call self._handleUpstreamTransmission() with the ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE and json data as its parameters.
		
		@return Boolean
		'''
		try:
			logging.info("Started handleSystemPerformanceMessage.")
			'''
			TO DO
			Use the DataUtil class to convert the ActuatorData to JSON. 
			'''
			json = self.dataUtil.systemPerformanceDataToJson(data)
			json_msg = json
			self._handleUpstreamTransmission(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, json_msg)
			return True
		except:
			return False
	
	
	def startManager(self):
		'''
		* Log an info message indicating manager was started.
		* Start the SystemPerformanceManager.
		* Start the SensorAdapterManager.
		'''
		logging.info("Started DeviceDataManager.")
		self.sysPerfManager.startManager()
		self.sensorAdapterManager.startManager()
		if self.enableMqttClient:
			self.mqttClient.setDataMessageListener(self)
			self.mqttClient.connectClient()
		if self. enableCoap:
			self.coapClient.setDataMessageListener(self)
			self.coapClient = CoapClientConnector()
		
	def stopManager(self):
		'''
		* Log an info message indicating manager was stopped.
		* Stop the SystemPerformanceManager.
		* Stop the SensorAdapterManager
		'''
		if self.enableMqttClient:
			self.mqttClient.disconnectClient()
		self.sensorAdapterManager.stopManager()
		self.sysPerfManager.stopManager()
		logging.info("Stopped DeviceDataManager.")

		
	def _handleIncomingDataAnalysis(self, msg: str):
		"""
		Call this from handleIncomeMessage() to determine if there's
		any action to take on the message. Steps to take:
		1) Validate msg: Most will be ActuatorData, but you may pass other info as well.
		2) Convert msg: Use DataUtil to convert if appropriate.
		3) Act on msg: Determine what - if any - action is required, and execute.
		"""
		
		'''
		* Log a message indicating this method has been called. Debug is probably best.
		* The 'msg' will almost certainly be a JSON-formatted ActuatorData instance, but can't be completely sure. 
		* Assuming you can convert 'msg' to an ActuatorData instance, and once you're sure you want to take action on it, 
		call the actuator data manager's sendActuatorCommand() method, passing the ActuatorData as the parameter.
		'''
		logging.debug("Started _handleIncomingDataAnalysis.")

		try:
			# json can load the message successfully, this is a valid JSON
			json.loads(msg)
			# If it is, convert JSON to ActuatorData and send over
			actuatorData = self.dataUtil.jsonToActuatorData(msg)
			self.actuatorAdapterManager.sendActuatorCommand(actuatorData)
			
		except:
			logging.info("Not a valid JSON-formatted ActuatorData")
			
		
		
	def _handleSensorDataAnalysis(self, data: SensorData):
		"""
		Call this from handleSensorMessage() to determine if there's
		any action to take on the message. Steps to take:
		1) Check config: Is there a rule or flag that requires immediate processing of data?
		2) Act on data: If # 1 is true, determine what - if any - action is required, and execute.
		"""
		'''
		* Log a message indicating this method has been called. Debug is probably best.
		* Check if probability detected for the fire is over the threshold for the history period of fire detection. 
			** Send this on its way to the actuator adapter manager via a call to its sendActuatorCommand() method.
		'''
		logging.debug("Started _handleSensorDataAnalysis.")
		
		# Analyzing on whether to send an actuator command for the fire alarm (Handle locally within CDA)
		if data.getSensorType() == SensorData.CAM_SENSOR_TYPE:
			self.fireProbabilityHistory[self.fireProbabilityHistoryIndex] = data.getValue()
			self.fireProbabilityHistoryIndex += 1
			self.fireProbabilityHistoryIndex %= len(self.fireProbabilityHistory)
		
			# Compute the average of the probability over the detected history
			fireProb = sum(self.fireProbabilityHistory) / len(self.fireProbabilityHistory)
			if  fireProb >= self.fireProbThreshold:
				# record timestamp between old and recent fire detected timestamp
				self.fireProbabilityHistoryTimestamp[0] = self.fireProbabilityHistoryTimestamp[1]
				self.fireProbabilityHistoryTimestamp[1] = time.time()
				# Create a new actuator data to send over via ActuatorAdapterManager
				actuatorData = ActuatorData()
				actuatorData.setValue(fireProb)
				actuatorData.setName("FireAlarmActuator")
				actuatorData.actuatorType = ActuatorData.ALARM_ACTUATOR_TYPE
				actuatorData.setCommand(ActuatorData.COMMAND_ON)
				actuatorData.setStateData(data.getStateData())
				self.actuatorAdapterManager.sendActuatorCommand(actuatorData)

				
				
	def _handleUpstreamTransmission(self, resourceName: ResourceNameEnum, msg: str):
		"""
		Call this from handleActuatorCommandResponse(), handlesensorMessage(), and handleSystemPerformanceMessage()
		to determine if the message should be sent upstream. Steps to take:
		1) Check connection: Is there a client connection configured (and valid) to a remote MQTT or CoAP server?
		2) Act on msg: If # 1 is true, send message upstream using one (or both) client connections.
		"""
		logging.debug("Started _handleUpstreamTransmission.")
		topic = self.validateTopicName(resourceName)
		if topic == "CDA_SENSOR_MSG_RESOURCE" or topic == "CDA_SYSTEM_PERF_MSG_RESOURCE" or topic == "CDA_ACTUATOR_RESPONSE_RESOURCE":
			if topic == "CDA_ACTUATOR_RESPONSE_RESOURCE":
				logging.info("Incoming actuator response received (from actuator manager)")		
			if self.enableMqttClient:
				self.mqttClient.publishMessage(resourceName, msg)
			if self.enableCoapClient:
				self.coapClient.sendPostRequest(resourceName, msg)
		

	def validateTopicName(self, resource: ResourceNameEnum):
		"""
		A function to validate the topic name
		
		@return String
		"""
		if resource == ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE: return "CDA_SENSOR_MSG_RESOURCE"
		if resource == ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE: return "CDA_SYSTEM_PERF_MSG_RESOURCE"
		if resource == ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE: return "CDA_ACTUATOR_CMD_RESOURCE"
		if resource == ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE: return "CDA_ACTUATOR_RESPONSE_RESOURCE"
		if resource == ResourceNameEnum.CDA_MGMT_STATUS_CMD_RESOURCE: return "CDA_MGMT_STATUS_CMD_RESOURCE"
		if resource == ResourceNameEnum.CDA_MGMT_STATUS_MSG_RESOURCE: return "CDA_MGMT_STATUS_MSG_RESOURCE"
		if resource == ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE: return "CDA_SYSTEM_PERF_MSG_RESOURCE"
		return None
	
	
	
	def handleActuatorCommandMessage(self, data: ActuatorData) -> bool:
		"""
		A function handle actuator command message which can be locally or from GDA
		
		@return Boolean
		"""
		if data:
			logging.info("Processing actuator command message.")
			self.actuatorAdapterManager.sendActuatorCommand(data)
			return True
		else:
			logging.warning("Received invalid ActuatorData command message. Ignoring.")
			return False
