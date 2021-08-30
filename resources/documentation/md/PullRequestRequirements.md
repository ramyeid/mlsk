# Pull Request Requirements

All pull requests should be open after running the following steps

- Make sure that the API was not updated, if so do not forget to update the swagger yaml files

- Lint python

    ```bash
    source .venv/bin/activate
    cd engine
    pylint *
    ```

- Lint Angular

    ```bash
    cd web-ui
    ng lint
    ```

- Package with tests

- Run your end-to-end test case

- Update _Board.csv_
