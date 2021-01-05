/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.awt.List;
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

import com.amazonaws.*;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudsearchdomain.model.Bucket;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import java.text.SimpleDateFormat;  
import java.util.Date; 
/**
 * Shell representation of class for Amazon AWS S3 client.
 * 
 */
public class AmazonS3ClientConnector
{
	// static
	private static final Logger _Logger =
		Logger.getLogger(AmazonS3ClientConnector.class.getName());
	
	// params
	private boolean connected = false;
	
	private IDataMessageListener dataMsgListener = null;
	
	private AmazonS3 s3Client;
	private Regions clientRegion;
	private BasicAWSCredentials awsCreds;
	private String access_key;
	private String secret_key;

	private String userAccessKey;
	private String userSecretKey;
	
	// constructor
	/**
	 * Default.
	 * 
	 */
	public AmazonS3ClientConnector()
	{
		String filecontents = null;
		ConfigUtil configUtil = ConfigUtil.getInstance();
		// Get user access and secret keys to get access to Amazon S3 bucket
		this.userAccessKey= configUtil.getProperty(
				ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.AWS_ACCESS_KEY);
		this.userSecretKey = configUtil.getProperty(
				ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.AWS_SECRET_KEY);
		// Read the file contents of access and secret keys
		try {
			Path path = Paths.get(this.userAccessKey);
			filecontents = Files.readString(path, StandardCharsets.ISO_8859_1);
			this.access_key = filecontents;
			
		} catch (IOException e) {
			_Logger.warning("Cannot read the access key");
		}
		
		try {
			Path path = Paths.get(this.userSecretKey);
			filecontents = Files.readString(path, StandardCharsets.ISO_8859_1);
			this.secret_key = filecontents;
			
		} catch (IOException e) {
			_Logger.warning("Cannot read the secret key");
		}
		// Create a S3 credential object 
		this.clientRegion = Regions.US_EAST_1;
        this.awsCreds = new BasicAWSCredentials(this.access_key, this.secret_key);
	}
	
	
	/**
	 * Connect to AWS S3 by initalizing the S3client parameter
	 * 
	 * @return boolean
	 */
	public boolean connectClient()
	{
		try {
			// Create a S3 client object using the credential information from constructor step 
			this.s3Client = AmazonS3ClientBuilder.standard().withRegion(this.clientRegion)
                    .withCredentials(new AWSStaticCredentialsProvider(this.awsCreds))
                    .build();
			this.connected = true;
			_Logger.info("Can Connect to Amazon AWS S3");
			return true;
			}
		catch (Exception e) {
			_Logger.warning("Fail to connect to Amazon AWS S3");
		}
		return true;
	}

	/** Disconnect the MQTT client
	 * 
	 * @return boolean
	 */
	public boolean disconnectClient()
	{
		this.s3Client = null;
		this.connected = false;
		return true;
	}

	/**
	 * A method to return the current connected status
	 * @return boolean
	 */
	public boolean isConnected()
	{
		return this.connected;
	}
	
	
	/**
	 * A method to pass the Sensor data along to the S3 bucket
	 * @param resource: ResourceNameEnum
	 * @param data: SensorData
	 * @return boolean
	 */
	public boolean putData(ResourceNameEnum resource, SensorData data) {
		try {
			String payload = DataUtil.getInstance().sensorDataToJson(data);
			String itemName = "";
			String bucket = this.getLabelDevice(resource);
			
			if (data.getName() == ConfigConst.CPU_UTIL_NAME) itemName = "cpu";
			else if (data.getName() == ConfigConst.MEM_UTIL_NAME) itemName = "memory";
			else if (data.getName() == ConfigConst.DISK_UTIL_NAME) itemName = "disk";
			else if (data.sensorType == SensorData.TEMP_SENSOR_TYPE)  itemName = "temperature";
			else if (data.sensorType == SensorData.HUMIDITY_SENSOR_TYPE) itemName = "humidity";
			else if (data.sensorType == SensorData.PRESSURE_SENSOR_TYPE) itemName = "pressure";
			else if (data.sensorType == SensorData.CAM_SENSOR_TYPE) itemName = "camera";
			else return false;
		    
			bucket = bucket + "/" + itemName;
			String keyFileName = data.getTimeStamp() + ".json";
			
			this.s3Client.putObject(bucket, keyFileName, payload);
			_Logger.info("Successfully upload to the AWS S3 bucket : " + bucket);
			return true;
		}
		catch (Exception e) {
			_Logger.warning("Fail to upload to AWS S3 bucket");
		}
		return false;
	}
	
	
	/**
	 * A method to pass the SystemPerformanceData data along to the S3 bucket
	 * @param resource: ResourceNameEnum
	 * @param data: SystemPerformanceData
	 * @return boolean
	 */
	public boolean putData(ResourceNameEnum resource, SystemPerformanceData data) {
		try {
			SensorData cpuData = new SensorData();
			cpuData.setName(ConfigConst.CPU_UTIL_NAME);
			cpuData.setValue(data.getCpuUtilization());
			cpuData.setTimeStamp(data.getTimeStamp());
			
			String payload = DataUtil.getInstance().sensorDataToJson(cpuData);
			String itemName = "cpu";
			String deviceName = this.getLabelDevice(resource);
		
			String bucket = deviceName + "/" + itemName;
			String keyFileName = data.getTimeStamp() + ".json";
			
			this.s3Client.putObject(bucket, keyFileName, payload);
			_Logger.info("Successfully upload to the AWS S3 bucket : " + bucket);

			SensorData memData = new SensorData();
			memData.setName(ConfigConst.MEM_UTIL_NAME);
			memData.setValue(data.getMemoryUtilization());
			memData.setTimeStamp(data.getTimeStamp());
			payload = DataUtil.getInstance().sensorDataToJson(memData);
			
			itemName = "memory";
			bucket = deviceName + "/" + itemName;
			
			this.s3Client.putObject(bucket, keyFileName, payload);
			_Logger.info("Successfully upload to the AWS S3 bucket : " + bucket);
			
			SensorData diskData = new SensorData();
			diskData.setName(ConfigConst.DISK_UTIL_NAME);
			diskData.setValue(data.getDiskUtilization());
			diskData.setTimeStamp(data.getTimeStamp());
			payload = DataUtil.getInstance().sensorDataToJson(diskData);
			
			itemName = "disk";
			bucket = deviceName + "/" + itemName;
			
			this.s3Client.putObject(bucket, keyFileName, payload);
			_Logger.info("Successfully upload to the AWS S3 bucket : " + bucket);
			
			return true;
		}
		catch (Exception e) {
			_Logger.warning("Fail to upload to the AWS S3 bucket");
		}
		return false;
	}
	
	

	
	/**
	 * A method to analyze the resource name
	 * @param resource
	 * @return String
	 */
	private String getLabelDevice(ResourceNameEnum resource) {
		if (resource == ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE) return "cda6530/actuators";
		if (resource == ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE) return "cda6530/sensors";
		if (resource == ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE) return "cda6530/systemperformance";
		if (resource == ResourceNameEnum.GDA_SYSTEM_PERF_MSG_RESOURCE) return "gda6530/systemperformance";
		return null;
	}
}
