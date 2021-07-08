# Web UI

The Web UI is a angular project that is easy to write and launch and should offer better graphics and easier UI experience

## Angular

Angular is very easy to use, launch and debug.
Also Angular offers very rich UI models and reusable components

### Setup Angular Environment

In order to launch Angular we need to install npm and ng

#### To install npm
```
https://nodejs.org/en/
```

#### To install ng
```bash
npm install -g @angular/cli
```

### Build

```bash
cd web-ui
ng build --prod
```

### Run test

```bash
ng test
ng e2e
```

### Run Lint

```bash
ng lint
```

### Launch the Web ui engine

To launch the web ui we need to build and open the index.html this is automated using the script under packaging

```bash
ng serve -o --port [port]
```
