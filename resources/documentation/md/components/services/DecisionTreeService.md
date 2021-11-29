# Decision Tree Service

A decision tree is a decision support tool that uses a tree-like model of decisions and their possible consequences, including chance event outcomes, resource costs, and utility. It is one way to display an algorithm that only contains conditional control statements.

This Services offers two end points;  predict and predict accuracy

## Predict

```decision-tree/start```
```decision-tree/data```
```decision-tree/predict```

Predict the next x values.
Given a csv input this service will compute the last x values.

## Accuracy for Predict

```decision-tree/start```
```decision-tree/data```
```decision-tree/predict-accuracy```

Compute accuracy for Predict.
Given a csv input this service will compute the last x values and compare them to the actual value.

## Python Documentation

```bash
python3 -m pydoc decision_tree_service
```

## Debug Service

We created a under engines/debug a python file: decision_tree_engine_debug.py
which will let you instead of launching all the components debug the engine seperately.
The three methods are also offered in this small python script:

* Predict

    ```bash
    python3 engine_debug.py --service DT --csv //Users//ramyeid//Documents//FYP//V1//mlsk//resources//data_example//classifier_data.csv --actionColumnNames "Length,Diameter,Height,Whole weight,Shucked weight,Viscera weight,Shell weight,Rings" --predictionColumnName Sex --numberOfValues 3 --action PREDICT [--output //Users//ramyeid//Documents//FYP//V1//mlsk//resources//data_example//classifier_data_predict_output.csv]
    ```

* Accuracy for Predict

    ```bash
    python3 engine_debug.py --service DT --csv //Users//ramyeid//Documents//FYP//V1//mlsk//resources//data_example//classifier_data.csv --actionColumnNames "Length,Diameter,Height,Whole weight,Shucked weight,Viscera weight,Shell weight,Rings" --predictionColumnName Sex --numberOfValues 3 --action PREDICT_ACCURACY
    ```
