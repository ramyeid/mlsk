import os
import common_constants as constants
import launch_common as common


if __name__== "__main__":
  if (common.configuration_file_exists()):
    project_information = common.read_project_information()
    service_port = project_information[constants.SERVICE_PORT_OPTION]
    engine_ports = project_information[constants.ENGINE_PORTS_OPTION]

    print ("launching service  with port: " + service_port + " and engine ports: " + engine_ports)
    os.system("java -Dserver.port=" + service_port + " -jar " + constants.COMPONENTS_DIRECTORY + "/service.jar --engine-ports " + engine_ports)
  else:
    print ("ERROR: Please make sure configuration file: " + constants.CONFIGURATION_FILE + " exists")