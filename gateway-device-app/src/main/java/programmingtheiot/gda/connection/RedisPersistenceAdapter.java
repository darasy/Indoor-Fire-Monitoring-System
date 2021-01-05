/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.DefaultDataMessageListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Shell representation of class for Redis data storage.
 * 
 */
public class RedisPersistenceAdapter implements IPersistenceClient
{
	// static
	private static final Logger _Logger =
		Logger.getLogger(RedisPersistenceAdapter.class.getName());
	
	// private var's
	private int port = -1;
	private String host = null;
	private Boolean isConnected = false;
	private IPersistenceListener listener = null;
	
	// constructors
	public Jedis redisClient = null;
	/**
	 * Default.
	 * 
	 */
	public RedisPersistenceAdapter()
	{
		super();
		
		initClient();
	}
	
	
	// public methods
	/**
	 * Connect the redis client
	 */
	@Override
	public boolean connectClient()
	{
		try {
			if (this.redisClient.isConnected() == false) {
				this.redisClient.connect();
				this.isConnected = true;
				return true;
			} else {
				_Logger.warning("Redis Cleint is already connected. Ignore the request");
				return false;
			}
		} 
		catch (JedisConnectionException e) {
			_Logger.warning("Fail to connect Redis client");
		}
		return false;
	}

	/**
	 * Disconnect the redis client
	 */
	@Override
	public boolean disconnectClient()
	{
		_Logger.info("disconnectClient() is called");
		try {
			if (this.redisClient.isConnected()) {
				this.redisClient.disconnect();
				this.isConnected = false;
				return true;
			} else {
				_Logger.warning("Redis Cleint is already disconnected. Ignore the request");
				return false;
			}
		} 
		catch (JedisConnectionException e) {
			_Logger.warning("Fail to disconnect Redis client");
		}
		return false;
	}

	@Override
	public ActuatorData[] getActuatorData(String topic, Date startDate, Date endDate)
	{
		_Logger.info("getActuatorData() is called");
		return null;
	}

	@Override
	public SensorData[] getSensorData(String topic, Date startDate, Date endDate)
	{	_Logger.info("getSensorData() is called");
		return null;
	}

	@Override
	public void registerDataStorageListener(Class cType, IPersistenceListener listener, String... topics)
	{
		this.listener = listener;
	}


	/**
	 * Store ActuatorData to redis database
	 * @param topic: topic name
	 * @param qos
	 * @param actuatorData: Actuator Data
	 * @return boolean
	 */
	public boolean storeData(String topic, int qos, ActuatorData actuatorData)
	{
		_Logger.info("storeData() is called");
		try {
			String json = DataUtil.getInstance().actuatorDataToJson(actuatorData);
			String field = "0"; 
			if (actuatorData.getActuatorType() == 1) {
				field = "HVAC";
			}
			if (actuatorData.getActuatorType() == 2) {
				field = "HUMIDIFIER";
			}
			if (actuatorData.getActuatorType() == 100) {
				field = "LED";
			}
			
			this.redisClient.set(topic + "-" + field + "_" + actuatorData.getTimeStamp(), json);
			return true;
		} catch (Exception e) {
			_Logger.warning("Fail to store ActuatorData");
			return false;
		}
	}

	/**
	 * Store SensorData to redis database
	 * @param topic: topic name
	 * @param qos
	 * @param actuatorData: Actuator Data
	 * @return boolean
	 */
	public boolean storeData(String topic, int qos, SensorData sensorData)
	{
		_Logger.info("storeData() is called");
		try {
			String json = DataUtil.getInstance().sensorDataToJson(sensorData);
			
			String field = "0"; 
			if (sensorData.sensorType == SensorData.HUMIDITY_SENSOR_TYPE) {
				field = "HUMIDITY";
			}
			if (sensorData.sensorType == SensorData.TEMP_SENSOR_TYPE) {
				field = "TEMPERATURE";
			}

			if (field == "0") return false;
			
			this.redisClient.set(topic + "-" + field + "_" + sensorData.getTimeStamp(), json);
			return true;
		} catch (Exception e) {
			_Logger.warning("Fail to store SensorData");
			return false;
		}
	}

	/**
	 * Store SystemPerformanceData to redis database
	 * @param topic: topic name
	 * @param qos
	 * @param actuatorData: Actuator Data
	 * @return boolean
	 */
	public boolean storeData(String topic, int qos, SystemPerformanceData sysPerfData)
	{
		_Logger.info("storeData() is called");
		try {
			String json = DataUtil.getInstance().systemPerformanceDataToJson(sysPerfData);
			this.redisClient.set(topic + "-" + sysPerfData.getName() + "_" + sysPerfData.getTimeStamp(), json);
			return true;
		} catch (Exception e) {
			_Logger.warning("Fail to store SystemPerformanceData");
			return false;
		}
	}
	
	
	// private methods
	
	/**
	 * Generates a listener key map from the class type and topic.
	 * The format will be as follows:
	 * <br>'simple class name' + "_" + 'topic name'
	 * <br>e.g. ActuatorData_localhost/fan
	 * <br>e.g. SensorData_localhost/temperature
	 * <p>
	 * If the class type is null, it will simply be dropped and
	 * only the topic name will be used in the key. If the topic
	 * name is also null or invalid (e.g. empty), the 'all' keyword
	 * will be used instead.
	 * 
	 * @param cType The class type to use in the key.
	 * @param topic The topic name to use in the key.
	 * @return String The key derived from cType and topic, as per above.
	 */
	private String getListenerMapKey(Class cType, String topic)
	{
		StringBuilder buf = new StringBuilder();
		
		if (cType != null) {
			buf.append(cType.getSimpleName()).append("_");
		}
		
		if (topic != null && topic.trim().length() > 0) {
			buf.append(topic.trim());
		} else {
			buf.append("all");
		}
		
		String key = buf.toString();
		
		_Logger.info("Generated listener map lookup key from '" + cType + "' and '" + topic + "': " + key);
		
		return key;
	}
	
	private void initClient()
	{
		this.host = "localhost";
		this.port = 6379;
		this.redisClient = new Jedis(this.host, this.port);
	}
	
	
	private Long updateRedisDataElement(String topic, double score, String payload)
	{
		return 0L;
	}


	public boolean isConnected() {
		// TODO Auto-generated method stub
		return this.isConnected;
	}


	@Override
	public boolean storeData(String topic, int qos, ActuatorData... data) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean storeData(String topic, int qos, SensorData... data) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean storeData(String topic, int qos, SystemPerformanceData... data) {
		// TODO Auto-generated method stub
		return false;
	}


}