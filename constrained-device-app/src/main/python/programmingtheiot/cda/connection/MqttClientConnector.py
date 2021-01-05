#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

import logging
import paho.mqtt.client as mqttClient

from programmingtheiot.common import ConfigUtil
from programmingtheiot.common import ConfigConst

from programmingtheiot.common.IDataMessageListener import IDataMessageListener
from programmingtheiot.common.ResourceNameEnum import ResourceNameEnum

from programmingtheiot.cda.connection.IPubSubClient import IPubSubClient
from _ast import Pass

from programmingtheiot.data.DataUtil import DataUtil

DEFAULT_QOS = 1

class MqttClientConnector(IPubSubClient):
	"""
	Shell representation of class for student implementation.
	
	"""
	'''
	Constructor
	'''
	def __init__(self, clientID: str = None):
		"""
		Default constructor. This will set remote broker information and client connection
		information based on the default configuration file contents.
		
		@param clientID Defaults to None. Can be set by caller. If this is used, it's
		critically important that a unique, non-conflicting name be used so to avoid
		causing the MQTT broker to disconnect any client using the same name. With
		auto-reconnect enabled, this can cause a race condition where each client with
		the same clientID continuously attempts to re-connect, causing the broker to
		disconnect the previous instance.
		"""
		
		self.mc = None
		self.clientID = clientID
		self.oldData = None
		self.config = ConfigUtil.ConfigUtil()
		self.host = self.config.getProperty(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.HOST_KEY, ConfigConst.DEFAULT_HOST)
		self.port = self.config.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_MQTT_PORT)
		self.keepAlive = self.config.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE)
		
		logging.info('\tMQTT Broker Host: ' + self.host)
		logging.info('\tMQTT Broker Port: ' + str(self.port))
		logging.info('\tMQTT Keep Alive:  ' + str(self.keepAlive))

		self.dataMsgListener = None
		
	
	def connectClient(self) -> bool:
		"""
		A function to connect to the client
		
		@return Boolean
		
		* Add the logic to create an instance of the client if needed, 
		* then, connect to the broker if not already connected, 
		* and log an info message indicating connector was started
		"""
		# Create an instance of the client if it is currently null
		if not self.mc:
			self.mc = mqttClient.Client(client_id = self.clientID, clean_session = True)
			self.mc.on_connect = self.onConnect
			self.mc.on_disconnect = self.onDisconnect
			self.mc.on_message = self.onMessage
			self.mc.on_publish = self.onPublish
			self.mc.on_subscribe = self.onSubscribe
		# If the client is not yet connected to the broker, connect it to the broker
		if not self.mc.is_connected():
			logging.info("MQTT client is now connected.")
			self.mc.connect(self.host, self.port, self.keepAlive)
			self.mc.loop_start()
			return True
		# If it is already connected, ignore the connection request
		else:
			logging.warn('MQTT client is already connected. Ignoring connect request.')
			return False
		
	def disconnectClient(self) -> bool:
		"""
		A function to disconnect the client
		
		@return Boolean
		
		* Add the logic to disconnect from the broker if currently connected, 
		* and log an info message indicating the connector was stopped.
		"""	
		# If the client is currently connected, disconnect it
		logging.info('Disconnecting from MQTT broker: ' + self.host)
		if self.mc.is_connected():
			self.mc.disconnect()
			self.mc.loop_stop()
			return True
		# If it is not connected, ignore it
		else:
			logging.warn('MQTT client is already disconnected. Ignoring disconnect request.')
			return False
		
	def onConnect(self, client, userdata, flags, rc):
		#logging.info("Client has successfully connected")
		logging.info('[Callback] Connected to MQTT broker. Result code: ' + str(rc))
		# NOTE: Use the QoS of your choice - '1' is only an example
		self.mc.subscribe(topic = ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE.value, qos = 1)
		self.mc.message_callback_add(sub = ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE.value, callback = self.onActuatorCommandMessage)
		
		
	def onDisconnect(self, client, userdata, rc):
		#logging.info("Client has successfully connected")
		logging.info('[Callback] Disconnected from MQTT broker. Result code: ' + str(rc))

		
	def onMessage(self, client, userdata, msg):
		logging.info("A message is received: " + str(msg.payload.decode("utf-8")))
			
	def onPublish(self, client, userdata, mid):
		logging.info("Publish, Message ID: " + str(mid))
		
	
	def onSubscribe(self, client, userdata, mid, granted_qos):
		logging.info("[Callback] Subscribed MID: " + str(mid))
		
	"""
	Implement the methods remaining three methods from the IPubSubClient interface by just loggin info and return False for now
	"""
	
	def publishMessage(self, resource: ResourceNameEnum, msg, qos: int = IPubSubClient.DEFAULT_QOS):
		"""
		publish message to the MQTT topic
		
		@return Boolean
		
		It will accept the topic name, message content, and requested QoS level for parameters. 
		It must validate the topic name and qos level. If the topic is invalid, return False. 
		If the QoS level is < 0 or > 2, set it to DEFAULT_QOS, which must be defined as a class-scoped ‘constant’. 
		If the publish is valid and the call to the MQTT client is successful, return True.
		"""
		#logging.info("publishMessage() method is called")
		
		topic = self.validateTopicName(resource)
		if not(topic): 
			#logging.warning("Invalid topic name")
			return False
		
		if qos < 0 or qos > 2: qos = DEFAULT_QOS
		
		try:
			msgInfo = self.mc.publish(topic = resource.value, payload = msg, qos = qos)
			logging.info("Publish a message to the topic: " + topic)
			# Tell the client to wait for each message to be published. 
			# The Paho client will return an MQTTMessageInfo when you publish a message. 
			#msgInfo.wait_for_publish()
			return True
		except Exception:
			#logging.warning("Fail to publish a message to the topic: " + str(topic))
			return False
	
	
	def subscribeToTopic(self, resource: ResourceNameEnum, qos: int = IPubSubClient.DEFAULT_QOS):
		"""
		Subscribe to the MQTT topic
		
		@return Boolean
		
		
		It will accept the topic name and requested QoS level for parameters.
		It must validate the topic name and qos level. If the topic is invalid, return False. 
		If the QoS level is < 0 or > 2, set it to DEFAULT_QOS, which must be defined as a class-scoped ‘constant’. 
		If the subscription is valid and the call to the MQTT client is successful, return True.
		"""
		logging.info("subscribeToTopic() method is called")
		
		topic = self.validateTopicName(resource)

		if not(topic): 
			logging.warning("Invalid topic name")
			return False
		
		if qos < 0 or qos > 2: qos = DEFAULT_QOS
		
		try:
			print("Subscribe to the topic " + str(topic))
			self.mc.subscribe(topic=topic, qos=qos)
			return True
		except Exception:
			logging.warning("Fail to subscribe to a topic: " + str(topic))
			return False
			
	def unsubscribeFromTopic(self, resource: ResourceNameEnum):
		"""
		Unsubscribe from the MQTT topic
		
		@return Boolean
		"""
		logging.info("unsubscribeFromTopic() method is called")
		topic = self.validateTopicName(resource)

		if not(topic): 
			logging.warning("Invalid topic name")
			return False

		try:
			self.mc.unsubscribe(topic=topic)
			return True
		except Exception:
			logging.warning("Fail to unsubscribe from a topic: " + str(topic))
			return False


	def onActuatorCommandMessage(self, client, userdata, msg):
		"""
		Handle actuator command message that is received either locally or from GDA
		"""
		logging.info('[Callback] Actuator command message received. Topic: %s', msg.topic)
		if self.dataMsgListener:
			try:
				actuatorData = DataUtil().jsonToActuatorData(msg.payload)
				if (self.oldData == None or self.oldData.getTimeStamp() != actuatorData.getTimeStamp()):
					self.oldData = actuatorData
					if not actuatorData.isResponse:
						self.dataMsgListener.handleActuatorCommandMessage(actuatorData)
			except:
				logging.exception("Failed to convert incoming actuation command payload to ActuatorData: ")


	def setDataMessageListener(self, listener: IDataMessageListener) -> bool:
		"""
		Set a new data message listener
		
		@return Boolean
		"""
		logging.info("setDataMessageListener() method is called")
		if listener:
			self.dataMsgListener = listener
			return True
		return False
	
	def validateTopicName(self, resource: ResourceNameEnum):
		"""
		A method to validate the topic name
		
		@return String
		"""
		if resource == ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE: return "CDA_ACTUATOR_CMD_RESOURCE"
		if resource == ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE: return "CDA_ACTUATOR_RESPONSE_RESOURCE"
		if resource == ResourceNameEnum.CDA_MGMT_STATUS_CMD_RESOURCE: return "CDA_MGMT_STATUS_CMD_RESOURCE"
		if resource == ResourceNameEnum.CDA_MGMT_STATUS_MSG_RESOURCE: return "CDA_MGMT_STATUS_MSG_RESOURCE"
		if resource == ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE: return "CDA_SENSOR_MSG_RESOURCE"
		if resource == ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE: return "CDA_SYSTEM_PERF_MSG_RESOURCE"
		return None
