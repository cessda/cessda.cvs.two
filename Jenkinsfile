pipeline {
    options {
        disableConcurrentBuilds abortPrevious: true
    }

    environment {
        productName = "cvs"
        componentName = "frontend"
        image_tag = "${DOCKER_ARTIFACT_REGISTRY}/${productName}-${componentName}:${env.BRANCH_NAME}-${env.BUILD_NUMBER}"

        // Fixes NPM trying to use / as the home directory
        HOME = '.'

        // Disable verbose webpack logs
        JHI_DISABLE_WEBPACK_LOGS = 'true'
    }

    agent {
        label 'jnlp-himem'
    }

    stages {
        stage('Node.JS') {
            agent {
                docker {
                    image 'node:18'
                    reuseNode true
                }
            }
            stages {
                stage('Install NPM dependencies') {
                    steps {
                        configFileProvider([configFile(fileId: 'be684558-5540-4ad6-a155-7c1b4278abc0', targetLocation: '.npmrc')]) {
                            sh 'npm ci'
                        }
                    }
                }
                stage('Lint Project') {
                    steps {
                        sh 'npm run lint -- --format checkstyle --output-file target/eslint-reports/report.xml'
                    }
                    post {
                        always {
                            recordIssues(tools: [esLint(pattern: 'target/eslint-reports/report.xml')])
                        }
                    }
                }
                stage('Compile Angular') {
                    steps {
                        sh 'npm run build-prod'
                    }
                }
                stage('Run Jest tests') {
                    steps {
                        sh 'npm test -- --coverage'
                    }
                    post {
                        always {
                            junit 'target/test-results/TESTS-results-jest.xml'
                        }
                    }
                }
            }
        }
        stage('Compile Java') {
            agent {
                docker {
                    image 'eclipse-temurin:11'
                    reuseNode true
                }
            }
            steps {
                withMaven {
                    sh "./mvnw verify -Pci"
                }
            }
        }
        stage('Record Issues') {
            steps {
                recordIssues aggregatingResults: true, tools: [java()]
            }
        }
        stage('Run Sonar Scan') {
            steps {
                withSonarQubeEnv('cessda-sonar') {
                    nodejs('node-18') {
                        withMaven {
                            sh "./mvnw sonar:sonar -Pci"
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
                sh "gcloud auth configure-docker ${ARTIFACT_REGISTRY_HOST}"
                withMaven {
                    sh "./mvnw jib:build -Pci -Djib.to.image=${IMAGE_TAG}"
                }
                sh "gcloud artifacts docker tags add ${IMAGE_TAG} ${DOCKER_ARTIFACT_REGISTRY}/${productName}-${componentName}:${env.BRANCH_NAME}-latest"
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
