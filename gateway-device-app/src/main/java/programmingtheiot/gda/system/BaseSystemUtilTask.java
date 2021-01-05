/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.system;

import java.util.logging.Logger;

import programmingtheiot.data.SensorData;

/**
 *
 */
public abstract class BaseSystemUtilTask
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(BaseSystemUtilTask.class.getName());
	
	// private
	private SensorData latestSensorData = null;
	
	// constructors
	
	public BaseSystemUtilTask()
	{
		super();
	}
	
	
	// public methods
	
	public SensorData generateTelemetry()
	{
		// Create a new SensorData instance and 
		SensorData sd = new SensorData();
		// Set the value of latestSensorData to be the return value of getSystemUtil()
		sd.setValue(getSystemUtil());
		// Set new SensorData as the latestSensorData reference
		this.latestSensorData = sd;
		// Return the latestSensorData
		return this.latestSensorData;
	}
	
	public float getTelemetryValue()
	{
		if (this.latestSensorData == null) this.generateTelemetry();
		float val = this.generateTelemetry().getValue();
		_Logger.info("Get telemetry value = " + val);
		return val;
	}
	
	
	// protected methods
	
	/**
	 * Template method definition. Sub-class will implement this to retrieve
	 * the system utilization measure.
	 * 
	 * @return float
	 */
	protected abstract float getSystemUtil();
	
}
