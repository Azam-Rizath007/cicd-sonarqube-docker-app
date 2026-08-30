pipeline {

    agent any

    tools {
        jdk 'JDK-21'
        maven 'maven3.9.16'
    }

    environment {
        DOCKERHUB_USERNAME = 'azamrizath'
        IMAGE_NAME = "${DOCKERHUB_USERNAME}/cicd-sonarqube-docker-app"
        PATH = "C:\\Users\\rizat\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin;${env.PATH}"
    }

    options {
        skipDefaultCheckout(true)
        timestamps()
    }

    triggers {
        githubPush()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                bat 'mvn clean verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube-Server') {
                    bat 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970:sonar'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t %IMAGE_NAME%:%BUILD_NUMBER% -t %IMAGE_NAME%:latest .'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'Dockerhub-Credential',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_TOKEN'
                    )
                ]) {
                    bat 'echo %DOCKER_TOKEN%| docker login -u %DOCKER_USER% --password-stdin'
                    bat 'docker push %IMAGE_NAME%:%BUILD_NUMBER%'
                    bat 'docker push %IMAGE_NAME%:latest'
                }
            }
        }

        stage('Deploy') {
            steps {
                bat '''
                    @echo off
                    for /f %%i in ('docker ps -aq -f "name=^cicd-app$"') do docker rm -f %%i
                    docker run -d --name cicd-app -p 8081:8081 %IMAGE_NAME%:latest
                '''
            }
        }
    }

    post {

        always {
            junit testResults: 'target/surefire-reports/*.xml',
                  allowEmptyResults: true

            archiveArtifacts artifacts: 'target/*.jar',
                             allowEmptyArchive: true

            bat 'docker logout'
        }

        success {
            echo 'CI/CD pipeline completed successfully.'
        }

        failure {
            echo 'CI/CD pipeline failed. Check the failed stage.'
        }
    }
}