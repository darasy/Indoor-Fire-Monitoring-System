#####
# 
# This class is part of the Programming the Internet of Things
# project, and is available via the MIT License, which can be
# found in the LICENSE file at the top level of this repository.
# 
# You may find it more helpful to your design to adjust the
# functionality, constants and interfaces (if there are any)
# provided within in order to meet the needs of your specific
# Programming the Internet of Things project.
# 

"""
Configuration and other constants for use when looking up
configuration values or when default values may be needed.
 
"""

#####
# General Names and Defaults
#

NOT_SET = 'Not Set'
DEFAULT_HOST             = 'localhost'
DEFAULT_COAP_PORT        = 5683
DEFAULT_COAP_SECURE_PORT = 5684
DEFAULT_MQTT_PORT        = 1883
DEFAULT_MQTT_SECURE_PORT = 8883
DEFAULT_KEEP_ALIVE       = 60
DEFAULT_POLL_CYCLES      = 60

PRODUCT_NAME = 'PIOT'
CLOUD        = 'Cloud'
GATEWAY      = 'Gateway'
CONSTRAINED  = 'Constrained'
DEVICE       = 'Device'
SERVICE      = 'Service'

CONSTRAINED_DEVICE = CONSTRAINED + DEVICE
GATEWAY_SERVICE    = GATEWAY + SERVICE
CLOUD_SERVICE      = CLOUD + SERVICE
DATA_GATEWAY_SERVICE = 'Data.GatewayService'

#####
# Resource and Topic Names
#

ACTUATOR_CMD    = 'ActuatorCmd'
ACTUATOR_RESPONSE = 'ActuatorResponse'
MGMT_STATUS_MSG = 'MgmtStatusMsg'
MGMT_STATUS_CMD = 'MgmtStatusCmd'
SENSOR_MSG      = 'SensorMsg'
SYSTEM_PERF_MSG = 'SystemPerfMsg'

CDA_ACTUATOR_CMD_MSG_RESOURCE = PRODUCT_NAME + '/' + CONSTRAINED_DEVICE + '/' + ACTUATOR_CMD
CDA_ACTUATOR_RESPONSE_MSG_RESOURCE = PRODUCT_NAME + '/' + CONSTRAINED_DEVICE + '/' + ACTUATOR_RESPONSE
CDA_MGMT_STATUS_MSG_RESOURCE  = PRODUCT_NAME + '/' + CONSTRAINED_DEVICE + '/' + MGMT_STATUS_MSG
CDA_MGMT_CMD_MSG_RESOURCE     = PRODUCT_NAME + '/' + CONSTRAINED_DEVICE + '/' + MGMT_STATUS_CMD
CDA_SENSOR_DATA_MSG_RESOURCE  = PRODUCT_NAME + '/' + CONSTRAINED_DEVICE + '/' + SENSOR_MSG
CDA_SYSTEM_PERF_MSG_RESOURCE  = PRODUCT_NAME + '/' + CONSTRAINED_DEVICE + '/' + SYSTEM_PERF_MSG

#####
# Configuration Sections, Keys and Defaults
#

# NOTE: You may need to update these paths if you change
# the directory structure for python-components
# INITIAL
DEFAULT_CONFIG_FILE_NAME = '../../../../../../../config/PiotConfig.props'
DEFAULT_CRED_FILE_NAME   = '../../../../../../../cred/PiotCred.props'
#DEFAULT_CONFIG_FILE_NAME = '/home/pi/workspace/constrained-device-app-rethd/config/PiotConfig.props'
#DEFAULT_CRED_FILE_NAME   = '/home/pi/workspace/constrained-device-app-rethd/cred/PiotCred.props'

TEST_GDA_DATA_PATH_KEY = 'testGdaDataPath'
TEST_CDA_DATA_PATH_KEY = 'testCdaDataPath'

LOCAL   = 'Local'
MQTT    = 'Mqtt'
COAP    = 'Coap'
OPCUA   = 'Opcua'
SMTP    = 'Smtp'

CLOUD_GATEWAY_SERVICE = CLOUD   + '.' + GATEWAY_SERVICE
COAP_GATEWAY_SERVICE  = COAP    + '.' + GATEWAY_SERVICE
MQTT_GATEWAY_SERVICE  = MQTT    + '.' + GATEWAY_SERVICE
OPCUA_GATEWAY_SERVICE = OPCUA   + '.' + GATEWAY_SERVICE
SMTP_GATEWAY_SERVICE  = SMTP    + '.' + GATEWAY_SERVICE

CRED_SECTION = "Credentials"

FROM_ADDRESS_KEY     = 'fromAddr'
TO_ADDRESS_KEY       = 'toAddr'
TO_MEDIA_ADDRESS_KEY = 'toMediaAddr'
TO_TXT_ADDRESS_KEY   = 'toTxtAddr'

HOST_KEY             = 'host'
PORT_KEY             = 'port'
SECURE_PORT_KEY      = 'securePort'

USER_NAME_TOKEN_KEY  = 'userToken'
USER_AUTH_TOKEN_KEY  = 'authToken'
API_TOKEN_KEY        = 'apiToken'

CERT_FILE_KEY        = 'certFile'
CRED_FILE_KEY        = 'credFile'
ENABLE_AUTH_KEY      = 'enableAuth'
ENABLE_CRYPT_KEY     = 'enableCrypt'
ENABLE_EMULATOR_KEY  = 'enableEmulator'
ENABLE_SENSE_HAT_KEY = 'enableSenseHAT'
ENABLE_LOGGING_KEY   = 'enableLogging'
USE_WEB_ACCESS_KEY   = 'useWebAccess'
POLL_CYCLES_KEY      = 'pollCycleSecs'
KEEP_ALIVE_KEY       = 'keepAlive'
DEFAULT_QOS_KEY      = 'defaultQos'

HUMIDITY_SIM_FLOOR_KEY   = 'humiditySimFloor'
HUMIDITY_SIM_CEILING_KEY = 'humiditySimCeiling'
PRESSURE_SIM_FLOOR_KEY   = 'pressureSimFloor'
PRESSURE_SIM_CEILING_KEY = 'pressureSimCeiling'
TEMP_SIM_FLOOR_KEY       = 'tempSimFloor'
TEMP_SIM_CEILING_KEY     = 'tempSimCeiling'

ENABLE_HANDLE_TEMP_CHANGE_ON_DEVICE_KEY = 'enableHandleTempChangeOnDevice'
TRIGGER_HVAC_TEMP_FLOOR_KEY = 'triggerHvacTempFloor'
TRIGGER_HVAC_TEMP_CEILING_KEY = 'triggerHvacTempCeiling'

LED_ACTUATOR_NAME = 'LedActuator'
HUMIDIFIER_ACTUATOR_NAME = 'HumidifierActuator'
HVAC_ACTUATOR_NAME = 'HvacActuator'
ALARM_ACTUATOR_NAME = 'FireAlarmActuator'
FIRE_PROBABILITY_THRESHOLD = "fireProbThreshold"


HUMIDITY_SENSOR_NAME = 'HumiditySensor'
PRESSURE_SENSOR_NAME = 'PressureSensor'
TEMP_SENSOR_NAME = 'TempSensor'
CAM_SENSOR_NAME = 'CameraSensor'

SYS_PERF_DATA = 'SysPerfData'

CPU_UTIL_NAME = 'CpuUtil'
DISK_UTIL_NAME = 'DiskUtil'
MEM_UTIL_NAME = 'MemUtil'
