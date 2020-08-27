import os
from pathlib import Path
from shutil import copy, copytree
import common_constants as const


def build_directory_exists() -> bool:
    return os.path.isdir(const.BUILD_DIRECTORY)


def create_build_directory():
    if not os.path.isdir(const.BUILD_DIRECTORY):
        os.mkdir(const.BUILD_DIRECTORY)


def create_components_directory():
    if not os.path.isdir(const.BUILD_COMPONENTS_DIRECTORY):
        os.mkdir(const.BUILD_COMPONENTS_DIRECTORY)


def create_logs_directory():
    if not os.path.isdir(const.BUILD_LOGS_DIRECTORY):
        os.mkdir(const.BUILD_LOGS_DIRECTORY)


def compile_and_run_tests_python():
    os.system("cd ../engine && python3 -m compileall -f .")
    os.system("cd ../engine && python3 -m pytest -s")


def compile_java_project():
    os.system("cd .. && mvn clean package -q")


def get_all_jar_paths() -> [str]:
    jar_paths = []
    for path in Path('..').rglob('*.jar'):
        jar_paths.append(path.resolve())
    return jar_paths


def copy_all_jars(jar_paths: [str]):
    for jar_path in jar_paths:
        copy(jar_path, const.BUILD_COMPONENTS_DIRECTORY)


def copy_engine_project():
    copytree("../engine", const.BUILD_COMPONENTS_ENGINE_DIRECTORY)


def copy_launcher_scripts():
    copy("common_constants.py", const.BUILD_DIRECTORY)
    copy("launch_common.py", const.BUILD_DIRECTORY)
    copy("launch_service.py", const.BUILD_DIRECTORY)
    copy("launch_ui.py", const.BUILD_DIRECTORY)


def copy_configuration_file():
    copy("swissknife.ini", const.BUILD_DIRECTORY)


if __name__ == "__main__":
    if build_directory_exists():
        print("ERROR: Please remove the build directory [../build], to package the solution")
    else:
        print("Compiling and Running Python tests")
        compile_and_run_tests_python()
        print("Compiling Java projects")
        compile_java_project()
        print("Creating build directory")
        create_build_directory()
        print("Creating components directory")
        create_components_directory()
        print("Creating logs directory")
        create_logs_directory()
        print("Copying all jars")
        copy_all_jars(get_all_jar_paths())
        print("Copying engine project")
        copy_engine_project()
        print("Copying launcher scripts")
        copy_launcher_scripts()
        print("Copying configuration file")
        copy_configuration_file()
