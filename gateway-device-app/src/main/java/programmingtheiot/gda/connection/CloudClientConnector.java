/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;

/**
 * Shell representation of class for Ubidots cloud client connector.
 *
 */
public class CloudClientConnector implements ICloudClient
{
	// private vars
	private String topicPrefix = "";
	private MqttClientConnector mqttClient = null;
	private IDataMessageListener dataMsgListener = null;
	private int qosLevel = 1;
	// static
	private static final Logger _Logger =
			Logger.getLogger(MqttClientConnector.class.getName());
	
	
	// Constructor
	public CloudClientConnector()
	{
		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		this.topicPrefix =
			configUtil.getProperty(ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.BASE_TOPIC_KEY);
		
		// Depending on the cloud service, the topic names may or may not begin with a "/", so this code
		// should be updated according to the cloud service provider's topic naming conventions
		if (topicPrefix == null) {
			topicPrefix = "/";
		} else {
			if (! topicPrefix.endsWith("/")) {
				topicPrefix += "/";
			}
		}
	}
	

	/**
	 * Connect client
	 */
	@Override
	public boolean connectClient() {
		_Logger.info("connectClient() has been called");
		if (this.mqttClient == null) {
			this.mqttClient = new MqttClientConnector(true);
			this.mqttClient.setDataMessageListener(this.dataMsgListener);
		}
				
		return this.mqttClient.connectClient();
	}

	/**
	 * Disconnect client
	 */
	@Override
	public boolean disconnectClient() {
		_Logger.info("disconnectClient() has been called");
		if (this.mqttClient != null) {
			return this.mqttClient.disconnectClient();
		}
		return false;
	}

	/**
	 * Send data from edge device to cloud
	 * 
	 * return boolean
	 */
	public boolean sendEdgeDataToCloud(ResourceNameEnum resource, ActuatorData data)
	{
		_Logger.info("sendEdgeDataToCloud() has been called");
		if (resource != null && data != null) {
			String payload = DataUtil.getInstance().actuatorDataToJson(data);
			String itemName = "";
			// Get the item name 
			if (data.getName() == ConfigConst.CPU_UTIL_NAME) itemName = "cpu";
			else if (data.getName() == ConfigConst.MEM_UTIL_NAME) itemName = "memory";
			else return false;
			// Create ubidot data
			String ubidotData = "{\"value\": " + data.getValue() + ", \"context\": " + payload +"}";
			// publish message to the cloud
			return publishMessageToCloud(resource, itemName, ubidotData);
		}
		
		return false;
	}
	
