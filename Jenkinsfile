pipeline {
    agent any

    environment {
        DOCKER_USER = 'quiroga148'
        EC2_HOST = '3.15.181.40'
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
                    bat "docker build -t ${DOCKER_USER}/gymetr-login:latest ."
                }
                dir('backend/GYMETR-Membership') {
                    bat "docker build -t ${DOCKER_USER}/gymetr-membership:latest ."
                }
                dir('backend/GYMETRA - Qr') {
                    bat "docker build -t ${DOCKER_USER}/gymetra-qr:latest ."
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
                bat "docker push ${DOCKER_USER}/gymetr-login:latest"
                bat "docker push ${DOCKER_USER}/gymetr-membership:latest"
                bat "docker push ${DOCKER_USER}/gymetra-qr:latest"
            }
        }

        stage('Deploy on AWS EC2') {
            steps {
                sshagent(['aws_ssh_key']) {
                    sh """
                    ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} '
                        mkdir -p ~/deploy &&
                        cd ~/deploy &&
                        echo "Descargando imagenes..." &&
                        docker compose -f docker-compose.aws.yml pull &&
                        docker compose -f docker-compose.aws.yml up -d --remove-orphans
                    '
                    """
                }
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
