import os
import common_constants as const
import launch_common as common


def get_absolute_path_logs() -> str:
    return os.path.abspath(const.LOGS_DIRECTORY)


def get_absolute_engine_path() -> str:
    return os.path.abspath(const.COMPONENTS_DIRECTORY + const.ENGINE_DIRECTORY)


if __name__ == "__main__":
    if common.configuration_file_exists():
        project_information = common.read_project_information()
        service_port = project_information[const.SERVICE_PORT_OPTION]
        engine_ports = project_information[const.ENGINE_PORTS_OPTION]
        logs_path = get_absolute_path_logs()
        engine_path = get_absolute_engine_path()

        print("launching service ")
        print(" with port: {}".format(service_port))
        print(" and engine ports: {}".format(engine_ports))
        print(" and logs path: {}".format(logs_path))
        print(" and engine path: {}".format(engine_path))

        os.system("java -Dserver.port={} -jar {}service.jar "
                  "--engine-ports {} "
                  "--logs-path {} "
                  "--engine-path {} "
                  .format(service_port, const.COMPONENTS_DIRECTORY, engine_ports, logs_path, engine_path))
    else:
        print("ERROR: Please make sure configuration file: {} exists".format(const.CONFIGURATION_FILE))
