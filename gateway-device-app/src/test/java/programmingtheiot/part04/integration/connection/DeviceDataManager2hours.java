/**
 * 
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */ 

package programmingtheiot.part04.integration.connection;
import java.util.Random;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.DefaultDataMessageListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.gda.app.DeviceDataManager;
import programmingtheiot.gda.connection.*;

/**
 * This test case class contains very basic integration tests for
 * DeviceDataManager. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 *
 */
public class DeviceDataManager2hours
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(DeviceDataManager2hours.class.getName());
	
	// test setup methods
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		ConfigUtil.getInstance();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}
	
	// Run device data manager for 2 hours
	@Test
	public void GDA2HourRun()
	{
		boolean enableMqtt = true;
		boolean enableCoap = false;
		boolean enableCloud = true;
		boolean enableSmtp = true;
		boolean enablePersistence = true;

		DeviceDataManager devDataMgr =
			new DeviceDataManager(enableMqtt, enableCoap, enableCloud, enableSmtp, enablePersistence);
		
		devDataMgr.startManager();
		
		try {
			Thread.sleep(122 * 60 * 1000);
		} catch (InterruptedException e) {
			// ignore
		}
		
		devDataMgr.stopManager();
	}
}
