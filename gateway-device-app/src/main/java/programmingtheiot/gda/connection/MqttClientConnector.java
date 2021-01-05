/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

import javax.net.ssl.SSLSocketFactory;
import programmingtheiot.common.SimpleCertManagementUtil;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import java.util.Base64;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class MqttClientConnector implements IPubSubClient, MqttCallbackExtended
{
	// static
	private static final Logger _Logger =
		Logger.getLogger(MqttClientConnector.class.getName());
	
	// params
	private String host;
	private int port;
	private int brokerKeepAlive;
	private String clientID;
	private MemoryPersistence persistence;
	private MqttConnectOptions connOpts;
	private Object brokerAddr;
	private String protocol = "tcp";
	private MqttClient mqttClient;
	private boolean connected = false;
	private int DEFAULT_QOS = 1;
	private boolean enableEncryption;
	private String pemFileName;
	private IDataMessageListener dataMsgListener = null;
	private boolean useCloudGatewayConfig = false;
	// constructors

	private String userToken;
	
	/**
	 * Default.
	 * 
	 */
	public MqttClientConnector()
	{
		this(false);
	}
	
	public MqttClientConnector(boolean useCloudGatewayConfig)
	{
		super();
		
		this.useCloudGatewayConfig = useCloudGatewayConfig;
		
		if (useCloudGatewayConfig) {
			initClientParameters(ConfigConst.CLOUD_GATEWAY_SERVICE);
		} else {
			initClientParameters(ConfigConst.MQTT_GATEWAY_SERVICE);
		}
	}
	
	
	// public methods
	// Connect the MQTT client
	@Override
	public boolean connectClient()
	{
		// If mqttClient is null, create a new MQTT client instance using try/catch
		if (this.mqttClient == null) {
		    try {
				this.mqttClient = new MqttClient((String) this.brokerAddr, this.clientID, this.persistence);
				_Logger.info("Attempting to connect to the broker: " + this.brokerAddr);
			} catch (MqttException e) {
				_Logger.warning("Cannot create mqttClient instance");
				return false;
			}
		    // Set callback on MqqqClientConnector
		    this.mqttClient.setCallback(this);
		}
		// Check if mqttClient is connected or not; if not connected, set up a new connection
		if (! this.mqttClient.isConnected()) {
		    try {
				this.mqttClient.connect(this.connOpts);
				_Logger.info("MQTT client is now connected to the broker: " + this.brokerAddr);
				this.connected = true;
				return true;
			} catch (MqttException e) {
				_Logger.warning("MQTT client cannot connect.");
				return false;
			}
		}
		// If already connected, ignore the request
		else {
			_Logger.warning("MQTT client is already connected. Ignoring connect request.");
			return false;
		}
	}

	// Disconnect the MQTT client
	@Override
	public boolean disconnectClient()
	{
		_Logger.info("Disconnecting from the broker");
		// if mqttClient is null, ignore the request
		if (this.mqttClient == null) {
		    _Logger.warning("No MQTT Client instance created.");
			return false;
		}
		// If mqtt client is currently connected, disconnect using try/catch
		if (this.mqttClient.isConnected()) {
		    try {
				this.mqttClient.disconnect();
				_Logger.info("MQTT client is now disconnected from the broker: " + this.brokerAddr);
				this.connected = false;
				return true;
			} catch (MqttException e) {
				_Logger.warning("MQTT client cannot disconnected from the broker: " + this.brokerAddr);
				return false;
			}
		}
		// If it is already disconnected, ignore the request
		else {
			_Logger.warning("MQTT client is already disconnected from the broker: " + this.brokerAddr);
			return false;
		}
	}

	// A method to return the current connected status
	public boolean isConnected()
	{
		return this.connected;
	}
	
	// A method to publish the message to the broker
	@Override
	public boolean publishMessage(ResourceNameEnum topicName, String msg, int qos)
	{
		//_Logger.info("publishMessage() is called");
		// Validate the topic name
		String topic = validateTopicName(topicName);
		if (topic == null) {
			//_Logger.warning("Invalid topic name");
			return false;
		}

		return this.publishMessage(topicName.getResourceName(), msg.getBytes(), qos);
	}

	// A method to subscribe to the topic
	@Override
	public boolean subscribeToTopic(ResourceNameEnum topicName, int qos)
	{
		_Logger.info("subscribeToTopic() is called");		
		// Validate the topic name
		String topic = validateTopicName(topicName);
		if (topic == null) {
			_Logger.warning("Invalid topic name");
			return false;
		}
		return this.subscribeToTopic(topicName.getResourceName(), qos);
	}

	// A method to unsubscribe to the topic
	@Override
	public boolean unsubscribeFromTopic(ResourceNameEnum topicName)
	{
		_Logger.info("unsubscribeFromTopic() is called");	
		// Validate the topic name
		String topic = validateTopicName(topicName);
		if (topic == null) {
			_Logger.warning("Invalid topic name");
			return false;
		}
		return this.unsubscribeFromTopic(topicName.getResourceName());
	}

	
	// Publish message to the broker
	protected boolean publishMessage(String topic, byte[] payload, int qos)
	{
		MqttMessage message = new MqttMessage(payload);
		// Check for a valid QoS
		if (qos < 0 || qos > 2) {
			qos = 0;
		}
		// Assign MQTT QoS
		message.setQos(qos);
		
		try {
			_Logger.info("Publishing message to topic: " + topic);
			// Publish
			this.mqttClient.publish(topic, message);
			return true;
		} catch (MqttPersistenceException e) {
			_Logger.warning("Persistence exception thrown when publishing to topic: " + topic);
		} catch (MqttException e) {
			_Logger.warning("MQTT exception thrown when publishing to topic: " + topic);
		}
		
		return false;
	}
	
	// Subscribe to the MQTT topic
	protected boolean subscribeToTopic(String topic, int qos)
	{
		try {
			this.mqttClient.subscribe(topic, qos);
			
			return true;
		} catch (MqttException e) {
			_Logger.warning("Failed to subscribe to topic: " + topic);
		}
		
		return false;
	}
	
	// Unsubscribe from the MQTT topic
	protected boolean unsubscribeFromTopic(String topic)
	{
		try {
			this.mqttClient.unsubscribe(topic);
			return true;
		} catch (MqttException e) {
			_Logger.warning("Failed to unsubscribe from topic: " + topic);
		}
		
		return false;
	}
	
	
	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
        if (listener != null) {
            this.dataMsgListener = listener;
            return true;
        }
        return false;
	}
	
	// callbacks
	@Override
	public void connectComplete(boolean reconnect, String serverURI)
	{
		_Logger.info("MQTT connection successful (is reconnect = " + reconnect + "). Broker: " + serverURI);
		
		int qos = 1;
		
		// Subscribe to the actuator topic
		if (! this.useCloudGatewayConfig) {
			
			try {
				this.mqttClient.subscribe(
					ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName(),
					qos,
					new ActuatorResponseMessageListener(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, this.dataMsgListener));
			} catch (MqttException e) {
				_Logger.warning("Failed to subscribe to CDA actuator response topic." + this.brokerAddr);
			}
			
			// Subscribe to the sensor topic
			try {
				this.mqttClient.subscribe(
					ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName(),
					qos,
					new SensorResponseMessageListener(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, this.dataMsgListener));
				_Logger.info("Subscribe to the topic: " + ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName());
			} catch (MqttException e) {
				_Logger.warning("Failed to subscribe to CDA sensor response topic.");
			}
			// Subscribe to the SystemPerformanceData
			try {
				this.mqttClient.subscribe(
					ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName(),
					qos,
					new SystemPerformanceDataResponseMessageListener(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, this.dataMsgListener));
			} catch (MqttException e) {
				_Logger.warning("Failed to subscribe to CDA system performance response topic.");
			}
		}
		// Subscribe to the LED Actuator events
		else {
			try {
				this.mqttClient.subscribe(
					ConfigConst.UBIDOTS_LED_EVENT,
					qos,
					new UbidotMessageListener(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, this.dataMsgListener));
			} catch (MqttException e) {
				_Logger.warning("Failed to subscribe to the cloud LED actuator event.");
			}
		}
	}

	@Override
	public void connectionLost(Throwable t)
	{
		_Logger.info("The client has successfully connected");
	}
	
	@Override
	public void deliveryComplete(IMqttDeliveryToken token)
	{
		//_Logger.info("The client has successfully published a message");
		
	}
	
	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception
	{
		_Logger.info("A message is received: " + msg.toString());;
	}

	
	// private methods
	
	/**
	 * Called by the constructor to set the MQTT client parameters to be used for the connection.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
	private void initClientParameters(String configSectionName)
	{
		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		this.host =
			configUtil.getProperty(
				configSectionName, ConfigConst.HOST_KEY, ConfigConst.DEFAULT_HOST);
		this.port =
			configUtil.getInteger(
				configSectionName, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_MQTT_PORT);
		this.brokerKeepAlive =
			configUtil.getInteger(
				configSectionName, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
		this.enableEncryption =
			configUtil.getBoolean(
				configSectionName, ConfigConst.ENABLE_CRYPT_KEY);
		this.pemFileName =
			configUtil.getProperty(
				configSectionName, ConfigConst.CERT_FILE_KEY);
		this.userToken = configUtil.getProperty(
				configSectionName, ConfigConst.CRED_FILE_KEY);
		
		// Paho Java client requires a client ID
		this.clientID = MqttClient.generateClientId();
		
		// these are specific to the MQTT connection which will be used during connect
		this.persistence = new MemoryPersistence();
		this.connOpts    = new MqttConnectOptions();
		
		this.connOpts.setKeepAliveInterval(this.brokerKeepAlive);
		this.connOpts.setCleanSession(true);
		this.connOpts.setAutomaticReconnect(true);
		
		// if encryption is enabled, try to load and apply the cert(s)
		
		if (this.enableEncryption && this.useCloudGatewayConfig) {
			initSecureConnectionParameters(configSectionName);
		}
		
		
		// if there's a credential file, try to load and apply them
		if (configUtil.hasProperty(configSectionName, ConfigConst.CRED_FILE_KEY) && this.useCloudGatewayConfig) {
			initCredentialConnectionParameters(configSectionName);
		}
		
		// NOTE: URL does not have a protocol handler for "tcp" or "ssl",
		// so construct the URL manually
		this.brokerAddr  = this.protocol + "://" + this.host + ":" + this.port;
		
		_Logger.info("Using URL for broker conn: " + this.brokerAddr);
	}
	
	/**
	 * Called by {@link #initClientParameters(String)} to load credentials.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
	private void initCredentialConnectionParameters(String configSectionName)
	{
		_Logger.info("initCredentialConnectionParameters() is called");
		if (this.userToken != null) {
			Path path = Paths.get(this.userToken);
			String filecontents = null;
			try {
				filecontents = Files.readString(path, StandardCharsets.ISO_8859_1);
				String userName = filecontents.substring(12, filecontents.length() -1);
				this.connOpts.setUserName(userName);
			} catch (IOException e) {
				_Logger.warning("Cannot read the token");
			}
			
		}
	}
	
	/**
	 * Called by {@link #initClientParameters(String)} to enable encryption.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
	private void initSecureConnectionParameters(String configSectionName)
	{
		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		try {
			_Logger.info("Configuring TLS...");
			
			if (this.pemFileName != null) {
				File file = new File(this.pemFileName);
				
				if (file.exists()) {
					_Logger.info("PEM file valid. Using secure connection: " + this.pemFileName);
				} else {
					this.enableEncryption = false;
					
					_Logger.log(Level.WARNING, "PEM file invalid. Using insecure connection: " + pemFileName, new Exception());
					
					return;
				}
			}
			
			SSLSocketFactory sslFactory =
				SimpleCertManagementUtil.getInstance().loadCertificate(this.pemFileName);
			
			this.connOpts.setSocketFactory(sslFactory);
			
			// override current config parameters
			this.port =
				configUtil.getInteger(
					configSectionName, ConfigConst.SECURE_PORT_KEY, ConfigConst.DEFAULT_MQTT_SECURE_PORT);
			
			this.protocol = ConfigConst.DEFAULT_MQTT_SECURE_PROTOCOL;
			
			_Logger.info("TLS enabled.");
			
			this.enableEncryption = true;
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to initialize secure MQTT connection. Using insecure connection.", e);
			this.protocol = "tcp";
			this.enableEncryption = false;
		}
	}
	
	
	// A helper method to validate and return the topic name
	private String validateTopicName(ResourceNameEnum topicName) {
		if (topicName == ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE) return "CDA_ACTUATOR_CMD_RESOURCE";
		if (topicName == ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE) return "CDA_ACTUATOR_RESPONSE_RESOURCE";
		if (topicName == ResourceNameEnum.CDA_MGMT_STATUS_CMD_RESOURCE) return "CDA_MGMT_STATUS_CMD_RESOURCE";
		if (topicName == ResourceNameEnum.CDA_MGMT_STATUS_MSG_RESOURCE) return "CDA_MGMT_STATUS_MSG_RESOURCE";
		if (topicName == ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE) return "CDA_SENSOR_MSG_RESOURCE";
		if (topicName == ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE) return "CDA_SYSTEM_PERF_MSG_RESOURCE";
		if (topicName == ResourceNameEnum.GDA_MGMT_STATUS_CMD_RESOURCE) return "GDA_MGMT_STATUS_CMD_RESOURCE";
		if (topicName == ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE) return "GDA_MGMT_STATUS_MSG_RESOURCE";
		if (topicName == ResourceNameEnum.UBIDOT_ACTUATOR_LED) return "UBIDOT_ACTUATOR";
		return null;
	}
	
	/*
	 * Inner Class
	 */
	// Actuator Class
	private class ActuatorResponseMessageListener implements IMqttMessageListener
	{
		private ResourceNameEnum resource = null;
		private IDataMessageListener dataMsgListener = null;
		
		ActuatorResponseMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
		{
			this.resource = resource;
			this.dataMsgListener = dataMsgListener;
		}
		
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
			_Logger.info("Actuator message has arrived");
			try {
				ActuatorData actuatorData =
					DataUtil.getInstance().jsonToActuatorData(new String(message.getPayload()));
				if (this.dataMsgListener != null) {
					this.dataMsgListener.handleActuatorCommandResponse(this.resource, actuatorData);
				}
			} catch (Exception e) {
				_Logger.warning("Failed to convert message payload to ActuatorData. ");
			}
		}
	}
	// Sensor Class
	private class SensorResponseMessageListener implements IMqttMessageListener
	{
		private ResourceNameEnum resource = null;
		private IDataMessageListener dataMsgeListener = null;
		
		SensorResponseMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
		{
			this.resource = resource;
			this.dataMsgeListener = dataMsgListener;
		}
		
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
			_Logger.info("Sensor message has arrived");
			try {
				SensorData sensorData =
					DataUtil.getInstance().jsonToSensorData(new String(message.getPayload()));

				if (this.dataMsgeListener != null) {
					this.dataMsgeListener.handleSensorMessage(resource, sensorData);
				}
			} catch (Exception e) {
				try {
					// Try decoding an image
					//byte[] decodedBytes = Base64.getDecoder().decode(message.getPayload());
					// Convert byte array into an image
					//FileUtils.writeByteArrayToFile(new File("TEST90909.JPG"), decodedBytes);
				} catch (Exception ee) {
					_Logger.warning("Failed to convert message payload to SensorData.");
				}
				_Logger.warning("Failed to convert message payload to SensorData.");
				
			}
		}
	}
	// SystemPerformanceData Class
	private class SystemPerformanceDataResponseMessageListener implements IMqttMessageListener
	{
		private ResourceNameEnum resource = null;
		private IDataMessageListener dataMsgListener = null;
		
		SystemPerformanceDataResponseMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
		{
			this.resource = resource;
			this.dataMsgListener = dataMsgListener;
		}
		
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
			_Logger.info("System Performance message has arrived");
			try {
				SystemPerformanceData systemPerformanceData =
					DataUtil.getInstance().jsonToSystemPerformanceData(new String(message.getPayload()));
				
				if (this.dataMsgListener != null) {
					this.dataMsgListener.handleSystemPerformanceMessage(resource, systemPerformanceData);
				}
			} catch (Exception e) {
				_Logger.warning("Failed to convert message payload to SystemPerformanceData.");
			}
		}
	}
	// Ubidot Class
	private class UbidotMessageListener implements IMqttMessageListener
	{
		private ResourceNameEnum resource = null;
		private IDataMessageListener dataMsgListener = null;
		
		UbidotMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
		{
			this.resource = resource;
			this.dataMsgListener = dataMsgListener;
		}
		
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
			_Logger.info("Actuator message has arrived:  " + message.toString());

			if (message.toString().equals("1.0")) {
				try {
					ActuatorData actuatorData = new ActuatorData();
					actuatorData.setActuatorType(100);
					actuatorData.setStateData("TURN ON LED ACTUATOR");
					actuatorData.setCommand(1);
					actuatorData.setValue(0.0f);
					String msgString = DataUtil.getInstance().actuatorDataToJson(actuatorData);
					if (this.dataMsgListener != null) {
						this.dataMsgListener.handleIncomingMessage(this.resource, msgString);
					}
				} catch (Exception e) {
					_Logger.warning("Failed to convert message payload to ActuatorData. ");
				}
			}
		}
	}
}
