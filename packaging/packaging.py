#!/usr/bin/python3

import os
import argparse
from pathlib import Path
from shutil import copy, copytree
import common_constants as const


def print_start_step(step_name: str):
    print("-"*10 + "START[" + step_name + "]" + "-"*10)


def print_end_step(step_name: str):
    print("-"*11 + "END[" + step_name + "]" + "-"*11)
    print("\n")


def build_directory_exists() -> bool:
    return os.path.isdir(const.BUILD_DIRECTORY)


def create_build_directory():
    print("  Creating build directory")
    if not os.path.isdir(const.BUILD_DIRECTORY):
        os.mkdir(const.BUILD_DIRECTORY)


def create_components_directory():
    print("  Creating components directory")
    if not os.path.isdir(const.BUILD_COMPONENTS_DIRECTORY):
        os.mkdir(const.BUILD_COMPONENTS_DIRECTORY)


def create_logs_directory():
    print("  Creating logs directory")
    if not os.path.isdir(const.BUILD_LOGS_DIRECTORY):
        os.mkdir(const.BUILD_LOGS_DIRECTORY)


def compile_python():
    print("  Compiling Python")
    os.system("cd ../engine && python3 -m compileall -f .")


def compile_python_and_run_tests():
    compile_python()
    print("  Running Python tests")
    os.system("cd ../engine && python3 -m pytest -s")


def compile_java():
    print("  Compiling Java")
    os.system("cd .. && mvn clean package -DskipTests -q")


def compile_java_and_run_tests():
    print("  Compiling Java and running tests")
    os.system("cd .. && mvn clean package -q")


def compile_angular():
    print("  Compiling Angular")
    os.system("cd ../web-ui && ng build --configuration production")


def compile_angular_and_run_tests():
    compile_angular()
    print("  Running Angular tests")
    os.system("cd ../web-ui && ng test --watch=false")


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


def copy_web_ui_project():
    print("  Copying Web UI")
    copytree("../web-ui", const.BUILD_COMPONENTS_WEB_UI_DIRECTORY)


def copy_launcher_scripts():
    print("  Copying launcher scripts")
    copy("common_constants.py", const.BUILD_DIRECTORY)
    copy("launch_common.py", const.BUILD_DIRECTORY)
    copy("launch_service.py", const.BUILD_DIRECTORY)
    copy("launch_ui.py", const.BUILD_DIRECTORY)
    copy("launch_web_ui.py", const.BUILD_DIRECTORY)


def copy_configuration_file():
    print("  Copying configuration file")
    copy("swissknife.ini", const.BUILD_DIRECTORY)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--skipTests", dest='should_skip_tests', action='store_true')
    args = parser.parse_args()

    if build_directory_exists():
        print("ERROR: Please remove the build directory [../build], to package the solution")
    else:

        print_start_step("PYTHON")
        compile_python() if args.should_skip_tests else compile_python_and_run_tests()
        print_end_step("PYTHON")

        print_start_step("JAVA")
        compile_java() if args.should_skip_tests else compile_java_and_run_tests()
        print_end_step("JAVA")

        print_start_step("ANGULAR")
        compile_angular() if args.should_skip_tests else compile_angular_and_run_tests()
        print_end_step("ANGULAR")

        print_start_step("CREATING DIRECTORIES")
        create_build_directory()
        create_components_directory()
        create_logs_directory()
        print_end_step("CREATING DIRECTORIES")

        print_start_step("COPYING JARS")
        copy_all_jars(get_all_jar_paths())
        print_end_step("COPYING JARS")

        print_start_step("COPYING ENGINE")
        copy_engine_project()
        print_end_step("COPYING ENGINE")

        print_start_step("COPYING WEB UI")
        copy_web_ui_project()
        print_end_step("COPYING WEB UI")

        print_start_step("COPYING SETUP")
        copy_launcher_scripts()
        copy_configuration_file()
        print_end_step("COPYING SETUP")
