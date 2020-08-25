# Engine

The Engine is a python project that is easy to launch that will offer endpoints for all kind of algorithms for machine learning.

## Algorithms

> - [Time Series Analysis](TimeSeriesAnalysisService.md)

## Python

Python is very easy to use, launch and debug.
Also Python Libraries are very rich with machine learning algorithms

### Setup Python Environment

``` bash
python3 -m venv .venv
source .venv/bin/activate
pip install --upgrade pip
pip install -r requirements.txt
pip install pylint
code .
```

### Compile all files

```bash
python -m compileall -f .
```

### Run test

``` bash
pytest -s
```

### Get Python documentation

``` bash
python -m pydoc {file}
```

### Launch an engine

```bash
    python engine.py --port 6767 --logs-path /Users/ramyeid/Documents/machine-learning-swissknife/build/logs/
```
