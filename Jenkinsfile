pipeline {
    options {
        buildDiscarder logRotator(artifactNumToKeepStr: '5', numToKeepStr: '20')
    }

    environment {
        productName = "cvs"
        componentName = "frontend"
        image_tag = "${docker_repo}/${productName}-${componentName}:${env.BRANCH_NAME}-${env.BUILD_NUMBER}"

        // Fixes NPM trying to use / as the home directory
        HOME = '.'

        // Disable verbose webpack logs
        JHI_DISABLE_WEBPACK_LOGS = 'true'
    }

    agent {
        label 'jnlp-himem'
    }

    stages {
        // Building on master
        stage('Pull SDK Docker Image') {
            agent {
                docker {
                    image 'maven:3-jdk-11'
                    reuseNode true
                }
            }
            stages {
                stage('Build Project') {
                    steps {
                        withMaven {
                            sh "$MVN_CMD clean install -Pprod"
                        }
                    }
                    when { branch 'master' }
                }
                // Not running on master - test only (for PRs and integration branches)
                stage('Test Project') {
                    steps {
                        withMaven {
                            sh '$MVN_CMD clean verify -Pprod'
                        }
                    }
                    when { not { branch 'master' } }
                }
                stage('Record Issues') {
                    steps {
                        recordIssues aggregatingResults: true, tools: [errorProne(), java()]
                    }
                }
                stage('Run Sonar Scan') {
                    steps {
                        withSonarQubeEnv('cessda-sonar') {
                            nodejs('node') {
                                withMaven {
                                    sh "$MVN_CMD sonar:sonar -Pprod"
                                }
                            }
                        }
                    }
                    when { branch 'master' }
                }
            }
            post {
                always {
                    junit 'target/test-results/TESTS-results-jest.xml'
                }
            }
        }
        stage("Get Sonar Quality Gate") {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: false
                }
            }
            when { branch 'master' }
        }
        stage('Build and Push Docker Image') {
            steps {
                sh 'gcloud auth configure-docker'
                withMaven {
                    sh "mvn jib:dockerBuild -Pprod -D\"docker.registry.host\"=${docker_repo} -D\"docker.image.name\"=${productName}-${componentName} -D\"docker.image.tag\"=${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
                }
                sh("gcloud container images add-tag ${IMAGE_TAG} ${docker_repo}/${productName}-${componentName}:${env.BRANCH_NAME}-latest")
            }
            when { branch 'master' }
        }
    }
}
