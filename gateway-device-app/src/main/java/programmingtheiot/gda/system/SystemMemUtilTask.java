/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.system;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.logging.Logger;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemMemUtilTask extends BaseSystemUtilTask
{
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public SystemMemUtilTask()
	{
		super();
	}
	private static final Logger _Logger =
			Logger.getLogger(BaseSystemUtilTask.class.getName());
	
	// protected methods
	
	@Override
	protected float getSystemUtil()
	{
		MemoryUsage memUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		 _Logger.info("Memory used: " + memUsage.getUsed());
	
		 _Logger.info("Memory avail: " + memUsage.getMax());
		 double val = ((double) memUsage.getUsed() / (double) memUsage.getMax()) * 100.0d;
		 _Logger.info("Memory used %: " + val);
		return (float) val;
	}
	
}
