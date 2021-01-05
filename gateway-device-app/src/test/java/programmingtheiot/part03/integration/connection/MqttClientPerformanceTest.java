/**
 * 
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */ 

package programmingtheiot.part03.integration.connection;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.DefaultDataMessageListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.gda.connection.*;

/**
 * This test case class contains very basic integration tests for
 * MqttClientConnectorTest. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 *
 */
public class MqttClientPerformanceTest
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(MqttClientPerformanceTest.class.getName());
	
	
	// member var's
	
	private MqttClientConnector mqttClient = null;
	
	
	// test setup methods
	// test methods
	
	/**
	 * Test method for {@link programmingtheiot.gda.connection.MqttClientConnector#connectClient()}.
	 */
	// NOTE: We'll use only 10,000 requests for MQTT
	public static final int MAX_TEST_RUNS = 10000;
	
	@Before
	public void setUp() throws Exception
	{
		this.mqttClient = new MqttClientConnector();
	}
	//@Ignore
	@Test
	public void testConnectAndDisconnect()
	{
		long startMillis = System.currentTimeMillis();
		
		assertTrue(this.mqttClient.connectClient());
		assertTrue(this.mqttClient.disconnectClient());
		
		long endMillis = System.currentTimeMillis();
		long elapsedMillis = endMillis - startMillis;
		
		_Logger.info("Connect and Disconnect: " + elapsedMillis + " ms");
	}
	@Ignore
	@Test
	public void testPublishQoS0()
	{
		execTestPublish(MAX_TEST_RUNS, 0);
	}
	@Ignore
	@Test
	public void testPublishQoS1()
	{
		execTestPublish(MAX_TEST_RUNS, 1);
	}
	@Ignore
	@Test
	public void testPublishQoS2()
	{
		execTestPublish(MAX_TEST_RUNS, 2);
	}
	
	private void execTestPublish(int maxTestRuns, int qos)
	{
		assertTrue(this.mqttClient.connectClient());
		
		SensorData sensorData = new SensorData();
		
		String payload = DataUtil.getInstance().sensorDataToJson(sensorData);
		
		long startMillis = System.currentTimeMillis();
		
		for (int sequenceNo = 0; sequenceNo < maxTestRuns; sequenceNo++) {
			this.mqttClient.publishMessage(ResourceNameEnum.CDA_MGMT_STATUS_CMD_RESOURCE, payload, qos);
		}
		
		long endMillis = System.currentTimeMillis();
		long elapsedMillis = endMillis - startMillis;
		
		assertTrue(this.mqttClient.disconnectClient());
		
		_Logger.info("Publish message - QoS " + qos + " [" + maxTestRuns + "]: " + elapsedMillis + " ms");
	}
	
}
