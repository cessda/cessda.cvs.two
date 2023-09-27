pipeline {
    options {
        disableConcurrentBuilds abortPrevious: true
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
        stage('Compile Angular') {
            agent {
                docker {
                    image 'node:14'
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
                stage('Compile Angular') {
                    steps {
                        sh 'npm run build-prod'
                    }
                }
                stage('Run Jest tests') {
                    steps {
                        sh 'npm test'
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
            stages {
                stage('Build Project') {
                    steps {
                        withMaven {
                            sh "./mvnw install -Pci"
                        }
                    }
                    when { branch 'main' }
                }
                // Not running on main - test only (for PRs and integration branches)
                stage('Test Project') {
                    steps {
                        withMaven {
                            sh './mvnw verify -Pci'
                        }
                    }
                    when { not { branch 'main' } }
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
                    nodejs('node-14') {
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
                sh 'gcloud auth configure-docker'
                withMaven {
                    sh "./mvnw jib:build -Pci -Djib.to.image=${IMAGE_TAG}"
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
