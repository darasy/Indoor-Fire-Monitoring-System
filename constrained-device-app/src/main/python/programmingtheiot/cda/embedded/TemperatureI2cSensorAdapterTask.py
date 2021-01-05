#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

import logging, smbus

from programmingtheiot.data.SensorData import SensorData
from programmingtheiot.cda.sim.BaseSensorSimTask import BaseSensorSimTask
from programmingtheiot.cda.sim.SensorDataGenerator import SensorDataGenerator
from sense_hat import SenseHat

class TemperatureI2cSensorAdapterTask(BaseSensorSimTask):
    """
    Shell representation of class for student implementation.
    
    """

    def __init__(self):
        super(TemperatureI2cSensorAdapterTask, self).__init__(SensorData.TEMP_SENSOR_TYPE, minVal = SensorDataGenerator.LOW_NORMAL_INDOOR_TEMP, maxVal = SensorDataGenerator.HI_NORMAL_INDOOR_TEMP)
        self.sensorType = SensorData.TEMP_SENSOR_TYPE
        
        # Example only: Read the spec for the SenseHAT temperature sensor to obtain the appropriate starting address, and use i2c-tools to verify.
        self.tempAddr = 0x5F

        # init the I2C bus at the temperature address
        # WARNING: only use I2C bus 1 when working with the SenseHAT on the Raspberry Pi!!
        self.i2cBus = smbus.SMBus(1)
        self.i2cBus.write_byte_data(self.tempAddr, 0, 0)
        self.sense = SenseHat()
    
    def generateTelemetry(self) -> SensorData:
        sd = SensorData(self.sensorType)
        sd.setValue(self.sense.get_temperature())
        self.latestSensorData = sd
        return self.latestSensorData
    
    def getTelemetryValue(self) -> float:
        pass
    