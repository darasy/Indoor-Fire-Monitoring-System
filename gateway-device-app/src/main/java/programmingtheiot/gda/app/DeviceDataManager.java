/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.app;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.BaseIotData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.data.SystemStateData;

import programmingtheiot.gda.connection.CloudClientConnector;
import programmingtheiot.gda.connection.CoapServerGateway;
import programmingtheiot.gda.connection.IPersistenceClient;
import programmingtheiot.gda.connection.IPubSubClient;
import programmingtheiot.gda.connection.IRequestResponseClient;
import programmingtheiot.gda.connection.MqttClientConnector;
import programmingtheiot.gda.connection.RedisPersistenceAdapter;
import programmingtheiot.gda.connection.SmtpClientConnector;
import programmingtheiot.gda.system.SystemPerformanceManager;
import programmingtheiot.gda.connection.AmazonS3ClientConnector;

/**
 * Shell representation of class for device data manager.
 *
 */
public class DeviceDataManager implements IDataMessageListener
{
	// static
	private static final Logger _Logger =
		Logger.getLogger(DeviceDataManager.class.getName());
	
	// private var's
	private boolean enableMqttClient = false;
	private boolean enableCoapServer = false;
	private boolean enableCloudClient = false;
	private boolean enableSmtpClient = false;
	private boolean enablePersistenceClient = false;
	
	private MqttClientConnector mqttClient = null;
	private CloudClientConnector cloudClient = null;
	private RedisPersistenceAdapter persistenceClient = null;
	private SmtpClientConnector smtpClient = null;
	private CoapServerGateway coapServer = null;
	private AmazonS3ClientConnector s3Client = null;
	
	private SystemPerformanceManager sysPerfManager = new SystemPerformanceManager(10);
	private DataUtil dataUtil = DataUtil.getInstance();
	private float lastSentEmailTimestamp = 0.0f;
	
