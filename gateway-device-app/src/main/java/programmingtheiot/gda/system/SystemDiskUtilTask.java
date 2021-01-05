/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.system;

import java.io.File;
import java.lang.management.ManagementFactory;


/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemDiskUtilTask extends BaseSystemUtilTask
{
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public SystemDiskUtilTask()
	{
		super();
	}
	
	
	// protected methods
	
	@Override
	protected float getSystemUtil()
	{
		File cDrive = new File("/dev/sda5");
		return (float) cDrive.getUsableSpace() /1073741824;
	}
	
}
