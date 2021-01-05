/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.system;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.DefaultDataMessageListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.data.SystemStateData;
import programmingtheiot.gda.app.GatewayDeviceApp;

import java.util.logging.Logger;
import java.time.LocalTime;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemPerformanceManager
{
	// private var's
	private static final Logger _Logger =
			Logger.getLogger(SystemPerformanceManager.class.getName());
	
	private int pollSecs = 30;
	
	private ScheduledExecutorService schedExecSvc = null;
	private SystemCpuUtilTask cpuUtilTask = null;
	private SystemMemUtilTask memUtilTask = null;
	private SystemDiskUtilTask diskUtilTask = null;

	private Runnable taskRunner = null;
	private boolean isStarted = false;

	private float cpuUtilVal;
	private float diskUtilVal;
	private float memUtilVal;
	
	private IDataMessageListener dmgListener = null;
	
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public SystemPerformanceManager()
	{
		this(ConfigConst.DEFAULT_POLL_CYCLES);
		
		this.schedExecSvc = Executors.newScheduledThreadPool(1);
		this.cpuUtilTask = new SystemCpuUtilTask();
		this.memUtilTask = new SystemMemUtilTask();
		this.diskUtilTask = new SystemDiskUtilTask();
		this.dmgListener = new DefaultDataMessageListener();
		this.taskRunner = () -> {
		    this.handleTelemetry();
		};
		
	}
	
	/**
	 * Constructor.
	 * 
	 * @param pollSecs The number of seconds between each scheduled task poll.
	 */
	public SystemPerformanceManager(int pollSecs)
	{
		if (pollSecs > 1 && pollSecs < Integer.MAX_VALUE)
			this.pollSecs = pollSecs;
		
		this.schedExecSvc = Executors.newScheduledThreadPool(1);
		this.cpuUtilTask = new SystemCpuUtilTask();
		this.memUtilTask = new SystemMemUtilTask();
		this.diskUtilTask = new SystemDiskUtilTask();
		this.dmgListener = new DefaultDataMessageListener();
		this.taskRunner = () -> {
		    this.handleTelemetry();
		};
		
	}
	
	
	// public methods
	
	public void handleTelemetry()
	{
		this.cpuUtilVal = this.cpuUtilTask.getTelemetryValue();
		this.memUtilVal = this.memUtilTask.getTelemetryValue();
		this.diskUtilVal = this.diskUtilTask.getTelemetryValue();
		
		_Logger.info("Handle telemetry results: cpuUtil = " + this.cpuUtilVal  + ", memUtil = " + this.memUtilVal + ", diskUtilVal = " + this.diskUtilVal);
		SystemPerformanceData data = new SystemPerformanceData();
		data.setCpuUtilization(this.cpuUtilVal);
		data.setMemoryUtilization(this.memUtilVal);
		data.setDiskUtilization(this.diskUtilVal);
		LocalTime myObj = LocalTime.now();
		data.setTimeStamp(myObj.toString());
		this.dmgListener.handleSystemPerformanceMessage(ResourceNameEnum.GDA_SYSTEM_PERF_MSG_RESOURCE, data);
	}
	
	public void setDataMessageListener(IDataMessageListener listener)
	{
		this.dmgListener = listener;
	}
	
	public void startManager()
	{
		_Logger.info("SytemPerformanceManager is starting...");
		if (! this.isStarted) {
		    ScheduledFuture<?> futureTask = this.schedExecSvc.scheduleAtFixedRate(this.taskRunner, 0L, this.pollSecs, TimeUnit.SECONDS);

		    this.isStarted = true;
		}
	}
	
	public void stopManager()
	{
		_Logger.info("SytemPerformanceManager is stopped.");
		this.schedExecSvc.shutdown();
	}
	
}
