#!/usr/bin/python3

import os
import argparse
from pathlib import Path
from shutil import copy, copytree, rmtree
import common_constants as const
import common


def launch_command(command: str, exception_message: str):
    if os.system(command) != 0:
        raise Exception(exception_message)


def print_start_step(step_name: str):
    print("-"*10 + "START[" + step_name + "]" + "-"*10)


def print_end_step(step_name: str):
    print("-"*11 + "END[" + step_name + "]" + "-"*11)
    print("\n")


def build_directory_exists() -> bool:
    return os.path.isdir(const.BUILD_DIRECTORY)


def create_build_directory():
    print("  Creating build directory")
    if not build_directory_exists():
        os.mkdir(const.BUILD_DIRECTORY)


def create_components_directory():
    print("  Creating components directory")
    if not os.path.isdir(const.BUILD_COMPONENTS_DIRECTORY):
        os.mkdir(const.BUILD_COMPONENTS_DIRECTORY)


def create_logs_directory():
    print("  Creating logs directory")
    if not os.path.isdir(const.BUILD_LOGS_DIRECTORY):
        os.mkdir(const.BUILD_LOGS_DIRECTORY)


def delete_build_directory():
    print("  Deleting build directory")
    if build_directory_exists():
        rmtree(const.BUILD_DIRECTORY)


def compile_python():
    print("  Compiling Python")
    command = "cd ../engine && python3 -m compileall -f ."
    launch_command(command, "Python compilation failed")


def compile_python_and_run_tests():
    compile_python()
    print("  Running Python tests")
    command = "cd ../engine && python3 -m pytest -s"
    launch_command(command, "Python test failed")


def compile_java():
    print("  Compiling Java")
    command = "cd .. && mvn clean package -DskipTests -q"
    launch_command(command, "Java compilation failed")


def compile_java_and_run_tests():
    compile_java()
    print("  Running Java tests")
    command = "cd .. && mvn verify -q"
    launch_command(command, "Java test failed")


def read_angular_prod_environment_file():
    return common.read_file("../web-ui/src/environments/environment.prod.ts")


def overwrite_angular_prod_environment_file():
    print("  Setting Angular Environment Variables")

    # Read Service Port
    project_information = common.read_project_information()
    service_port = project_information[const.SERVICE_PORT_OPTION]

    # Override Service Port in environment.prod.ts
    common.replace_placeholder_in_file("../web-ui/src/environments/environment.prod.ts",
                                       const.ANGULAR_SERVER_PORT_OPTION, service_port)


def reset_angular_prod_environment_file(initial_content: str):
    common.write_file("../web-ui/src/environments/environment.prod.ts", initial_content)


def compile_angular():
    print("  Compiling Angular")
    command = "cd ../web-ui && ng build --configuration production"
    launch_command(command, "Angular compilation failed")


def compile_angular_and_run_tests():
    compile_angular()
    print("  Running Angular tests")
    command = "cd ../web-ui && ng test --watch=false --browsers=ChromeHeadlessNoSandbox"
    launch_command(command, "Angular test failed")


def get_all_jar_paths() -> [str]:
    jar_paths = []
    for path in Path('..').rglob('*.jar'):
        jar_paths.append(path.resolve())
    return jar_paths


def copy_all_jars(jar_paths: [str]):
    print("  Copying jars")
    for jar_path in jar_paths:
        copy(jar_path, const.BUILD_COMPONENTS_DIRECTORY)


def copy_engine_project():
    print("  Copying Engine")
    copytree("../engine", const.BUILD_COMPONENTS_ENGINE_DIRECTORY)


def copy_web_ui_dist():
    print("  Copying Web UI Dist")
    copytree("../web-ui/dist/machine-learning-swissknife", const.BUILD_COMPONENTS_WEB_UI_DIST_DIRECTORY)


def copy_launcher_scripts():
    print("  Copying launcher scripts")
    copy("common_constants.py", const.BUILD_DIRECTORY)
    copy("common.py", const.BUILD_DIRECTORY)
    copy("launch_service.py", const.BUILD_DIRECTORY)
    copy("launch_ui.py", const.BUILD_DIRECTORY)
    copy("launch_web_ui.py", const.BUILD_DIRECTORY)


def copy_configuration_file():
    print("  Copying configuration file")
    copy(const.CONFIGURATION_FILE, const.BUILD_DIRECTORY)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--skipTests", dest='should_skip_tests', action='store_true')
    args = parser.parse_args()

    prod_environment_file_content = read_angular_prod_environment_file()

    if build_directory_exists():
        print("ERROR: Please remove the build directory [../build], to package the solution")
    else:
        try:
            print_start_step("CREATING DIRECTORIES")
            create_build_directory()
            create_components_directory()
            create_logs_directory()
            print_end_step("CREATING DIRECTORIES")

            print_start_step("COMPILE PYTHON")
            compile_python() if args.should_skip_tests else compile_python_and_run_tests()
            print_end_step("COMPILE PYTHON")
            print_start_step("COPY PYTHON")
            copy_engine_project()
            print_end_step("COPY PYTHON")

            print_start_step("COMPILE JAVA")
            compile_java() if args.should_skip_tests else compile_java_and_run_tests()
            print_end_step("COMPILE JAVA")
            print_start_step("COPY JAVA")
            copy_all_jars(get_all_jar_paths())
            print_end_step("COPY JAVA")

            print_start_step("OVERWRITE PROD ENV FILE ANGULAR")
            overwrite_angular_prod_environment_file()
            print_end_step("OVERWRITE PROD ENV FILE ANGULAR")
            print_start_step("COMPILE ANGULAR")
            compile_angular() if args.should_skip_tests else compile_angular_and_run_tests()
            print_end_step("COMPILE ANGULAR")
            print_start_step("COPY ANGULAR")
            copy_web_ui_dist()
            print_end_step("COPY ANGULAR")

            print_start_step("COPY SETUP")
            copy_launcher_scripts()
            copy_configuration_file()
            print_end_step("COPY SETUP")

        except Exception as exception:
            print("\n"*5)
            print_start_step("ERROR")
            print("  {}".format(exception))
            delete_build_directory()
            print_end_step("ERROR")

        finally:
            reset_angular_prod_environment_file(prod_environment_file_content)
