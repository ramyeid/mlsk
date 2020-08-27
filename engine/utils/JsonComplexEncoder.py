#!/usr/bin/python3
import json


class JsonComplexEncoder(json.JSONEncoder):


    def default(self, obj):
        if hasattr(obj, 'to_json'):
            return obj.to_json()
        else:
            return json.JSONEncoder.default(self, obj)
