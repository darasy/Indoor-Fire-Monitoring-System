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
public class ActuatorData extends BaseIotData implements Serializable
{
	// static
	public static final int DEFAULT_COMMAND = 0;
	public static final int COMMAND_OFF = DEFAULT_COMMAND;
	public static final int COMMAND_ON = 1;
	
	private int actuatorType = 2;
	
	// private var's
	private float curValue = 0.0f;
	private int command = DEFAULT_COMMAND;
    
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public ActuatorData()
	{
		super();
	}
	
	
	// public methods
	// Getter
	public int getCommand()
	{
		return this.command;
	}
	
	public float getValue()
	{
		return this.curValue;
	}
	// Setter
	public void setCommand(int command)
	{
		this.command = command;
	}
	
	public void setValue(float val)
	{
		this.curValue = val;
	}
	public int getActuatorType() 
	{
		return this.actuatorType;
	}
	public void setActuatorType(int actuatorType)
	{
		this.actuatorType = actuatorType;
	}
	
	// protected methods
	
	/* (non-Javadoc)
	 * @see programmingtheiot.data.BaseIotData#handleUpdateData(programmingtheiot.data.BaseIotData)
	 */
	protected void handleUpdateData(BaseIotData data)
	{
		if (data.hasError() == false) {
			this.setCommand(((ActuatorData) data).getCommand());
			this.setValue(((ActuatorData) data).getValue());
		}
	}
	
}
