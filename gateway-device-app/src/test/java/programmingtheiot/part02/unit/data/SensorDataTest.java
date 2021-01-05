/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */ 

package programmingtheiot.part02.unit.data;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.data.SensorData;

/**
 * This test case class contains very basic unit tests for
 * SensorDataTest. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 *
 */
public class SensorDataTest
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(SensorDataTest.class.getName());
	
	public static final String DEFAULT_NAME = "SensorDataFooBar";
	public static final float DEFAULT_VAL = 10.0f;
	
	
	// member var's
	
	
	// test setup methods
	
	@Before
	public void setUp() throws Exception
	{
	}
	
	@After
	public void tearDown() throws Exception
	{
	}
	
	
	// test methods
	
	@Test
	public void testDefaultValues()
	{
		SensorData ssd = new SensorData();
		
		assertEquals(ssd.getName(), ConfigConst.NOT_SET);
		assertEquals(ssd.getStatusCode(), SensorData.DEFAULT_STATUS);
		assertTrue(ssd.getValue() == SensorData.DEFAULT_VAL);
	}
	
	@Test
	public void testParameterUpdates()
	{
		SensorData ssd = createTestData();
		
		assertEquals(ssd.getName(), DEFAULT_NAME);
		assertEquals(ssd.getStatusCode(), SensorData.DEFAULT_STATUS);
		assertTrue(ssd.getValue() == DEFAULT_VAL);
	}
	
	@Test
	public void testFullUpdate()
	{
		SensorData ssd = new SensorData();
		SensorData ssd2 = createTestData();

		assertEquals(ssd.getName(), ConfigConst.NOT_SET);
		assertEquals(ssd.getStatusCode(), SensorData.DEFAULT_STATUS);
		assertTrue(ssd.getValue() == SensorData.DEFAULT_VAL);
		
		ssd.updateData(ssd2);
		
		assertEquals(ssd.getName(), DEFAULT_NAME);
		assertEquals(ssd.getStatusCode(), SensorData.DEFAULT_STATUS);
		assertTrue(ssd.getValue() == DEFAULT_VAL);
	}
	
	
	// private
	
	private SensorData createTestData()
	{
		SensorData ssd = new SensorData();
		ssd.setName(DEFAULT_NAME);
		ssd.setValue(DEFAULT_VAL);
		
		return ssd;
	}
	
}
