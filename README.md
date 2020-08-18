# Machine Learning Swissknife

The aim of this application is to make machine learning accessible for everyone. A user with a set of data wants to have a calculated prediction, estimation or forecast will be able to do so with this very simple webapplication.

## Architecture

Below is the architecture of this application (Components might be added while developing this platform).

![Architecture](./resources/documentation/images/architectures.png)

> - The Web app will be developped in Java using spring framework
> - The calculation engines are seperate  spawnable webservices developed in python using flask and sklearn (Python3)
> - The aim is dockerize all components

## Algorithms

> - [Time Series Analysis](./resources/documentation/TimeSeriesAnalysisService.md)

## Developers

### Python

``` bash
python3 -m venv .venv
source .venv/bin/activate
pip install --upgrade pip
pip install -r requirements.txt
pip install pylint
code .
```

#### Compile all files

```bash
python -m compileall -f .
```

#### Run test

``` bash
pytest -s
```

#### Get Python documentation

``` bash
python -m pydoc {file}
```

## Contributors

- Joe ABDELNOUR
- Ramy EID
