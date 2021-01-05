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
 * CloudClientConnector. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 *
 */
public class CloudClientConnectorTest
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(CloudClientConnectorTest.class.getName());
	
	
	// member var's
	
	private CloudClientConnector cloudClient = null;
	
	
	// test setup methods
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		ConfigUtil.getInstance();
		this.cloudClient = new CloudClientConnector();
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
	 * Test method for {@link programmingtheiot.gda.connection.CloudClientConnector#connectClient()}.
	 */
	//@Test
	public void testConnectAndDisconnect()
	{
		int delay = ConfigUtil.getInstance().getInteger(ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
		
		assertTrue(this.cloudClient.connectClient());
		//assertFalse(this.cloudClient.connectClient());
		
		try {
			Thread.sleep(delay * 1000 + 5000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.cloudClient.disconnectClient());
		//assertFalse(this.cloudClient.disconnectClient());
	}
	
	/**
	 * Test method for {@link programmingtheiot.gda.connection.CloudClientConnector#publishMessage(programmingtheiot.common.ResourceNameEnum, java.lang.String, int)}.
	 */
	//@Test
	public void testPublishAndSubscribe()
	{
		int qos = 1;
		int delay = ConfigUtil.getInstance().getInteger(ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
		
		IDataMessageListener listener = new DefaultDataMessageListener();
		
		assertTrue(this.cloudClient.connectClient());
		assertTrue(this.cloudClient.subscribeToTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, qos));
		
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.cloudClient.publishMessage(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, "TEST: This is the GDA message payload.", qos));
		
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.cloudClient.unsubscribeFromTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE));

		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			// ignore
		}

		try {
			Thread.sleep(delay * 1000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.cloudClient.disconnectClient());
	}
	
	
	// Test 1
	//@Test
	public void test1()
	{
		int qos = 1;
		int delay = ConfigUtil.getInstance().getInteger(ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
				
		assertTrue(this.cloudClient.connectClient());
		
		SensorData sensorData = new SensorData(SensorData.TEMP_SENSOR_TYPE);
		sensorData.setValue(23.0f);
		sensorData.setName("Temperature");
		sensorData.setStateData("TEST: This is the sensor data");
		String payload = DataUtil.sensorDataToJson(sensorData);
		assertTrue(this.cloudClient.publishMessage(ResourceNameEnum.CDA_SENSOR_TEMP, payload, qos));

		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			// ignore
		}

		try {
			Thread.sleep(delay * 1000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.cloudClient.disconnectClient());
	}
	
	// Test 2
	//@Test
	public void test2()
	{
		int qos = 1;
		int delay = ConfigUtil.getInstance().getInteger(ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
				
		assertTrue(this.cloudClient.connectClient());
		assertTrue(this.cloudClient.subscribeToEdgeEvents(ResourceNameEnum.UBIDOT_ACTUATOR_LED));
		
		SensorData sensorData = new SensorData(SensorData.TEMP_SENSOR_TYPE);
		sensorData.setValue(20.0f);
		sensorData.setName("Temperature");
		sensorData.setStateData("TEST: This is the sensor data");
		String payload = DataUtil.sensorDataToJson(sensorData);
		assertTrue(this.cloudClient.publishMessage(ResourceNameEnum.CDA_SENSOR_TEMP, payload, qos));
		
		Random rd = new Random();
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			// ignore
		}
		for (int i = 0; i < 30; i++)  {
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				// ignore
			}
			sensorData.setValue(30 + rd.nextFloat() * (40 - 30));
			payload = DataUtil.sensorDataToJson(sensorData);
			assertTrue(this.cloudClient.publishMessage(ResourceNameEnum.CDA_SENSOR_TEMP, payload, qos));
		}
		
		try {
			Thread.sleep(delay * 1000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.cloudClient.disconnectClient());
	}
	
	// Test 3
	@Test
	public void test3()
	{
		boolean enableMqtt = true;
		boolean enableCoap = false;
		boolean enableCloud = true;
		boolean enableSmtp = false;
		boolean enablePersistence = true;

		DeviceDataManager devDataMgr =
			new DeviceDataManager(enableMqtt, enableCoap, enableCloud, enableSmtp, enablePersistence);
		
		devDataMgr.startManager();
		
		try {
			Thread.sleep(600000000L);
		} catch (InterruptedException e) {
			// ignore
		}
		
		devDataMgr.stopManager();
	}
}
