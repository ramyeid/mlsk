# Package and run

We worked hard to make machine learning easy and accessible for everyone. Packaging and running the solution should also be easy.

## Requirements

- mvn
- jdk11
- python3
- npm
- ng

## Package

To package the solution a script is offered under packaging/.
The packaging will create a build directory containing the launcher scripts for different services, a configuration file (swissknife.ini) & the jars and the python module under build/components

> It is recommended to launch a python virtual environment locally \
> to do so [Setup venv](./Engine.md#Setup-Python-Environment)


```bash
source .venv/bin/activate
cd packaging
python packaging.py [--skipTests]
```

## Configuration file

The configuration file used has to be named **swissknife.ini** and it should have the following style.

```txt
[SERVICE]
port={port}
[ENGINE]
ports={port1,port2}
[WEB_UI]
port={port}
```

We will create as many engines as there are values in the section ENGINE#ports_.

Ports can be modified if needed. (nothing is hard coded in our codeline)_.

## Launch Service

The launch_service dumped under build/ will read from the configuration file; and run the service jar which will launch the REST service

```bash
source .venv/bin/activate
cd build
python launch_service.py
```

## Launch Desktop UI

The launch_ui dumped under build/ will read from the configuration file; and run the ui jar.

```bash
source .venv/bin/activate
cd build
python launch_ui.py
```

## Launch Web UI

The launch_web_ui dumped under build/ will read from the configuration file; and run the web-ui jar.

```bash
source .venv/bin/activate
cd build
python launch_web_ui.py
```

## Logs

Logs are dumped under build/logs
