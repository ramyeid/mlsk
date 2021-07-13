#!/usr/bin/python3

import common_constants as const
import launch_common as common
import os


if __name__ == "__main__":
    if common.configuration_file_exists():
        project_information = common. read_project_information()
        service_port = project_information[const.SERVICE_PORT_OPTION]

        print("launching ui ")
        print(" with service port: {}".format(service_port))
        os.system("java -jar {}{} "
                  "--service-port {}"
                  .format(const.COMPONENTS_DIRECTORY, const.UI_JAR, service_port))

    else:
        print("ERROR: Please make sure configuration file: {} exists".format(const.CONFIGURATION_FILE))
