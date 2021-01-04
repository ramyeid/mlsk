#!/usr/bin/python3

import common_constants as const
import launch_common as common
import os


if __name__ == "__main__":
    if common.configuration_file_exists():
        project_information = common. read_project_information()
        web_ui_port = project_information[const.WEB_UI_PORT_OPTION]

        print("launching web ui")
        print(" with angular port: {}".format(web_ui_port))
        os.system("cd {}/{} && ng serve -o --port {}".format(const.COMPONENTS_DIRECTORY, const.WEB_UI_DIRECTORY, web_ui_port))

    else:
        print("ERROR: Please make sure configuration file: {} exists".format(const.CONFIGURATION_FILE))
