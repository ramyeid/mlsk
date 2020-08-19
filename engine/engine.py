#!/usr/bin/python3

from flask import Flask
import argparse
import time_series_analysis_controller


app = Flask(__name__)
app.add_url_rule("/time-series-analysis/forecast", methods=['POST'], view_func=time_series_analysis_controller.forecast)
app.add_url_rule("/time-series-analysis/forecast-accuracy", methods=['POST'], view_func=time_series_analysis_controller.compute_accuracy_of_forecast)
app.add_url_rule("/time-series-analysis/predict", methods=['POST'], view_func=time_series_analysis_controller.predict)


if __name__== "__main__":
  parser = argparse.ArgumentParser()
  parser.add_argument("--port", help="specify port to run python engine", required= True)
  args = parser.parse_args()
  
  app.run(host='0.0.0.0', port=args.port)
