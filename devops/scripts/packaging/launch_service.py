#!/usr/bin/python3

from lib import helper
from lib import file_helper
from lib import constants as lib_const
from packaging import constants as const
from packaging import config_helper


if __name__ == '__main__':
  if config_helper.configuration_file_exists():
    service_port = config_helper.read_service_port()
    engine_ports = config_helper.read_engine_ports()
    logs_path = file_helper.get_absolute_path(const.LOGS_DIRECTORY)
    engine_path = file_helper.get_absolute_path(const.COMPONENTS_ENGINE_DIRECTORY)

    helper.print_inner_step('launching service', 0)
    helper.print_inner_step('with port: {}'.format(service_port), 1)
    helper.print_inner_step('and engine ports: {}'.format(engine_ports), 1)
    helper.print_inner_step('and logs path: {}'.format(logs_path), 1)
    helper.print_inner_step('and engine path: {}'.format(engine_path), 1)

    helper.launch_command('java -Dserver.port={} -jar {}{} \
                          --engine-ports {} --logs-path {} --engine-path {}'
                          .format(service_port, const.COMPONENTS_DIRECTORY, lib_const.SERVICE_JAR,
                                  engine_ports, logs_path, engine_path),
                          'Service stopped')
  else:
    helper.print_inner_step('ERROR: Please make sure configuration file: {} exists'.format(const.CONFIGURATION_FILE), 0)
