#!/usr/bin/python3

#Configuration file
HOST_SECTION = 'host'
PORT_SECTION = 'port'
PORTS_SECTION = 'ports'
SERVICE_SECTION = 'SERVICE'
ENGINE_SECTION = 'ENGINE'
WEB_UI_SECTION = 'WEB_UI'
WEB_UI_PORT_OPTION = 'webui_port'
ENGINE_PORTS_OPTION = 'engine_ports'
SERVICE_PORT_OPTION = 'service_port'
SERVICE_HOST_OPTION = 'service_host'

#Paths
BUILD_DIRECTORY = '../../build/'
COMPONENTS_DIRECTORY = 'components/'
BUILD_COMPONENTS_DIRECTORY = '{}{}'.format(BUILD_DIRECTORY, COMPONENTS_DIRECTORY)
LOGS_DIRECTORY = 'logs/'
BUILD_LOGS_DIRECTORY = '{}{}'.format(BUILD_DIRECTORY, LOGS_DIRECTORY)
ENGINE_DIRECTORY = 'engine/'
COMPONENTS_ENGINE_DIRECTORY = '{}{}'.format(COMPONENTS_DIRECTORY, ENGINE_DIRECTORY)
BUILD_COMPONENTS_ENGINE_DIRECTORY = '{}{}'.format(BUILD_COMPONENTS_DIRECTORY, ENGINE_DIRECTORY)
WEB_UI_DIRECTORY = 'web-ui/'
DIST_DIRECTORY = 'dist/machine-learning-swissknife'
COMPONENTS_WEB_UI_DIST_DIRECTORY = '{}{}{}'.format(COMPONENTS_DIRECTORY, WEB_UI_DIRECTORY, DIST_DIRECTORY)
BUILD_COMPONENTS_WEB_UI_DIST_DIRECTORY = '{}{}{}'.format(BUILD_COMPONENTS_DIRECTORY, WEB_UI_DIRECTORY, DIST_DIRECTORY)
LIB_DIRECTORY = 'lib'
BUILD_LIB_DIRECTORY = '{}{}'.format(BUILD_DIRECTORY, LIB_DIRECTORY)
PACKAGING_DIRECTORY = 'packaging/'
BUILD_PACKAGING_DIRECTORY = '{}{}'.format(BUILD_DIRECTORY, PACKAGING_DIRECTORY)

#Files
CONFIGURATION_FILE = 'packaging/mlsk.ini'
