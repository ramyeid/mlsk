import os
import common_constants as constants
import launch_common as common

def get_absolute_path_logs() -> str:
  return os.path.abspath(constants.LOGS_DIRECTORY)

def get_absolute_engine_path() -> str:
  return os.path.abspath(constants.COMPONENTS_DIRECTORY + constants.ENGINE_DIRECTORY)

if __name__== "__main__":
  if (common.configuration_file_exists()):
    project_information = common.read_project_information()
    service_port = project_information[constants.SERVICE_PORT_OPTION]
    engine_ports = project_information[constants.ENGINE_PORTS_OPTION]

    print ("launching service ")
    print (" with port: " + service_port)
    print (" and engine ports: " + engine_ports)
    print (" and logs path: " + get_absolute_path_logs())
    print (" and engine path: " + get_absolute_engine_path())

    os.system("java -Dserver.port=" + service_port + " -jar " + constants.COMPONENTS_DIRECTORY + "service.jar --engine-ports " + engine_ports + " --logs-path " + get_absolute_path_logs() + " --engine-path " + get_absolute_engine_path())
  else:
    print ("ERROR: Please make sure configuration file: " + constants.CONFIGURATION_FILE + " exists")