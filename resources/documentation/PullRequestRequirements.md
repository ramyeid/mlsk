# Pull Request Requirements

All pull requests should be open after running the following steps

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
- run your end-to-end test case