	/**
	 *  constructors
	 */
	public DeviceDataManager()
	{
		// Derive from IDataMessageListener class
		super();
		// Initialize the connection
		initConnections();
		// Use ConfigUtil to retrieve communication connection enablement flags from the GatewayDevice section
		this.enableMqttClient  = ConfigUtil.getInstance().getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_MQTT_CLIENT_KEY);
		this.enableCoapServer  = ConfigUtil.getInstance().getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_COAP_SERVER_KEY);
		this.enableCloudClient = ConfigUtil.getInstance().getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_CLOUD_CLIENT_KEY);
		this.enableSmtpClient  = ConfigUtil.getInstance().getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_SMTP_CLIENT_KEY);
		this.enablePersistenceClient = ConfigUtil.getInstance().getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_PERSISTENCE_CLIENT_KEY);
		
		if (this.enableMqttClient) this.mqttClient = new MqttClientConnector();
		if (this.enableCloudClient) this.cloudClient = new CloudClientConnector();
		if (this.enableCoapServer) {
			String url = ConfigConst.DEFAULT_COAP_PROTOCOL + "://" + ConfigConst.DEFAULT_HOST + ":" + ConfigConst.DEFAULT_COAP_PORT;
			this.coapServer = new CoapServerGateway(true);
		}
		this.s3Client = new AmazonS3ClientConnector();
		if (this.enableSmtpClient) this.smtpClient = new SmtpClientConnector();
	}
	
	public DeviceDataManager(
		boolean enableMqttClient,
		boolean enableCoapServer,
		boolean enableCloudClient,
		boolean enableSmtpClient,
		boolean enablePersistenceClient)
	{
		// Derive from IDataMessageListener class
		super();
		// Initialize the connection
		//initConnections();
		// Retrieve communication connection enablement flags from the parameters
		this.enableMqttClient = enableMqttClient;
		this.enableCoapServer = enableCoapServer;
		this.enableCloudClient = enableCloudClient;
		this.enableSmtpClient = enableSmtpClient;
		this.enablePersistenceClient = enablePersistenceClient;
		
		if (this.enableMqttClient) this.mqttClient = new MqttClientConnector(false);
		if (this.enableCloudClient) {
			this.cloudClient = new CloudClientConnector();
			this.s3Client = new AmazonS3ClientConnector();
		}
		if (this.enableCoapServer) {
			String url =
					ConfigConst.DEFAULT_COAP_PROTOCOL + "://" + ConfigConst.DEFAULT_HOST + ":" + ConfigConst.DEFAULT_COAP_PORT;
				
				this.coapServer = new CoapServerGateway(true);
		}
		if (this.enablePersistenceClient) this.persistenceClient = new RedisPersistenceAdapter();
		if (this.enableSmtpClient) this.smtpClient = new SmtpClientConnector();
	}
	
	
	// public methods
	/**
	 * Handle Actuator command response message
	 */
	@Override
	public boolean handleActuatorCommandResponse(ResourceNameEnum resourceName, ActuatorData data)
	{
		// Log a message indicating this method has been called.
		_Logger.log(Level.INFO, "handleActuatorCommandResponse is called");
		// write the response to the local datastore this is mostly for logging and event tracking purposes
		try {
			if (data.getActuatorType() == 3) {
				if (data.getCommand() == ActuatorData.COMMAND_ON) {
					float currentTimeInMinute = System.currentTimeMillis() / 60000;
					if ((currentTimeInMinute - this.lastSentEmailTimestamp) > 10) {
						this.lastSentEmailTimestamp = currentTimeInMinute;
						this.smtpClient.sendEmail(data);
						data.setStateData("Image not included to store on GDA");
					}
				}
			}
			this.persistenceClient.storeData(resourceName.getResourceName(), 0, data);
			// If the ActuatorData response indicates an error, log the error. 
			if (data.hasError())
				_Logger.log(Level.WARNING, "Actuator data contains error flag");
			return true;
		}
		catch (Exception e) {
			_Logger.log(Level.WARNING, e.toString());
			return false;
		}
	}

	/**
	 * Handle incoming message (json)
	 */
	@Override
	public boolean handleIncomingMessage(ResourceNameEnum resourceName, String message)
	{
		// Log a message indicating this method has been called.
		_Logger.log(Level.INFO, "handleIncomingMessage is called");
		
		// The 'msg' will most likely be JSON that represents either an ActuatorData instance or a SystemStateData instance.
		// For now, use exception handling and the DataUtil class to attempt a conversion to ActuatorData first, 
		try {
			ActuatorData actuatorData = this.dataUtil.jsonToActuatorData(message);
			this.handleIncomingDataAnalysis(resourceName, actuatorData);
			return true;
		} 
		// then SystemStateData next. Whether you choose to implement a JSON schema validation mechanism (within DataUtil, for instance), 
		// or validate the message here is up to you, although either way, it should technically be done.
		catch (Exception e) {
			try {
				SystemStateData data = this.dataUtil.jsonToSystemStateData(message);
				this.handleIncomingDataAnalysis(resourceName, data);
				return true;
			}
			catch (Exception ee) {
				return false;
			}
		}
	}
	
	/**
	 * Handle Sensor data message
	 */
	@Override
	public boolean handleSensorMessage(ResourceNameEnum resourceName, SensorData data)
	{
		// Log a message indicating this method has been called.
		_Logger.log(Level.INFO, "handleSensorMessage() is called");
		// Check if the persistence client is active, and if so, write the response to the local datastore (this is mostly for logging and event tracking purposes).
		try {
			if (data.sensorType == SensorData.HUMIDITY_SENSOR_TYPE) {
				float humidFloor = (float) ConfigUtil.getInstance().getInteger(ConfigConst.GATEWAY_DEVICE, ConfigConst.HUMIDITY_FLOOR);
			    float humidCeiling = (float) ConfigUtil.getInstance().getInteger(ConfigConst.GATEWAY_DEVICE, ConfigConst.HUMIDITY_CEILING);
				if (data.getValue() < humidFloor || data.getValue() > humidCeiling) {
					ActuatorData newData = new ActuatorData();
					float newValue = (float) ((humidCeiling + humidFloor )  / 2.0);
					newData.setValue(newValue);
					newData.setCommand(ActuatorData.COMMAND_ON);
					newData.setName("Humidity");
					newData.setStateData("Humidity Value : " + newValue);
					newData.setActuatorType(2);
					if (this.enableMqttClient) {
						String msg = this.dataUtil.actuatorDataToJson(newData);
						this.mqttClient.publishMessage(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, msg, 1);
					}
				}
			}
			String json = this.dataUtil.sensorDataToJson(data);
			this.handleUpstreamTransmission(resourceName, json);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Handle SystemPerformanceData message
	 */
	@Override
	public boolean handleSystemPerformanceMessage(ResourceNameEnum resourceName, SystemPerformanceData data)
	{
		// Log a message indicating this method has been called.
		_Logger.log(Level.INFO, "handleSystemPerformanceMessage is called");
		// Check if the persistence client is active, and if so, write the response to the local datastore (this is mostly for logging and event tracking purposes).
		try {
			String json = this.dataUtil.systemPerformanceDataToJson(data);
			this.handleUpstreamTransmission(resourceName, json);
			return true;
		}
		catch (Exception e) {
			_Logger.log(Level.WARNING, "Fail to convert SystemPerformanceData to Json");
			return false;
		}
	}
	
	
	// Private methods
	private void handleIncomingDataAnalysis(ResourceNameEnum resourceName, ActuatorData data) {
		// TODO Auto-generated method stub
		// Log a message indicating this method has been called.
		_Logger.log(Level.FINE, "handleIncomingDataAnalysis is called");
		if (this.enableMqttClient) {
			if (data.getActuatorType() == 100) {
				String msg = this.dataUtil.actuatorDataToJson(data);
				this.mqttClient.publishMessage(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, msg, 2);
			}
		}
		
	}
	private void handleIncomingDataAnalysis(ResourceNameEnum resourceName, SystemStateData data) {
		// TODO Auto-generated method stub
		// Log a message indicating this method has been called.
		_Logger.log(Level.FINE, "handleIncomingDataAnalysis is called");
	}
	
	private void handleUpstreamTransmission(ResourceNameEnum resourceName, String msg) {
		// Log a message indicating this method has been called.
		_Logger.log(Level.CONFIG, "handleUpstreamTransmission is called");
		// Store within local data storage
		if (this.enablePersistenceClient) 
		{
			String topic = validateTopicName(resourceName);
			if (topic == "CDA_SENSOR_MSG_RESOURCE") {
				SensorData sensorData = this.dataUtil.jsonToSensorData(msg);
				this.persistenceClient.storeData(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName(), 0, sensorData);
			} 
			if (topic == "CDA_SYSTEM_PERF_MSG_RESOURCE") {
				_Logger.log(Level.CONFIG, "handle system performance message");
				SystemPerformanceData sysPerfData = this.dataUtil.jsonToSystemPerformanceData(msg);
				this.persistenceClient.storeData(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName(), 0, sysPerfData);
			}
			if (topic == "GDA_SYSTEM_PERF_MSG_RESOURCE") {
				_Logger.log(Level.CONFIG, "handle system performance message");
				SystemPerformanceData sysPerfData = this.dataUtil.jsonToSystemPerformanceData(msg);
				this.persistenceClient.storeData(ResourceNameEnum.GDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName(), 0, sysPerfData);
			}
		}
		// Store data into cloud
		if (this.enableCloudClient) 
		{
			String topic = validateTopicName(resourceName);
			if (topic == "CDA_SENSOR_MSG_RESOURCE") 
			{
				SensorData sensorData = this.dataUtil.jsonToSensorData(msg);
				this.s3Client.putData(resourceName, sensorData);
				this.cloudClient.sendEdgeDataToCloud(resourceName, sensorData);
			} 
			if (topic == "CDA_SYSTEM_PERF_MSG_RESOURCE" || topic == "GDA_SYSTEM_PERF_MSG_RESOURCE") 
			{
				_Logger.log(Level.CONFIG, "handle system performance message");
				SystemPerformanceData sysPerfData = this.dataUtil.jsonToSystemPerformanceData(msg);
				this.s3Client.putData(resourceName, sysPerfData);
				this.cloudClient.sendEdgeDataToCloud(resourceName, sysPerfData);
			}
		}
		
	}
	
	
	/**
	 * A method to start the device manager along with other connections
	 */
	public void startManager()
	{
		// Log an info message indicating manager was started.
		_Logger.log(Level.INFO, "Starting DeviceDataManager...");
		this.sysPerfManager.setDataMessageListener(this);
		this.sysPerfManager.startManager();
		/*
		 * Check the flag indicating whether or not the connections created during construction are enabled.
		 * For those that represent stateful client connections. simply call their respective connectClient() method and check the return code.
		 * For those that represent stateless client connections, there's nothing to do.
		 * For those that represent servers, simply call their respective startServer() method and check the return code.
		 */
		// MQTT Client
		if (this.enableMqttClient) {
			boolean returnCode = this.mqttClient.connectClient();
			this.mqttClient.setDataMessageListener(this);
			_Logger.log(Level.INFO, "MQTT Client connect with the return code {0}", returnCode);
		}
		// COAP Server
		if (this.enableCoapServer) {
			boolean returnCode = this.coapServer.startServer();
			this.coapServer.setDataMessageListener(this);
			_Logger.log(Level.INFO, "COAP Server start with the return code {0}", returnCode);
			try {
				Thread.sleep(50000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Cloud Client
		if (this.enableCloudClient) {
			this.cloudClient.setDataMessageListener(this);
			boolean returnCode = this.cloudClient.connectClient();
			
			_Logger.log(Level.INFO, "Cloud Client connect with the return code {0}", returnCode);
			
			this.s3Client.connectClient();
		}
		// Persistance Client
		if (this.enablePersistenceClient) {
			boolean returnCode = this.persistenceClient.connectClient();
			_Logger.log(Level.INFO, "Persistence Client connect with the return code {0}", returnCode);
		}
	
	}
	
	/**
	 * A method to stop the device manager along with other connections
	 */
	public void stopManager()
	{
		// Log an info message indicating manager was stopped.
		_Logger.log(Level.INFO, "Stopping DeviceDataManager...");	
		/*
		* Check the flag indicating whether or not the connections created during construction are enabled and their connection status or state. 
		* * The connections we'll create in Part 03 will have internal logic to track their state so you can generally leave it to the connection wrapper 
		* * itself to determine how to handle a potential 'stop' or 'disconnect' call multiple times. That said, it's best to do some validation up front 
		* * without introducing too much unrelated state management logic.
		* For those that represent stateful client connections. simply call their respective disconnectClient() method and check the return code.
		* For those that represent stateless client connections, there's nothing to do.
		* For those that represent servers, simply call their respective stopServer() method and check the return code.
		*/
		// MQTT Client
		if (this.enableMqttClient && this.mqttClient.isConnected()) {
			boolean returnCode = this.mqttClient.disconnectClient();
			_Logger.log(Level.INFO, "MQTT Client disconnect with the return code {0}", returnCode);
		}
		// COAP Server
		if (this.enableCoapServer) {
			boolean returnCode = this.coapServer.stopServer();
			_Logger.log(Level.INFO, "COAP Server stop with the return code {0}", returnCode);
		}
		// Cloud Client
		if (this.enableCloudClient && this.cloudClient.isMqttClientConnected()) {
			boolean returnCode = this.cloudClient.disconnectClient();
			_Logger.log(Level.INFO, "Cloud Client disconnect with the return code {0}", returnCode);
		}
		// Persistance Client
		if (this.enablePersistenceClient && this.persistenceClient.isConnected()) {
			boolean returnCode = this.persistenceClient.disconnectClient();
			_Logger.log(Level.INFO, "Persistence Client disconnect with the return code {0}", returnCode);
		}
		this.sysPerfManager.stopManager();
		
	}

	
	// private methods
	
	/**
	 * Initializes the enabled connections. This will NOT start them, but only create the
	 * instances that will be used in the {@link #startManager() and #stopManager()) methods.
	 * 
	 */
	private void initConnections()
	{
		this.mqttClient = new MqttClientConnector();
		this.cloudClient = new CloudClientConnector();
		this.persistenceClient = new RedisPersistenceAdapter();
		this.smtpClient = new SmtpClientConnector();
		this.coapServer = new CoapServerGateway();
		this.s3Client = new AmazonS3ClientConnector();
	}
	
	/**
	 * A method to validate the topic name and return that name for a comparison
	 * @param topicName
	 * @return String
	 */
	private String validateTopicName(ResourceNameEnum topicName) 
	{
		if (topicName == ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE) return "CDA_ACTUATOR_CMD_RESOURCE";
		if (topicName == ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE) return "CDA_ACTUATOR_CMD_RESOURCE";
		if (topicName == ResourceNameEnum.CDA_MGMT_STATUS_CMD_RESOURCE) return "CDA_MGMT_STATUS_CMD_RESOURCE";
		if (topicName == ResourceNameEnum.CDA_MGMT_STATUS_MSG_RESOURCE) return "CDA_MGMT_STATUS_MSG_RESOURCE";
		if (topicName == ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE) return "CDA_SENSOR_MSG_RESOURCE";
		if (topicName == ResourceNameEnum.GDA_MGMT_STATUS_CMD_RESOURCE) return "GDA_MGMT_STATUS_CMD_RESOURCE";
		if (topicName == ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE) return "GDA_MGMT_STATUS_MSG_RESOURCE";
		if (topicName == ResourceNameEnum.GDA_SYSTEM_PERF_MSG_RESOURCE) return "GDA_SYSTEM_PERF_MSG_RESOURCE";
		if (topicName == ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE) return "CDA_SYSTEM_PERF_MSG_RESOURCE";
		return null;
	}
}
