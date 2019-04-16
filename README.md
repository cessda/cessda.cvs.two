DEPRECATED, see [cessda.cvmanager.beta](https://bitbucket.org/cessda/cessda.cvmanager.beta) instead



# README #


This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* Quick summary
* Version
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact

### How to run docker locally? ###

1. Run Maven and docker build command in the project directory,
`$ mvn clean -U package docker:build -Pdocker-compose -Dmaven.test.skip=true`

2. Change working directory to target/docker/generated directory  
`$ cd target/docker/generated/`

3. Run docker-compose  
`$ docker-compose up`

4. Finally access the CVManager from `http://localhost:2002/cvmanager-test/#!Browse`
