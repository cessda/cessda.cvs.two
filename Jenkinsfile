pipeline {
    options {
        buildDiscarder logRotator(artifactNumToKeepStr: '1', numToKeepStr: '20')
        quietPeriod 300
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
        // Building on main
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
                            sh "./mvnw clean install -Pprod"
                        }
                    }
                    when { branch 'main' }
                }
                // Not running on main - test only (for PRs and integration branches)
                stage('Test Project') {
                    steps {
                        withMaven {
                            sh './mvnw clean verify -Pprod'
                        }
                    }
                    when { not { branch 'main' } }
                }
                stage('Record Issues') {
                    steps {
                        recordIssues aggregatingResults: true, tools: [errorProne(), java()]
                    }
                }
            }
            post {
                always {
                    junit 'target/test-results/TESTS-results-jest.xml'
                }
            }
        }
        stage('Run Sonar Scan') {
            steps {
                withSonarQubeEnv('cessda-sonar') {
                    nodejs('node-14') {
                        withMaven {
                            sh "./mvnw sonar:sonar -Pprod"
                        }
                    }
                }
            }
            when { branch 'main' }
        }
        stage("Get Sonar Quality Gate") {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: false
                }
            }
            when { branch 'main' }
        }
        stage('Build and Push Docker Image') {
            steps {
                sh 'gcloud auth configure-docker'
                withMaven {
                    sh "./mvnw jib:build -Pprod -Djib.to.image=${IMAGE_TAG}"
                }
                sh "gcloud container images add-tag ${IMAGE_TAG} ${docker_repo}/${productName}-${componentName}:${env.BRANCH_NAME}-latest"
            }
            when { branch 'main' }
        }
        stage('Deploy CVS') {
            steps {
                build job: 'cessda.cvs.deploy/main', parameters: [string(name: 'frontend_image_tag', value: "${env.BRANCH_NAME}-${env.BUILD_NUMBER}")], wait: false
            }
            when { branch 'main' }
        }
    }
}
