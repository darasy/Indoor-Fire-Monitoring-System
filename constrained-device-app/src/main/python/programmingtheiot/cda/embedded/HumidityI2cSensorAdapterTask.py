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

class HumidityI2cSensorAdapterTask(BaseSensorSimTask):
    """
    Shell representation of class for student implementation.
    
    """

    def __init__(self):
        super(HumidityI2cSensorAdapterTask, self).__init__(SensorData.HUMIDITY_SENSOR_TYPE, minVal = SensorDataGenerator.LOW_NORMAL_ENV_HUMIDITY, maxVal = SensorDataGenerator.HI_NORMAL_ENV_HUMIDITY)
        self.sensorType = SensorData.HUMIDITY_SENSOR_TYPE

        # Example only: Read the spec for the SenseHAT humidity sensor to obtain the appropriate starting address, and use i2c-tools to verify.
        self.humidAddr = 0x5F

        # init the I2C bus at the humidity address
        # WARNING: only use I2C bus 1 when working with the SenseHAT on the Raspberry Pi!!
        self.i2cBus = smbus.SMBus(1)
        self.i2cBus.write_byte_data(self.humidAddr, 0, 0)
        self.sense = SenseHat()
    
    def generateTelemetry(self) -> SensorData:
        sd = SensorData(self.sensorType)
        sd.setValue(self.sense.get_humidity())
        self.latestSensorData = sd
        return self.latestSensorData
    
    def getTelemetryValue(self) -> float:
        pass
    