/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.gson.Gson;

/**
 * Shell representation of class for student implementation.
 *
 */
public class DataUtil
{
	// static
	
	private static final DataUtil _Instance = new DataUtil();

	/**
	 * Returns the Singleton instance of this class.
	 * 
	 * @return ConfigUtil
	 */
	public static final DataUtil getInstance()
	{
		return _Instance;
	}
	
	
	// private var's
	
	
	// constructors
	
	/**
	 * Default (private).
	 * 
	 */
	private DataUtil()
	{
		super();
	}
	
	
	// public methods
	
	public static String actuatorDataToJson(ActuatorData actuatorData)
	{
		// Use gson to convert ActuatorData to JSON
		Gson gson = new Gson();
		String jsonData = gson.toJson(actuatorData);
		return jsonData;
	}
	
	public static String sensorDataToJson(SensorData sensorData)
	{
		// Use gson to convert SensorData to JSON
		Gson gson = new Gson();
		String jsonData = gson.toJson(sensorData);
		return jsonData;
	}
	
	public static String systemPerformanceDataToJson(SystemPerformanceData sysPerfData)
	{
		// Use gson to convert SystemPerformanceData to JSON
		Gson gson = new Gson();
		String jsonData = gson.toJson(sysPerfData);
		return jsonData;
	}
	
	public String systemStateDataToJson(SystemStateData sysStateData)
	{
		// Use gson to convert SystemStateData to JSON
		Gson gson = new Gson();
		String jsonData = gson.toJson(sysStateData);
		return jsonData;
	}
	
	public static ActuatorData jsonToActuatorData(String jsonData)
	{
		// Convert JSON to an ActuatorData instance
		Gson gson = new Gson();
		ActuatorData actuatorData = gson.fromJson(jsonData, ActuatorData.class);
		return actuatorData;
	}
	
	public static SensorData jsonToSensorData(String jsonData)
	{
		// Convert JSON to an SensorData instance
		Gson gson = new Gson();
		SensorData sensorData = gson.fromJson(jsonData, SensorData.class);
		return sensorData;
	}
	
	public static SystemPerformanceData jsonToSystemPerformanceData(String jsonData)
	{
		// Convert JSON to an SystemPerformanceData instance
		Gson gson = new Gson();
		SystemPerformanceData sysPerfData = gson.fromJson(jsonData, SystemPerformanceData.class);
		return sysPerfData;
	}
	
	public static SystemStateData jsonToSystemStateData(String jsonData)
	{
		// Convert JSON to an SystemStateData instance
		Gson gson = new Gson();
		SystemStateData sysStateData = gson.fromJson(jsonData, SystemStateData.class);
		return sysStateData;
	}
	
}