	/**
	 * Send data from edge device to cloud
	 * 
	 * return boolean
	 */
	@Override
	public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SensorData data)
	{
		_Logger.info("sendEdgeDataToCloud() has been called");
		if (resource != null && data != null) {
			String payload = DataUtil.getInstance().sensorDataToJson(data);
			String itemName = "";
			// Get item name
			if (data.getName() == ConfigConst.CPU_UTIL_NAME) itemName = "cpu";
			else if (data.getName() == ConfigConst.MEM_UTIL_NAME) itemName = "memory";
			else if (data.getName() == ConfigConst.DISK_UTIL_NAME) itemName = "disk";
			else if (data.sensorType == SensorData.TEMP_SENSOR_TYPE) itemName = "temperature";
			else if (data.sensorType == SensorData.HUMIDITY_SENSOR_TYPE) itemName = "humidity";
			else if (data.sensorType == SensorData.PRESSURE_SENSOR_TYPE) itemName = "pressure";
			else if (data.sensorType == SensorData.CAM_SENSOR_TYPE) itemName = "camera";
			else return false;
			// Create Ubidot data
			String ubidotData = "{\"value\": " + data.getValue() + ", \"context\": " + payload +"}";
			// publish to the cloud
			return publishMessageToCloud(resource, itemName, ubidotData);
		}
		
		return false;
	}

	/**
	 * Send data from edge device to cloud
	 * 
	 * return boolean
	 */
	@Override
	public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SystemPerformanceData data)
	{
		_Logger.info("sendEdgeDataToCloud() has been called");
		if (resource != null && data != null) {
			// Create sensor data of CPU
			SensorData cpuData = new SensorData();
			cpuData.setName(ConfigConst.CPU_UTIL_NAME);
			cpuData.setValue(data.getCpuUtilization());
			// Send the data to cloud
			boolean cpuDataSuccess = sendEdgeDataToCloud(resource, cpuData);
			
			if (! cpuDataSuccess) {
				_Logger.warning("Failed to send CPU utilization data to cloud service.");
			}
			// Create sensor data of Memory
			SensorData memData = new SensorData();
			memData.setName(ConfigConst.MEM_UTIL_NAME);
			memData.setValue(data.getMemoryUtilization());
			// Send the data to cloud
			boolean memDataSuccess = sendEdgeDataToCloud(resource, memData);
			
			if (! memDataSuccess) {
				_Logger.warning("Failed to send memory utilization data to cloud service.");
			}
			// Create sensor data of Disk
			SensorData diskData = new SensorData();
			diskData.setName(ConfigConst.DISK_UTIL_NAME);
			diskData.setValue(data.getDiskUtilization());
			// Send the data to cloud
			boolean diskDataSuccess = sendEdgeDataToCloud(resource, diskData);
			
			if (! diskDataSuccess) {
				_Logger.warning("Failed to send disk utilization data to cloud service.");
			}
			// Return a successful connection of all; otherwise, if one not send successfully, return False
			return (cpuDataSuccess == memDataSuccess) && (cpuDataSuccess == diskDataSuccess);
		}
		
		return false;
	}

	/**
	 * Subscribe to the edge events
	 */
	@Override
	public boolean subscribeToEdgeEvents(ResourceNameEnum resource) {
		_Logger.info("subscribeToEdgeEvents() has been called");
		boolean success = false;
		
		String topicName = null;
		// Make sure MQTT is connected
		if (isMqttClientConnected()) {
			topicName = createTopicName(resource);
			// Subscribe to the topic
			this.mqttClient.subscribeToTopic(topicName, this.qosLevel );
			
			success = true;
		} else {
			_Logger.warning("Subscription methods only available for MQTT. No MQTT connection to broker. Ignoring. Topic: " + topicName);
		}
		
		return success;
	}

	/**
	 * Unsubscribe to the edge events
	 */
	@Override
	public boolean unsubscribeFromEdgeEvents(ResourceNameEnum resource) {
		_Logger.info("unsubscribeFromEdgeEvents() has been called");
		boolean success = false;
		
		String topicName = null;
		// Make sure MQTT is connected
		if (isMqttClientConnected()) {
			topicName = createTopicName(resource);
			// Unsubscribe from the topic
			this.mqttClient.unsubscribeFromTopic(topicName);
			
			success = true;
		} else {
			_Logger.warning("Unsubscribe method only available for MQTT. No MQTT connection to broker. Ignoring. Topic: " + topicName);
		}
		
		return success;
	}

	/**
	 * Check if the connection is holding
	 */
	public boolean isMqttClientConnected() {
		return (this.mqttClient != null);
	}

	/**
	 * Assign data message listener
	 */
	@Override
	public boolean setDataMessageListener(IDataMessageListener listener) {
		_Logger.info("setDataMessageListener() has been called");
		if (listener != null) {
			this.dataMsgListener = listener;
			return true;
		}
		return false;
	}
	
	/**
	 * Create topic name
	 */
	private String createTopicName(ResourceNameEnum resource)
	{
		return this.topicPrefix + resource.getDeviceeName() + "/" + resource.getResourceType();
	}
	
	/**
	 * Get device label, constrained or gateway
	 */
	private String getLabelDevice(ResourceNameEnum resource) {
		if (resource == ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE) return "constraineddevice";
		if (resource == ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE) return "constraineddevice";
		if (resource == ResourceNameEnum.CDA_MGMT_STATUS_CMD_RESOURCE) return "constraineddevice";
		if (resource == ResourceNameEnum.CDA_MGMT_STATUS_MSG_RESOURCE) return "constraineddevice";
		if (resource == ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE) return "constraineddevice";
		if (resource == ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE) return "constraineddevice";
		if (resource == ResourceNameEnum.GDA_SYSTEM_PERF_MSG_RESOURCE) return "gatewaydevice";
		if (resource == ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE) return "gatewaydevice";
		return null;
	}

	/**
	 * Publish the MQTT message to cloud
	 */
	private boolean publishMessageToCloud(ResourceNameEnum resource, String itemName, String payload)
	{
		if (resource == ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE) {
			payload = "{\"value\": " + 0 + ", \"context\": {\"String\": \"" + payload +"\"}}";
		}
			
		String ubidotPayload = "{\"" + itemName + "\": " + payload + "}";
		
		String topicName = this.topicPrefix + resource.getDeviceeName();
		
		try {
			_Logger.finest("Publishing payload value(s) to Ubidots: " + topicName);

			this.mqttClient.publishMessage(topicName, ubidotPayload.getBytes(), 0);
			return true;
		} catch (Exception e) {
			_Logger.warning("Failed to publish message to Ubidots: " + topicName);
		}
		return false;
	}

	/**
	 * Additional public methods
	 */
			
	// Subscribe to topic method
	public boolean subscribeToTopic(ResourceNameEnum gdaMgmtStatusMsgResource, int qos) {
		this.qosLevel = qos;
		return this.subscribeToEdgeEvents(gdaMgmtStatusMsgResource);
	}

	// Unsubscribe from the topic
	public boolean unsubscribeFromTopic(ResourceNameEnum gdaMgmtStatusMsgResource) {
		return this.unsubscribeFromEdgeEvents(gdaMgmtStatusMsgResource);
	}

	// Publish message to the topic
	public boolean publishMessage(ResourceNameEnum gdaMgmtStatusMsgResource, String string, int qos) {
		this.qosLevel = qos;
		return this.publishMessageToCloud(gdaMgmtStatusMsgResource, gdaMgmtStatusMsgResource.getResourceType(), string);
	}

	
}
