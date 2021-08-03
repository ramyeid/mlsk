#!/usr/bin/python3

import common_constants as const
import common
import os


def create_bs_config_json(web_ui_port: str, base_dir: str):
    bs_config_file_content = '{"port": ' + web_ui_port + ', "server":{"baseDir": "' + base_dir + '"}}'
    with open("bs-config.json", "w+") as bs_config_file:
        bs_config_file.write(bs_config_file_content)


if __name__ == "__main__":
    if common.configuration_file_exists():
        project_information = common. read_project_information()
        web_ui_port = project_information[const.WEB_UI_PORT_OPTION]

        print("launching web ui")
        print(" with angular port: {}".format(web_ui_port))
        print(" with dist folder: {}".format(const.DIST_DIRECTORY))
        create_bs_config_json(web_ui_port, const.COMPONENTS_WEB_UI_DIST_DIRECTORY)
        os.system("lite-server")

    else:
        print("ERROR: Please make sure configuration file: {} exists".format(const.CONFIGURATION_FILE))
