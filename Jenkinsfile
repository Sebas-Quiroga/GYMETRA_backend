pipeline {
    agent any

    environment {
        DOCKER_USER = 'quiroga148'
        EC2_HOST = '3.14.191.19'
        SSH_KEY = 'C:\\ProgramData\\Jenkins\\.ssh\\ec2-key.ppk'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Sebas-Quiroga/GYMETRA_backend.git'
            }
        }

        stage('Build Docker Images') {
            steps {
                dir('backend/GYMETR-login') {
                    bat "docker build -t %DOCKER_USER%/gymetr-login:latest ."
                }
                dir('backend/GYMETR-Membership') {
                    bat "docker build -t %DOCKER_USER%/gymetr-membership:latest ."
                }
                dir('backend/GYMETRA-qr') {
                    bat "docker build -t %DOCKER_USER%/gymetra-qr:latest ."
                }
            }
        }

        stage('Login to DockerHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                    usernameVariable: 'USER', passwordVariable: 'PASS')]) {

                    bat """
                    echo %PASS% | docker login -u %USER% --password-stdin
                    """
                }
            }
        }

        stage('Push Images to DockerHub') {
            steps {
                bat "docker push %DOCKER_USER%/gymetr-login:latest"
                bat "docker push %DOCKER_USER%/gymetr-membership:latest"
                bat "docker push %DOCKER_USER%/gymetra-qr:latest"
            }
        }

        stage('Deploy on AWS EC2') {
            steps {
                bat """
                plink -i "%SSH_KEY%" -ssh ubuntu@%EC2_HOST% ^
                    "cd ~/deploy && docker compose -f docker-compose.aws.yml pull && docker compose -f docker-compose.aws.yml up -d --remove-orphans"
                """
            }
        }
    }

    post {
        always {
            bat 'docker system prune -f'
        }
        success {
            echo 'Despliegue EXITOSO en AWS 🚀🔥'
        }
        failure {
            echo 'El despliegue falló ❌'
        }
    }
}
