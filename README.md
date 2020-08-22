# Machine Learning Swissknife

The aim of this application is to make machine learning accessible for everyone. A user with a set of data wants to have a calculated prediction, estimation or forecast will be able to do so with this very simple webapplication.

## Architecture

Below is the architecture of this application (Components might be added while developing this platform).

![Architecture](./resources/documentation/images/architectures.png)

> - The Web app will be developped in Java using spring framework
> - The calculation engines are seperate  spawnable webservices developed in python using flask and sklearn (Python3)
> - The aim is dockerize all components

## Components

> - [Engine](./resources/documentation/Engine.md)
> - [Service](./resources/documentation/Service.md)

## Package and run

We worked hard to make machine learning easy and accessible for everyone. Packaging and running the solution should also be easy.

### Requirements

- jdk11
- python3

### Package

To package the solution a script is offered under packaging/.
The packaging will create a build directory containing the launcher scripts for different services, a configuration file (swissknife.ini) & the jars under build/components/

```bash
cd packaging
python3 packaging.py
```

### Configuration file

The configuration file used has to be named **swissknife.ini** and it should have the following style.

```txt
[SERVICE]
port={port}
[ENGINE]
ports={port1,port2}
```

_We will create as many engines as there are values in the section ENGINE#ports_.

_Ports can be modified if needed. (nothing is hard coded in our codeline)_.

### Launch Service

The launch_service dumped under build/ will read from the configuration file; and run the service jar which will launch the REST service

```bash
cd build
python3 launch_service.py
```

### Launch UI

The launch_ui dumped under build/ will read from the configuration file; and run the ui jar.

```bash
cd build
python3 launch_ui.py
```

## Contributors

- Joe ABDELNOUR
- Ramy EID
