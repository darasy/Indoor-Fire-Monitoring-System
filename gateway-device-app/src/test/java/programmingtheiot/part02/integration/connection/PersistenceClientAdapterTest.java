/**
 * 
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */ 

package programmingtheiot.part02.integration.connection;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.gda.connection.RedisPersistenceAdapter;

/**
 * This test case class contains very basic integration tests for
 * RedisPersistenceAdapter. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 *
 */
public class PersistenceClientAdapterTest
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(PersistenceClientAdapterTest.class.getName());
	
	
	// member var's
	
	private RedisPersistenceAdapter rpa = null;
	
	
	// test setup methods
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		this.rpa = new RedisPersistenceAdapter();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}
	
	// test methods
	
	/**
	 * Test method for {@link programmingtheiot.gda.connection.RedisPersistenceAdapter#connectClient()}.
	 */
	@Test
	public void testConnectClient()
	{
		assertTrue(this.rpa.connectClient());
		
		try {
			Thread.sleep(1000 + 5000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.rpa.disconnectClient());
	}
	
	/**
	 * Test method for {@link programmingtheiot.gda.connection.RedisPersistenceAdapter#disconnectClient()}.
	 */
	@Test
	public void testDisconnectClient()
	{
		assertTrue(this.rpa.connectClient());
		
		try {
			Thread.sleep(1000 + 5000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.rpa.disconnectClient());
	}
	
	/**
	 * Test method for {@link programmingtheiot.gda.connection.RedisPersistenceAdapter#getActuatorData(java.lang.String, java.util.Date, java.util.Date)}.
	 */
	//@Test
	public void testGetActuatorData()
	{
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for {@link programmingtheiot.gda.connection.RedisPersistenceAdapter#getSensorData(java.lang.String, java.util.Date, java.util.Date)}.
	 */
	//@Test
	public void testGetSensorData()
	{
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for {@link programmingtheiot.gda.connection.RedisPersistenceAdapter#storeData(java.lang.String, int, programmingtheiot.data.ActuatorData[])}.
	 */
	//@Test
	public void testStoreDataStringIntActuatorDataArray()
	{
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for {@link programmingtheiot.gda.connection.RedisPersistenceAdapter#storeData(java.lang.String, int, programmingtheiot.data.SensorData[])}.
	 */
	@Test
	public void testStoreDataStringIntSensorData()
	{
		SensorData sensorData = new SensorData(SensorData.TEMP_SENSOR_TYPE);
		sensorData.setValue(23.0f);
		sensorData.setName("Temperature");
		sensorData.setStateData("TEST: This is the sensor data");

		
		
		this.rpa.connectClient();
		this.rpa.storeData(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName(), 0, sensorData);
		sensorData.setValue(25.0f);
		sensorData.setName("Temperature");
		sensorData.setStateData("TEST: This is the sensor data");
		this.rpa.storeData(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName(), 0, sensorData);
		
		try {
			Thread.sleep(1000 + 5000);
		} catch (Exception e) {
			// ignore
		}
		
		this.rpa.disconnectClient();
	}
	
	/**
	 * Test method for {@link programmingtheiot.gda.connection.RedisPersistenceAdapter#storeData(java.lang.String, int, programmingtheiot.data.SystemPerformanceData[])}.
	 */
	@Test
	public void testStoreDataStringIntSystemPerformanceDataArray()
	{
		SystemPerformanceData sysPerfData = new SystemPerformanceData();
		sysPerfData.setCpuUtilization(2.0f);
		sysPerfData.setMemoryUtilization(3.0f);
		sysPerfData.setDiskUtilization(34.0f);
		sysPerfData.setStateData("TEST: This is the system performance data 1");
		
		this.rpa.connectClient();
		this.rpa.storeData(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName(), 0, sysPerfData);
		sysPerfData.setCpuUtilization(12.0f);
		sysPerfData.setMemoryUtilization(23.0f);
		sysPerfData.setDiskUtilization(31.0f);
		sysPerfData.setStateData("TEST: This is the system performance data 2");
		this.rpa.storeData(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName(), 0, sysPerfData);
		
		try {
			Thread.sleep(1000 + 5000);
		} catch (Exception e) {
			// ignore
		}
		
		this.rpa.disconnectClient();
	}
	
}
