/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.io.Serializable;

/**
 * Shell representation of class for student implementation.
 *
 */
public class SensorData extends BaseIotData implements Serializable
{
	// static
	public static final int DEFAULT_SENSOR_TYPE = 0;
	
	// private var's
	private float value = 0.0f;
	
	public static int HUMIDITY_SENSOR_TYPE = 1;
	public static int PRESSURE_SENSOR_TYPE = 2;
	public static int TEMP_SENSOR_TYPE = 3;
	public static int CAM_SENSOR_TYPE = 4;
	
	public double MIN_INDOOR_TEMP = 15.0;
	public double MAX_INDOOR_TEMP = 25.0;
	
	public double MIN_ENV_HUMIDITY = 0.0;
	public double MAX_ENV_HUMIDITY = 100.0;
	
	public double MIN_ENV_PRESSURE = 500.0;
	public double MAX_ENV_PRESSURE = 1500.0;


	public int sensorType = this.TEMP_SENSOR_TYPE;
	
    
	// constructors
	public SensorData()
	{
		super();
	}
	
	public SensorData(int sensorType)
	{
		super();
		this.sensorType = sensorType;
	}
	
	
	// public methods
	// Getter
	public float getValue()
	{
		return this.value;
	}
	// Setter
	public void setValue(float val)
	{
		this.value = val;
	}

	
	
	// protected methods
	
	/* (non-Javadoc)
	 * @see programmingtheiot.data.BaseIotData#handleUpdateData(programmingtheiot.data.BaseIotData)
	 */
	protected void handleUpdateData(BaseIotData data)
	{
		if (data.hasError() == false) {
			this.setValue(((SensorData) data).getValue());
		}
	}
	
	
	public boolean withinThreshold() {
		if (this.sensorType == this.TEMP_SENSOR_TYPE) {
			if ((this.getValue() < this.MIN_INDOOR_TEMP) || (this.getValue() > this.MAX_INDOOR_TEMP)) {
				return false;
			}
		}
		if (this.sensorType == this.HUMIDITY_SENSOR_TYPE) {
			if ((this.getValue() < this.MIN_ENV_HUMIDITY) || (this.getValue() > this.MAX_ENV_HUMIDITY)) {
				return false;
			}
		}
		if (this.sensorType == this.PRESSURE_SENSOR_TYPE) {
			if ((this.getValue() < this.MIN_ENV_PRESSURE) || (this.getValue() > this.MAX_ENV_PRESSURE)) {
				return false;
			}
		}
		return true;
	}
	
}
