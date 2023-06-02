[![SQAaaS badge](https://github.com/EOSC-synergy/SQAaaS/raw/master/badges/badges_150x116/badge_software_silver.png)](https://api.eu.badgr.io/public/assertions/4nWcBFSBRZ2rvczQL3gCqA 'SQAaaS silver badge achieved')

[![Build Status](https://jenkins.cessda.eu/buildStatus/icon?job=cessda.cvs.two%2Fmain)](https://jenkins.cessda.eu/job/cessda.cvs.two/job/main/)
[![Bugs](https://sonarqube.cessda.eu/api/project_badges/measure?project=eu.cessda.cvs%3Acvs&metric=bugs)](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs)
[![Code Smells](https://sonarqube.cessda.eu/api/project_badges/measure?project=eu.cessda.cvs%3Acvs&metric=code_smells)](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs)
[![Coverage](https://sonarqube.cessda.eu/api/project_badges/measure?project=eu.cessda.cvs%3Acvs&metric=coverage)](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs)
[![Duplicated Lines (%)](https://sonarqube.cessda.eu/api/project_badges/measure?project=eu.cessda.cvs%3Acvs&metric=duplicated_lines_density)](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs)
[![Lines of Code](https://sonarqube.cessda.eu/api/project_badges/measure?project=eu.cessda.cvs%3Acvs&metric=ncloc)](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs)
[![Maintainability Rating](https://sonarqube.cessda.eu/api/project_badges/measure?project=eu.cessda.cvs%3Acvs&metric=sqale_rating)](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs)
[![Quality Gate Status](https://sonarqube.cessda.eu/api/project_badges/measure?project=eu.cessda.cvs%3Acvs&metric=alert_status)](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs)
[![Reliability Rating](https://sonarqube.cessda.eu/api/project_badges/measure?project=eu.cessda.cvs%3Acvs&metric=reliability_rating)](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs)
[![Security Rating](https://sonarqube.cessda.eu/api/project_badges/measure?project=eu.cessda.cvs%3Acvs&metric=security_rating)](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs)
[![Technical Debt](https://sonarqube.cessda.eu/api/project_badges/measure?project=eu.cessda.cvs%3Acvs&metric=sqale_index)](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs)
[![Vulnerabilities](https://sonarqube.cessda.eu/api/project_badges/measure?project=eu.cessda.cvs%3Acvs&metric=vulnerabilities)](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs)

# CESSDA Vocabulary Service

This repository contains all the source code and configuration needed to deploy the CESSDA Vocabulary Service application

## Project Structure

```bash
<ROOT>
├── infrastructure/ # Deployment scripts and configuration for gcp
├── src/            # Java application source code and configuration
├── target/         # Target build location - this is not committed to Git
├── CONTRIBUTORS.md # List of contributors to the project
├── Jenkinsfile     # Script used by Jenkins to deploy the application to gcp
├── pom.xml         # Maven build configuration file
├── README.md       # Instructions to build and deploy the application (this file)
```

## Technology Stack

Several frameworks and languages are used in this application.

| Framework/Technology | Description                           |
| -------------------- | ------------------------------------- |
| Docker               | Container platform                    |
| Kubernetes           | Container orchestrator                |
| Maven                | Build tool                            |
| Java                 | Programming language and API          |
| Spring Boot          | Web application framework (Back-End)  |
| Angular 10           | Web application framework (Front-End) |

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js](https://nodejs.org/): We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

```shell
npm install
```

We use NPM scripts and [Webpack](https://webpack.js.org/) as our build system.

Run the following commands in two separate terminals to create a blissful development
experience where your browser auto-refreshes when files change on your hard drive.

```shell
./mvnw
npm start
```

NPM is also used to manage CSS and JavaScript dependencies used in this application.
You can upgrade dependencies by specifying a newer version in [package.json](package.json).
You can also run `npm update` and `npm install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `npm help update`.

The `npm run` command will list all of the scripts available to run for this project.

### Managing dependencies

For example, to add [Leaflet](https://www.npmjs.com/package/leaflet) library as a runtime dependency of your application, you would run following command:

```shell
npm install --save --save-exact leaflet
```

To benefit from TypeScript type definitions from [DefinitelyTyped](https://github.com/DefinitelyTyped/DefinitelyTypednpm) repository in development, you would run following command:

```shell
npm install --save-dev --save-exact @types/leaflet
```

Then you would import the JS and CSS files specified in library's installation instructions so that [Webpack](https://webpack.js.org/) knows about them:
Edit [src/main/webapp/app/vendor.ts](src/main/webapp/app/vendor.ts) file:

```Javascript
import 'leaflet/dist/leaflet.js';
```

Edit [src/main/webapp/content/scss/vendor.scss](src/main/webapp/content/scss/vendor.scss) file:

```scss
@import '~leaflet/dist/leaflet.css';
```

Note: There are still a few other things remaining to do for Leaflet that we won't detail here.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development](https://www.jhipster.tech/development/).

### Using Angular CLI

You can also use [Angular CLI](https://angular.io/cli) to generate some custom client code.

For example, the following command:

```shell
ng generate component my-component
```

will generate the files:

```text
create src/main/webapp/app/my-component/my-component.component.html
create src/main/webapp/app/my-component/my-component.component.ts
update src/main/webapp/app/app.module.ts
```

## Building for production

### Packaging as jar

To build the final jar and optimize the CVS application for production, run:

```shell
./mvnw -Pprod clean verify
```

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

```shell
java -jar target/*.jar
```

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production](https://www.jhipster.tech/production/) for more details.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

```shell
./mvnw -Pprod,war clean verify
```

## Testing

To launch your application's tests, run:

```shell
./mvnw verify
```

### Client tests

Unit tests are run by [Jest](https://jestjs.io/). They're located in [src/test/javascript/](src/test/javascript/) and can be run with:

```shell
npm test
```

For more information, refer to the [Running tests page](https://www.jhipster.tech/running-tests/).

### Code quality

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on [localhost:9001](http://localhost:9001)) with:

```shell
docker-compose -f src/main/docker/sonar.yml up -d
```

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner)
or by using the maven plugin.

Then, run a Sonar analysis:

```shell
./mvnw -Pprod clean verify sonar:sonar
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize`
phase since Sonar properties are loaded from the sonar-project.properties file.

```shell
./mvnw initialize sonar:sonar
```

For more information, refer to the [Code quality page](https://www.jhipster.tech/code-quality/).

## Using Docker to simplify development

You can use Docker to improve your JHipster development experience.
A number of docker-compose configuration are available in the [src/main/docker](src/main/docker)
folder to launch required third party services.

For example, to start a MySQL database in a docker container, run:

```shell
docker-compose -f src/main/docker/mysql.yml up -d
```

To stop it and remove the container, run:

```shell
docker-compose -f src/main/docker/mysql.yml down
```

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

```shell
./mvnw -Pprod verify jib:dockerBuild
```

Then run:

```shell
docker-compose -f src/main/docker/app.yml up -d
```

For more information refer to [Using Docker and Docker-Compose](https://www.jhipster.tech/docker-compose/),
this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`),
which is able to generate docker configurations for one or several JHipster applications.

## Contributing

Please read [CONTRIBUTING](CONTRIBUTING.md) for details on our code of conduct,
and the process for submitting pull requests to us.

## Versioning

See [Semantic Versioning](https://semver.org/) for guidance.

## Changes

You can find the list of changes made in each release in the
[CHANGELOG](CHANGELOG.md) file.

## License

See the [LICENSE](LICENSE.txt) file.

## Citing

See the [CITATION](CITATION.cff) file.
