/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.io.Serializable;

import programmingtheiot.common.ConfigConst;

/**
 * Shell representation of class for student implementation.
 *
 */
public class SystemPerformanceData extends BaseIotData implements Serializable
{
	// static
	
	
	// private var's
	// CPU utilization, memory utilization, and disk utilization
	private float cpuUtil = 0.0f;
	private float memUtil = 0.0f;
	private float diskUtil = 0.0f;

	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public SystemPerformanceData()
	{
		super();
		
		super.setName(ConfigConst.SYS_PERF_DATA);
	}
	
	
	// public methods
	// Getter
	public float getCpuUtilization()
	{
		return this.cpuUtil;
	}
	
	public float getDiskUtilization()
	{
		return this.diskUtil;
	}
	
	public float getMemoryUtilization()
	{
		return this.memUtil;
	}
	// Setter
	public void setCpuUtilization(float val)
	{
		this.cpuUtil = val;
	}
	
	public void setDiskUtilization(float val)
	{
		this.diskUtil = val;
	}
	
	public void setMemoryUtilization(float val)
	{
		this.memUtil = val;
	}
	
	
	// protected methods
	
	/* (non-Javadoc)
	 * @see programmingtheiot.data.BaseIotData#handleToString()
	 */
	protected String handleToString()
	{
		return null;
	}
	
	/* (non-Javadoc)
	 * @see programmingtheiot.data.BaseIotData#handleUpdateData(programmingtheiot.data.BaseIotData)
	 */
	protected void handleUpdateData(BaseIotData data)
	{
		if (data.hasError() == false) {
			this.setCpuUtilization(((SystemPerformanceData) data).getCpuUtilization());
			this.setDiskUtilization(((SystemPerformanceData) data).getDiskUtilization());
			this.setMemoryUtilization(((SystemPerformanceData) data).getMemoryUtilization());
		}
	}
	
}
