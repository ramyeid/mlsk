# Web UI

The Web UI is a angular project that is easy to write and launch and should offer better graphics and easier UI experience

## Angular

Angular is very easy to use, launch and debug.
Also Angular offers very rich UI models and reusable components

### Setup Angular Environment

In order to launch Angular we need to install npm and ng

#### To install npm

```text
https://nodejs.org/en/
```

#### To install ng

```bash
npm install -g @angular/cli
```

#### To install lite-server

```bash
npm install --global lite-server
```

> for more information on [_lite-server_](https://www.npmjs.com/package/lite-server)

### Build

```bash
cd web-ui
ng build --prod
```

### Run test

```bash
ng test --watch=false --browsers=[Chrome|ChromeHeadlessNoSandbox] --reporters=[junit|progress]
ng e2e
```

### Run Lint

```bash
ng lint
```

#### Rules

Rules should be added to *.eslintrc*

> Rules can be found [here](https://eslint.org/docs/rules/)
>
> Tutorial for ESLint can be found [here](https://khalilstemmler.com/blogs/typescript/eslint-for-typescript/)
>
> List of other configs and plugins can be found [here](https://github.com/dustinspecker/awesome-eslint)

### Unused dependencies

To check for unused dependencies we are using *depcheck*

```bash
npm install depcheck -g
cd web-ui
depcheck
```

### Launch the Web ui engine

To launch the web ui we need to build and open the index.html this is automated using the script under packaging

```bash
ng serve -o --port [port]
```
