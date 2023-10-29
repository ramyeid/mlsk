# Web UI

The Web UI is a angular project that is easy to write and launch and should offer better graphics and easier UI experience

> **_NOTE:_**  You will be able to modify the url (host & port) of the *server* in a configuration page in the ui.

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

### Install all dependencies

```bash
cd web-ui
npm install
```

### Upgrade all dependencies in package.json

The below commands will not consider compatibility with other dependencies, the command will simply upgrade dependencies to latest versions.

The expec

```bash
# Clean Cache
sudo npm cache clean --force
# Update Node
node -v
sudo npm install -g n
sudo n latest 
node -v
# Update npm
sudo npm install -g npm@latest --force
sudo npm cache clean --force
# Install npm-check-updates
sudo npm install -g npm-check-updates 
# Upgrade all dependencies
ncu -u
# Install dependencies
npm install
# Fix compatibility issues from the command above.
# For example the error below is warning us to switch typescript version from 5.2.2 to 4.9.3,
# since @angular/compiler-cli@16.2.11 is using 4.9.3.
npm ERR! While resolving: machine-learning-swissknife@0.0.0
npm ERR! Found: typescript@5.2.2
npm ERR! node_modules/typescript
npm ERR!   dev typescript@"~5.2.2" from the root project
npm ERR! 
npm ERR! Could not resolve dependency:
npm ERR! peer typescript@">=4.9.3 <5.2" from @angular/compiler-cli@16.2.11
npm ERR! node_modules/@angular/compiler-cli
npm ERR!   dev @angular/compiler-cli@"~16.2.11" from the root project
npm ERR!   peer @angular/compiler-cli@"^16.0.0" from @angular-devkit/build-angular@16.2.8
npm ERR!   node_modules/@angular-devkit/build-angular
npm ERR!     dev @angular-devkit/build-angular@"^16.2.8" from the root project
# We might get vulnerabilities e.g. of output
45 vulnerabilities (2 low, 9 moderate, 29 high, 5 critical)
# Let's leverage `audit fix` to fix them!
npm audit fix --force
```

### Build

```bash
cd web-ui
ng build --prod
```

### Run test

```bash
ng test --watch=false --browsers=[Chrome|ChromeHeadlessNoSandbox] --reporters=[junit|progress] --code-coverage 

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

## Docker Image

This service is deployed on docker under [ramyeid/mlsk-web-ui](https://hub.docker.com/repository/docker/ramyeid/mlsk-web-ui)

> Information on how to launch a container can be found [here](devops/scripts/Deployment.md)
