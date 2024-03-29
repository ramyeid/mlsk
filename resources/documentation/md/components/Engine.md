# Engine

The Engine is a python project that is easy to launch that will offer endpoints for all kind of algorithms for machine learning.

## Algorithms

> - [Time Series Analysis](services/TimeSeriesAnalysisService.md)
> - [Decision Tree](services/DecisionTreeService.md)

## Python

Python is very easy to use, launch and debug.
Also Python Libraries are very rich with machine learning algorithms

### Requirements

> - python3

### Setup Python Environment

```bash
cd mlsk
python3 -m venv .venv
source .venv/bin/activate
pip install --upgrade pip
pip install -r engine/requirements.txt
pip install pylint
code .
```

### Compile all files

```bash
python3 -m compileall -f .
```

### Run all tests

```bash
python3 -m pytest -s
```

### Run single test

```bash
python3 -m pytest -s test/services/test_time_series_analysis_service.py::TestTimeSeriesAnalysisService::test_forecast_service
```

### Run all tests with coverage and junit report

```bash
python3 -m pytest -s --junitxml=python-test-reports.xml --cov=. --cov-report xml:coverage.xml
```

### Run Lint

```bash
pylint *
```

### Get Python documentation

```bash
python3 -m pydoc {file}
```

### Launch an engine

```bash
python3 engine_server.py \
  --port 6767 \
  --logs-path /Users/ramyeid/Documents/FYP/V1/mlsk/build/logs/ \
  --log-level INFO
```
