#!/usr/bin/python3

import configparser
from lib import file_helper
from packaging import constants as const


def configuration_file_exists() -> bool:
  return file_helper.does_file_exist(const.CONFIGURATION_FILE)


def read_service_port() -> str:
  project_information = read_project_information()
  return project_information[const.SERVICE_PORT_OPTION]


def read_engine_ports() -> str:
  project_information = read_project_information()
  return project_information[const.ENGINE_PORTS_OPTION]


def read_web_ui_port() -> str:
  project_information = read_project_information()
  return project_information[const.WEB_UI_PORT_OPTION]


def read_project_information() -> dict:
  config_parser = configparser.ConfigParser()
  config_parser.read(const.CONFIGURATION_FILE)
  return {
    const.SERVICE_PORT_OPTION: config_parser.get(const.SERVICE_SECTION, const.PORT_SECTION),
    const.ENGINE_PORTS_OPTION: config_parser.get(const.ENGINE_SECTION, const.PORTS_SECTION),
    const.WEB_UI_PORT_OPTION:  config_parser.get(const.WEB_UI_SECTION, const.PORT_SECTION)
  }
