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

import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.WebLink;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.DefaultDataMessageListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemStateData;
import programmingtheiot.gda.connection.*;

/**
 * This test case class contains very basic integration tests for
 * CoapClientToServerConnectorTest. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 *
 */
public class CoapClientPerformanceTest
{
	// NOTE: We'll use only 10,000 requests for CoAP
		public static final int MAX_TEST_RUNS = 10000;
		
		private CoapClientConnector coapClient = null;
		
		public static final int DEFAULT_TIMEOUT = 300 * 10000;
		public static final boolean USE_DEFAULT_RESOURCES = true;
		private static final Logger _Logger =
				Logger.getLogger(CoapClientConnectorTest.class.getName());

		@Before
		public void setUp() throws Exception
		{
			this.coapClient = new CoapClientConnector();
		}
		@Ignore
		@Test
		public void testPostRequestCon()
		{
			execTestPost(MAX_TEST_RUNS, true);
		}
		//@Ignore
		@Test
		public void testPostRequestNon()
		{
			execTestPost(MAX_TEST_RUNS, false);
		}
		
		@Ignore
		@Test
		public void testPutRequestCon()
		{
			execTestPut(MAX_TEST_RUNS, true);
		}
		@Ignore
		@Test
		public void testPutRequestNon()
		{
			execTestPut(MAX_TEST_RUNS, false);
		}
		
		private void execTestPost(int maxTestRuns, boolean enableCON)
		{
			SensorData sd = new SensorData();
			String payload = DataUtil.getInstance().sensorDataToJson(sd);
			
			long startMillis = System.currentTimeMillis();
			
			for (int seqNo = 0; seqNo < maxTestRuns; seqNo++) {
				this.coapClient.sendPostRequest(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, enableCON, payload, DEFAULT_TIMEOUT);
			}
			
			long endMillis = System.currentTimeMillis();
			long elapsedMillis = endMillis - startMillis;
			
			_Logger.info("POST message - useCON " + enableCON + " [" + maxTestRuns + "]: " + elapsedMillis + " ms");
		}
		
		private void execTestPut(int maxTestRuns, boolean enableCON)
		{
			SensorData sd = new SensorData();
			String payload = DataUtil.getInstance().sensorDataToJson(sd);
			
			long startMillis = System.currentTimeMillis();
			
			for (int seqNo = 0; seqNo < maxTestRuns; seqNo++) {
				this.coapClient.sendPutRequest(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, enableCON, payload, DEFAULT_TIMEOUT);
			}
			
			long endMillis = System.currentTimeMillis();
			long elapsedMillis = endMillis - startMillis;
			
			_Logger.info("PUT message - useCON " + enableCON + " [" + maxTestRuns + "]: " + elapsedMillis + " ms");
		}
}
