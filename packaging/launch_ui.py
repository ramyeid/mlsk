import common_constants as constants
import launch_common as common
import os


if __name__== "__main__":
  if (common.configuration_file_exists()):
    project_information = common. read_project_information()
    service_port = project_information[constants.SERVICE_PORT_OPTION]

    print ("launching ui with service port: " + service_port)
    os.system("java -jar " + constants.COMPONENTS_DIRECTORY + "ui-jar-with-dependencies.jar --service-port " + service_port)

  else:
    print ("ERROR: Please make sure configuration file: " + constants.CONFIGURATION_FILE + " exists")