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
import java.util.Date;
/**
 * This test case class contains very basic integration tests for
 * CloudClientConnector. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 *
 */
public class AWSS3ClientConnectorTest
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(AWSS3ClientConnectorTest.class.getName());
	
	
	// member var's
	
	private AmazonS3ClientConnector s3Client = null;
	
	
	// test setup methods
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		ConfigUtil.getInstance();
		this.s3Client = new AmazonS3ClientConnector();
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
	 * Test method 
	 */
	@Test
	public void testConnectAndDisconnect()
	{
		int delay = ConfigUtil.getInstance().getInteger(ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
		SensorData sensorData = new SensorData(SensorData.TEMP_SENSOR_TYPE);
		sensorData.setValue(23.0f);
		sensorData.setName("Temperature");
		sensorData.setStateData("TEST: This is the sensor data");
		sensorData.setTimeStamp("123456456");
		String payload = DataUtil.sensorDataToJson(sensorData);
		
		assertTrue(this.s3Client.connectClient());
		assertTrue(this.s3Client.putData(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sensorData));
	}
	
	
}
